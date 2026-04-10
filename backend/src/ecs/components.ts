// Component type identifiers. Using string keys keeps the component store
// a simple Map<string, Map<EntityId, T>> without any decorator magic.
export const COMPONENTS = {
  Position: "Position",
  Player: "Player",
  Score: "Score",
  Obstacle: "Obstacle",
  Boost: "Boost",
} as const;

export type ComponentName = (typeof COMPONENTS)[keyof typeof COMPONENTS];

export interface PositionComponent {
  x: number;
  y: number;
}

export interface PlayerComponent {
  playerId: string;
  displayName: string;
  connected: boolean;
}

export interface ScoreComponent {
  value: number;
}

export interface ObstacleComponent {
  blocks: boolean;
}

export interface BoostComponent {
  amount: number;
}
