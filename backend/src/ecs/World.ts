import { GameWorld, EntityId } from "./GameWorld";
import {
  COMPONENTS,
  BoostComponent,
  ObstacleComponent,
  PlayerComponent,
  PositionComponent,
  ScoreComponent,
} from "./components";
import { BoardGenerationSystem, BOARD_SIZE } from "./systems/BoardGenerationSystem";
import { Game, GamePlayer } from "../types";

/**
 * Serialized board layout stored in Firestore under Game.boardState.
 * Only immutable-ish board tiles live here — player state is tracked in the
 * existing Game.players array so the external schema does not change.
 */
export interface SerializedBoard {
  width: number;
  height: number;
  obstacles: Array<[number, number]>;
  boosts: Array<[number, number, number]>; // x, y, amount
}

export function isSerializedBoard(
  value: unknown,
): value is SerializedBoard {
  if (!value || typeof value !== "object") return false;
  const v = value as Partial<SerializedBoard>;
  return (
    typeof v.width === "number" &&
    typeof v.height === "number" &&
    Array.isArray(v.obstacles) &&
    Array.isArray(v.boosts)
  );
}

/**
 * Singleton registry of per-game ECS worlds. Express workers are long-lived,
 * so keeping live worlds in memory lets systems run without re-reading
 * Firestore on every request. On cache miss we rehydrate from a stored
 * snapshot.
 */
export class World {
  private static instance: World | undefined;
  private games: Map<string, GameWorld> = new Map();

  private constructor() {}

  static getInstance(): World {
    if (!World.instance) World.instance = new World();
    return World.instance;
  }

  /** Test/utility only — drops all in-memory state. */
  static reset(): void {
    World.instance = undefined;
  }

  /**
   * Create a brand new ECS world for a game, populate it with a generated
   * board, and register it.
   */
  createGame(gameId: string): GameWorld {
    const world = new GameWorld(gameId);
    BoardGenerationSystem.run(world);
    this.games.set(gameId, world);
    return world;
  }

  getGame(gameId: string): GameWorld | undefined {
    return this.games.get(gameId);
  }

  deleteGame(gameId: string): void {
    this.games.delete(gameId);
  }

  /**
   * Hydrate an ECS world from a persisted Game document. Used when an
   * operation arrives for a game that is not currently in the in-memory
   * registry (e.g. after a server restart).
   */
  hydrate(game: Game): GameWorld {
    const world = new GameWorld(game.id);

    if (isSerializedBoard(game.boardState)) {
      for (const [x, y] of game.boardState.obstacles) {
        const e = world.createEntity();
        world.addComponent<PositionComponent>(e, COMPONENTS.Position, { x, y });
        world.addComponent<ObstacleComponent>(e, COMPONENTS.Obstacle, {
          blocks: true,
        });
      }
      for (const [x, y, amount] of game.boardState.boosts) {
        const e = world.createEntity();
        world.addComponent<PositionComponent>(e, COMPONENTS.Position, { x, y });
        world.addComponent<BoostComponent>(e, COMPONENTS.Boost, { amount });
      }
    }

    for (const player of game.players) {
      this.spawnPlayer(world, player);
    }

    this.games.set(game.id, world);
    return world;
  }

  /**
   * Get the live ECS world for a game, hydrating from a Firestore document
   * if needed.
   */
  getOrHydrate(game: Game): GameWorld {
    return this.games.get(game.id) ?? this.hydrate(game);
  }

  /**
   * Create a player entity inside a world. Idempotent: if the player already
   * has an entity, its components are updated in place.
   */
  spawnPlayer(world: GameWorld, player: GamePlayer): EntityId {
    for (const e of world.query(COMPONENTS.Player)) {
      const p = world.getComponent<PlayerComponent>(e, COMPONENTS.Player);
      if (p?.playerId === player.playerId) {
        world.addComponent<PositionComponent>(e, COMPONENTS.Position, {
          x: player.position.x,
          y: player.position.y,
        });
        world.addComponent<ScoreComponent>(e, COMPONENTS.Score, {
          value: player.score,
        });
        p.displayName = player.displayName;
        p.connected = player.connected;
        return e;
      }
    }

    const entity = world.createEntity();
    world.addComponent<PlayerComponent>(entity, COMPONENTS.Player, {
      playerId: player.playerId,
      displayName: player.displayName,
      connected: player.connected,
    });
    world.addComponent<PositionComponent>(entity, COMPONENTS.Position, {
      x: player.position.x,
      y: player.position.y,
    });
    world.addComponent<ScoreComponent>(entity, COMPONENTS.Score, {
      value: player.score,
    });
    return entity;
  }

  /**
   * Flatten the ECS world's tiles back into a SerializedBoard for Firestore.
   * Player entities are excluded — they are persisted via Game.players.
   */
  serializeBoard(world: GameWorld): SerializedBoard {
    const obstacles: Array<[number, number]> = [];
    for (const e of world.query(COMPONENTS.Obstacle, COMPONENTS.Position)) {
      const pos = world.getComponent<PositionComponent>(e, COMPONENTS.Position);
      if (pos) obstacles.push([pos.x, pos.y]);
    }

    const boosts: Array<[number, number, number]> = [];
    for (const e of world.query(COMPONENTS.Boost, COMPONENTS.Position)) {
      const pos = world.getComponent<PositionComponent>(e, COMPONENTS.Position);
      const boost = world.getComponent<BoostComponent>(e, COMPONENTS.Boost);
      if (pos && boost) boosts.push([pos.x, pos.y, boost.amount]);
    }

    return {
      width: BOARD_SIZE,
      height: BOARD_SIZE,
      obstacles,
      boosts,
    };
  }

  /**
   * Pull the mutable per-player state out of the ECS world so callers can
   * persist it back into Game.players.
   */
  syncPlayersToGame(world: GameWorld, game: Game): void {
    for (const gp of game.players) {
      for (const e of world.query(
        COMPONENTS.Player,
        COMPONENTS.Position,
        COMPONENTS.Score,
      )) {
        const tag = world.getComponent<PlayerComponent>(e, COMPONENTS.Player);
        if (tag?.playerId !== gp.playerId) continue;
        const pos = world.getComponent<PositionComponent>(
          e,
          COMPONENTS.Position,
        );
        const score = world.getComponent<ScoreComponent>(e, COMPONENTS.Score);
        if (pos) gp.position = { x: pos.x, y: pos.y };
        if (score) gp.score = score.value;
        gp.connected = tag.connected;
        break;
      }
    }
  }
}
