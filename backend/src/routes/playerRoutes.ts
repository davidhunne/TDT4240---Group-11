import { Router, Request, Response } from "express";
import {
  getOrCreatePlayer,
  getPlayer,
  updateDisplayName,
  getLeaderboard,
} from "../services/playerService";
import { NotFoundError } from "../errors";
import {
  leaderboardQuery,
  parseOrThrow,
  updateDisplayNameBody,
  upsertPlayerBody,
} from "../schemas";

const router = Router();

// Register or re-identify a player (call on app launch)
router.post("/", async (req: Request, res: Response) => {
  const { id, displayName } = parseOrThrow(upsertPlayerBody, req.body, "body");
  const player = await getOrCreatePlayer(id, displayName);
  res.json(player);
});

// Leaderboard
router.get("/leaderboard/top", async (req: Request, res: Response) => {
  const { limit } = parseOrThrow(leaderboardQuery, req.query, "query");
  const players = await getLeaderboard(limit ?? 10);
  const leaderboard = players.map((p) => ({
    playerId: p.id,
    displayName: p.displayName,
    highScore: p.stats.highScore,
    gamesWon: p.stats.gamesWon,
    gamesPlayed: p.stats.gamesPlayed,
  }));
  res.json(leaderboard);
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
  const { displayName } = parseOrThrow(updateDisplayNameBody, req.body, "body");
  await updateDisplayName(id, displayName);
  res.json({ success: true });
});

export default router;
