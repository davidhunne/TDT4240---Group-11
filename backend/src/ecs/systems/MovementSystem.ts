import { GameWorld, EntityId } from "../GameWorld";
import {
  COMPONENTS,
  PlayerComponent,
  PositionComponent,
} from "../components";
import { BOARD_SIZE } from "./BoardGenerationSystem";

export interface MoveResult {
  success: boolean;
  reason?: "out_of_bounds" | "blocked" | "unknown_player";
  position?: PositionComponent;
}

function findPlayerEntity(
  world: GameWorld,
  playerId: string,
): EntityId | undefined {
  for (const e of world.query(COMPONENTS.Player, COMPONENTS.Position)) {
    const p = world.getComponent<PlayerComponent>(e, COMPONENTS.Player);
    if (p?.playerId === playerId) return e;
  }
  return undefined;
}

/**
 * Moves a player entity to a target cell if the cell is in bounds and not
 * occupied by an obstacle. Returns the result so callers can surface errors.
 */
export const MovementSystem = {
  findPlayerEntity,

  move(
    world: GameWorld,
    playerId: string,
    to: PositionComponent,
  ): MoveResult {
    const entity = findPlayerEntity(world, playerId);
    if (entity === undefined) {
      return { success: false, reason: "unknown_player" };
    }

    if (to.x < 0 || to.y < 0 || to.x >= BOARD_SIZE || to.y >= BOARD_SIZE) {
      return { success: false, reason: "out_of_bounds" };
    }

    for (const oe of world.query(COMPONENTS.Obstacle, COMPONENTS.Position)) {
      const pos = world.getComponent<PositionComponent>(
        oe,
        COMPONENTS.Position,
      );
      if (pos && pos.x === to.x && pos.y === to.y) {
        return { success: false, reason: "blocked" };
      }
    }

    world.addComponent<PositionComponent>(entity, COMPONENTS.Position, {
      x: to.x,
      y: to.y,
    });
    return { success: true, position: { x: to.x, y: to.y } };
  },
};
