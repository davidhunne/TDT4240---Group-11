import { Router, Request, Response } from "express";
import {
  getOrCreatePlayer,
  getPlayer,
  updateDisplayName,
  getLeaderboard,
} from "../services/playerService";
import { NotFoundError, ValidationError } from "../errors";

const router = Router();

function requireString(value: unknown, field: string): string {
  if (typeof value !== "string" || value.length === 0) {
    throw new ValidationError(`${field} is required`);
  }
  return value;
}

// Register or re-identify a player (call on app launch)
router.post("/", async (req: Request, res: Response) => {
  const id = requireString(req.body?.id, "id");
  const displayName = requireString(req.body?.displayName, "displayName");
  const player = await getOrCreatePlayer(id, displayName);
  res.json(player);
});

// Get player by ID
router.get("/:id", async (req: Request, res: Response) => {
  const id = req.params.id as string;
  const player = await getPlayer(id);
  if (!player) throw new NotFoundError(`Player ${id} not found`);
  res.json(player);
});

// Update display name
router.patch("/:id", async (req: Request, res: Response) => {
  const id = req.params.id as string;
  const displayName = requireString(req.body?.displayName, "displayName");
  await updateDisplayName(id, displayName);
  res.json({ success: true });
});

// Leaderboard
router.get("/leaderboard/top", async (req: Request, res: Response) => {
  const rawLimit = parseInt(req.query.limit as string, 10);
  const limit = Number.isFinite(rawLimit) && rawLimit > 0 ? rawLimit : 10;
  const players = await getLeaderboard(limit);
  const leaderboard = players.map((p) => ({
    playerId: p.id,
    displayName: p.displayName,
    highScore: p.stats.highScore,
    gamesWon: p.stats.gamesWon,
    gamesPlayed: p.stats.gamesPlayed,
  }));
  res.json(leaderboard);
});

export default router;
