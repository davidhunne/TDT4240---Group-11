import { Router, Request, Response } from "express";
import {
  createGame,
  getGame,
  listLobbies,
  joinGame,
  leaveGame,
  startGame,
  submitMove,
  endGame,
  getGameMoves,
  updateConnectionState,
} from "../services/gameService";
import { NotFoundError, ValidationError } from "../errors";

const router = Router();

function requireString(value: unknown, field: string): string {
  if (typeof value !== "string" || value.length === 0) {
    throw new ValidationError(`${field} is required`);
  }
  return value;
}

// List open lobbies
router.get("/lobbies", async (_req: Request, res: Response) => {
  const lobbies = await listLobbies();
  res.json(lobbies);
});

// Create a new game
router.post("/", async (req: Request, res: Response) => {
  const playerId = requireString(req.body?.playerId, "playerId");
  const displayName = requireString(req.body?.displayName, "displayName");
  const game = await createGame(playerId, displayName);
  res.status(201).json(game);
});

// Get game state
router.get("/:gameId", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const game = await getGame(gameId);
  if (!game) throw new NotFoundError(`Game ${gameId} not found`);
  res.json(game);
});

// Join a game
router.post("/:gameId/join", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const playerId = requireString(req.body?.playerId, "playerId");
  const displayName = requireString(req.body?.displayName, "displayName");
  const game = await joinGame(gameId, playerId, displayName);
  res.json(game);
});

// Leave a game
router.post("/:gameId/leave", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const playerId = requireString(req.body?.playerId, "playerId");
  const game = await leaveGame(gameId, playerId);
  res.json(game);
});

// Start the game
router.post("/:gameId/start", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const playerId = requireString(req.body?.playerId, "playerId");
  const game = await startGame(gameId, playerId);
  res.json(game);
});

// Submit a move
router.post("/:gameId/move", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const playerId = requireString(req.body?.playerId, "playerId");
  const action = requireString(req.body?.action, "action");
  const data = (req.body?.data ?? {}) as Record<string, unknown>;
  const result = await submitMove(gameId, playerId, action, data);
  res.json(result);
});

// End the game
router.post("/:gameId/end", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const { winnerId } = req.body ?? {};
  const game = await endGame(gameId, winnerId);
  res.json(game);
});

// Get move history for a game
router.get("/:gameId/moves", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const moves = await getGameMoves(gameId);
  res.json(moves);
});

// Update connection state
router.post("/:gameId/connection", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const playerId = requireString(req.body?.playerId, "playerId");
  const { connected } = req.body ?? {};
  if (typeof connected !== "boolean") {
    throw new ValidationError("connected must be a boolean");
  }
  await updateConnectionState(gameId, playerId, connected);
  res.json({ success: true });
});

export default router;
