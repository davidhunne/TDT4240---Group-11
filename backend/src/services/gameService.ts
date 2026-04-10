import { db } from "../config/firebase";
import { Game, GamePlayer, Move } from "../types";
import { v4 as uuidv4 } from "uuid";
import { Timestamp } from "firebase-admin/firestore";
import { updatePlayerStats } from "./playerService";
import {
  World,
  MovementSystem,
  CollisionSystem,
  PositionComponent,
} from "../ecs";

const GAMES = "games";
const MOVES = "moves";

const BOARD_SIZE = 100;
const OBSTACLE_COUNT = 500;
const BOOST_COUNT = 200;

function generateBoard(): Record<string, unknown> {
  const cells: Record<string, "obstacle" | "boost"> = {};
  const used = new Set<string>();
  // Reserve the spawn cell so players never start on an obstacle/boost.
  used.add("0,0");

  const placeRandom = (type: "obstacle" | "boost", count: number) => {
    let placed = 0;
    while (placed < count) {
      const x = Math.floor(Math.random() * BOARD_SIZE);
      const y = Math.floor(Math.random() * BOARD_SIZE);
      const key = `${x},${y}`;
      if (used.has(key)) continue;
      used.add(key);
      cells[key] = type;
      placed++;
    }
  };

  placeRandom("obstacle", OBSTACLE_COUNT);
  placeRandom("boost", BOOST_COUNT);

  return {
    width: BOARD_SIZE,
    height: BOARD_SIZE,
    cells,
  };
}

export async function createGame(
  hostId: string,
  hostDisplayName: string,
): Promise<Game> {
  const id = uuidv4();
  const now = Timestamp.now();

  const hostPlayer: GamePlayer = {
    playerId: hostId,
    displayName: hostDisplayName,
    position: { x: 0, y: 0 },
    score: 0,
    connected: true,
  };

  // Spin up an ECS world for this game through the singleton. The world
  // generates a 100x100 board with obstacles and boosts, then we add the host
  // as a player entity.
  const world = World.getInstance();
  const ecs = world.createGame(id);
  world.spawnPlayer(ecs, hostPlayer);

  const game: Game = {
    id,
    status: "lobby",
    hostId,
    players: [hostPlayer],
    currentTurnIndex: 0,
    turnOrder: [hostId],
    boardState: world.serializeBoard(ecs) as unknown as Record<string, unknown>,
    createdAt: now,
    updatedAt: now,
  };

  await db.collection(GAMES).doc(id).set(game);
  return game;
}

export async function getGame(id: string): Promise<Game | null> {
  const doc = await db.collection(GAMES).doc(id).get();
  if (!doc.exists) return null;
  return doc.data() as Game;
}

export async function listLobbies(): Promise<Game[]> {
  const snapshot = await db
    .collection(GAMES)
    .where("status", "==", "lobby")
    .orderBy("createdAt", "desc")
    .limit(20)
    .get();

  return snapshot.docs.map((doc) => doc.data() as Game);
}

export async function joinGame(
  gameId: string,
  playerId: string,
  displayName: string,
): Promise<Game> {
  const game = await getGame(gameId);
  if (!game) throw new Error("Game not found");
  if (game.status !== "lobby") throw new Error("Game is not in lobby state");

  const alreadyJoined = game.players.some((p) => p.playerId === playerId);
  if (alreadyJoined) return game;

  const newPlayer: GamePlayer = {
    playerId,
    displayName,
    position: { x: 0, y: 0 },
    score: 0,
    connected: true,
  };

  game.players.push(newPlayer);
  game.turnOrder.push(playerId);
  game.updatedAt = Timestamp.now();

  // Mirror the new player into the ECS world.
  const world = World.getInstance();
  const ecs = world.getOrHydrate(game);
  world.spawnPlayer(ecs, newPlayer);

  await db.collection(GAMES).doc(gameId).set(game);
  return game;
}

