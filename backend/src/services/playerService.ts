import { db } from "../config/firebase";
import { Player, PlayerStats } from "../types";
import { FieldValue, Timestamp } from "firebase-admin/firestore";

const PLAYERS = "players";

const defaultStats: PlayerStats = {
  gamesPlayed: 0,
  gamesWon: 0,
  totalScore: 0,
  highScore: 0,
};

export async function createPlayer(
  id: string,
  displayName: string
): Promise<Player> {
  const now = Timestamp.now();
  const player: Player = {
    id,
    displayName,
    createdAt: now,
    lastSeen: now,
    stats: { ...defaultStats },
  };
  await db.collection(PLAYERS).doc(id).set(player);
  return player;
}

export async function getPlayer(id: string): Promise<Player | null> {
  const doc = await db.collection(PLAYERS).doc(id).get();
  if (!doc.exists) return null;
  return doc.data() as Player;
}

export async function getOrCreatePlayer(
  id: string,
  displayName: string
): Promise<Player> {
  const existing = await getPlayer(id);
  if (existing) {
    await db
      .collection(PLAYERS)
      .doc(id)
      .update({ lastSeen: FieldValue.serverTimestamp() });
    return { ...existing, lastSeen: Timestamp.now() };
  }
  return createPlayer(id, displayName);
}

export async function updateDisplayName(
  id: string,
  displayName: string
): Promise<void> {
  await db.collection(PLAYERS).doc(id).update({ displayName });
}

export async function updatePlayerStats(
  id: string,
  score: number,
  won: boolean
): Promise<void> {
  const player = await getPlayer(id);
  if (!player) return;

  const stats = player.stats;
  stats.gamesPlayed += 1;
  stats.totalScore += score;
  if (won) stats.gamesWon += 1;
  if (score > stats.highScore) stats.highScore = score;

  await db.collection(PLAYERS).doc(id).update({ stats });
}

export async function getLeaderboard(
  limit: number = 10
): Promise<Player[]> {
  const snapshot = await db
    .collection(PLAYERS)
    .orderBy("stats.highScore", "desc")
    .limit(limit)
    .get();

  return snapshot.docs.map((doc) => doc.data() as Player);
}
