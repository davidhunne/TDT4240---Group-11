export interface Player {
  id: string;
  displayName: string;
  createdAt: FirebaseFirestore.Timestamp;
  lastSeen: FirebaseFirestore.Timestamp;
  stats: PlayerStats;
}

export interface PlayerStats {
  gamesPlayed: number;
  gamesWon: number;
  totalScore: number;
  highScore: number;
}

export type GameStatus = "lobby" | "in_progress" | "finished";

export interface Game {
  id: string;
  status: GameStatus;
  hostId: string;
  players: GamePlayer[];
  currentTurnIndex: number;
  turnOrder: string[];
  boardState: Record<string, unknown>;
  createdAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
  finishedAt?: FirebaseFirestore.Timestamp;
}

export interface GamePlayer {
  playerId: string;
  displayName: string;
  position: { x: number; y: number };
  score: number;
  connected: boolean;
}

export interface Move {
  id: string;
  gameId: string;
  playerId: string;
  turnNumber: number;
  action: string;
  data: Record<string, unknown>;
  timestamp: FirebaseFirestore.Timestamp;
}

export interface LeaderboardEntry {
  playerId: string;
  displayName: string;
  highScore: number;
  gamesWon: number;
  gamesPlayed: number;
}