export async function leaveGame(
  gameId: string,
  playerId: string,
): Promise<Game> {
  const game = await getGame(gameId);
  if (!game) throw new Error("Game not found");

  if (game.status === "lobby") {
    game.players = game.players.filter((p) => p.playerId !== playerId);
    game.turnOrder = game.turnOrder.filter((id) => id !== playerId);

    if (game.players.length === 0) {
      await db.collection(GAMES).doc(gameId).delete();
      World.getInstance().deleteGame(gameId);
      return game;
    }

    if (game.hostId === playerId) {
      game.hostId = game.players[0].playerId;
    }
  } else if (game.status === "in_progress") {
    const player = game.players.find((p) => p.playerId === playerId);
    if (player) player.connected = false;
  }

  game.updatedAt = Timestamp.now();
  await db.collection(GAMES).doc(gameId).set(game);
  return game;
}

export async function startGame(gameId: string, hostId: string): Promise<Game> {
  const game = await getGame(gameId);
  if (!game) throw new Error("Game not found");
  if (game.hostId !== hostId)
    throw new Error("Only the host can start the game");
  if (game.status !== "lobby") throw new Error("Game is not in lobby state");
  if (game.players.length < 2)
    throw new Error("Need at least 2 players to start");

  game.status = "in_progress";
  game.currentTurnIndex = 0;
  game.updatedAt = Timestamp.now();

  await db.collection(GAMES).doc(gameId).set(game);
  return game;
}

export async function submitMove(
  gameId: string,
  playerId: string,
  action: string,
  data: Record<string, unknown>,
): Promise<{ game: Game; move: Move }> {
  const game = await getGame(gameId);
  if (!game) throw new Error("Game not found");
  if (game.status !== "in_progress") throw new Error("Game is not in progress");

  const currentPlayerId = game.turnOrder[game.currentTurnIndex];
  if (currentPlayerId !== playerId) throw new Error("Not your turn");

  const world = World.getInstance();
  const ecs = world.getOrHydrate(game);

  const moveId = uuidv4();
  const move: Move = {
    id: moveId,
    gameId,
    playerId,
    turnNumber: game.currentTurnIndex,
    action,
    data,
    timestamp: Timestamp.now(),
  };

  // Run movement through the ECS movement system so obstacle checks and
  // bounds validation stay out of this service.
  if (action === "move" && data.position && typeof data.position === "object") {
    const pos = data.position as Partial<PositionComponent>;
    if (typeof pos.x === "number" && typeof pos.y === "number") {
      const result = MovementSystem.move(ecs, playerId, { x: pos.x, y: pos.y });
      if (!result.success) {
        throw new Error(`Invalid move: ${result.reason ?? "unknown"}`);
      }
      const collision = CollisionSystem.run(ecs, playerId);
      move.data = {
        ...data,
        resolved: {
          position: result.position,
          pickedBoost: collision.pickedBoost ?? null,
        },
      };
    }
  }

  await db.collection(MOVES).doc(moveId).set(move);

  // Mirror ECS state back into the Game document.
  world.syncPlayersToGame(ecs, game);
  game.boardState = world.serializeBoard(ecs) as unknown as Record<
    string,
    unknown
  >;

  // Advance turn
  game.currentTurnIndex = (game.currentTurnIndex + 1) % game.turnOrder.length;
  game.updatedAt = Timestamp.now();

  await db.collection(GAMES).doc(gameId).set(game);
  return { game, move };
}

export async function endGame(
  gameId: string,
  winnerId?: string,
): Promise<Game> {
  const game = await getGame(gameId);
  if (!game) throw new Error("Game not found");

  game.status = "finished";
  game.finishedAt = Timestamp.now();
  game.updatedAt = Timestamp.now();

  await db.collection(GAMES).doc(gameId).set(game);

  // Drop the in-memory ECS world for the finished game.
  World.getInstance().deleteGame(gameId);

  // Update player stats
  for (const player of game.players) {
    const won = player.playerId === winnerId;
    await updatePlayerStats(player.playerId, player.score, won);
  }

  return game;
}

export async function getGameMoves(gameId: string): Promise<Move[]> {
  const snapshot = await db
    .collection(MOVES)
    .where("gameId", "==", gameId)
    .orderBy("timestamp", "asc")
    .get();

  return snapshot.docs.map((doc) => doc.data() as Move);
}

export async function updateConnectionState(
  gameId: string,
  playerId: string,
  connected: boolean,
): Promise<void> {
  const game = await getGame(gameId);
  if (!game) return;

  const player = game.players.find((p) => p.playerId === playerId);
  if (player) {
    player.connected = connected;
    game.updatedAt = Timestamp.now();
    await db.collection(GAMES).doc(gameId).set(game);
  }
}
