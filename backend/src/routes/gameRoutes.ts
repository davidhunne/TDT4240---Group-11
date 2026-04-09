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

const router = Router();

// List open lobbies
router.get("/lobbies", async (_req: Request, res: Response) => {
  const lobbies = await listLobbies();
  res.json(lobbies);
});

// Create a new game
router.post("/", async (req: Request, res: Response) => {
  const { playerId, displayName } = req.body;
  if (!playerId || !displayName) {
    res.status(400).json({ error: "playerId and displayName are required" });
    return;
  }
  const game = await createGame(playerId, displayName);
  res.status(201).json(game);
});

// Get game state
router.get("/:gameId", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const game = await getGame(gameId);
  if (!game) {
    res.status(404).json({ error: "Game not found" });
    return;
  }
  res.json(game);
});

// Join a game
router.post("/:gameId/join", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const { playerId, displayName } = req.body;
  if (!playerId || !displayName) {
    res.status(400).json({ error: "playerId and displayName are required" });
    return;
  }
  try {
    const game = await joinGame(gameId, playerId, displayName);
    res.json(game);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// Leave a game
router.post("/:gameId/leave", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const { playerId } = req.body;
  if (!playerId) {
    res.status(400).json({ error: "playerId is required" });
    return;
  }
  try {
    const game = await leaveGame(gameId, playerId);
    res.json(game);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// Start the game
router.post("/:gameId/start", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const { playerId } = req.body;
  if (!playerId) {
    res.status(400).json({ error: "playerId is required" });
    return;
  }
  try {
    const game = await startGame(gameId, playerId);
    res.json(game);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// Submit a move
router.post("/:gameId/move", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const { playerId, action, data } = req.body;
  if (!playerId || !action) {
    res.status(400).json({ error: "playerId and action are required" });
    return;
  }
  try {
    const result = await submitMove(gameId, playerId, action, data || {});
    res.json(result);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// End the game
router.post("/:gameId/end", async (req: Request, res: Response) => {
  const gameId = req.params.gameId as string;
  const { winnerId } = req.body;
  try {
    const game = await endGame(gameId, winnerId);
    res.json(game);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
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
  const { playerId, connected } = req.body;
  if (!playerId || connected === undefined) {
    res.status(400).json({ error: "playerId and connected are required" });
    return;
  }
  await updateConnectionState(gameId, playerId, connected);
  res.json({ success: true });
});

export default router;
