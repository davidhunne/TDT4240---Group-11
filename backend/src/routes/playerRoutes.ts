import { Router, Request, Response } from "express";
import {
  getOrCreatePlayer,
  getPlayer,
  updateDisplayName,
  getLeaderboard,
} from "../services/playerService";

const router = Router();

// Register or re-identify a player (call on app launch)
router.post("/", async (req: Request, res: Response) => {
  const { id, displayName } = req.body;
  if (!id || !displayName) {
    res.status(400).json({ error: "id and displayName are required" });
    return;
  }

  const player = await getOrCreatePlayer(id, displayName);
  res.json(player);
});

// Get player by ID
router.get("/:id", async (req: Request, res: Response) => {
  const player = await getPlayer(req.params.id as string);
  if (!player) {
    res.status(404).json({ error: "Player not found" });
    return;
  }
  res.json(player);
});

// Update display name
router.patch("/:id", async (req: Request, res: Response) => {
  const { displayName } = req.body;
  if (!displayName) {
    res.status(400).json({ error: "displayName is required" });
    return;
  }
  await updateDisplayName(req.params.id as string, displayName);
  res.json({ success: true });
});

// Leaderboard
router.get("/leaderboard/top", async (req: Request, res: Response) => {
  const limit = parseInt(req.query.limit as string) || 10;
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
