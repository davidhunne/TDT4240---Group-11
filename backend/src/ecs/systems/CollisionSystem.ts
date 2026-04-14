import { GameWorld } from "../GameWorld";
import {
  COMPONENTS,
  BoostComponent,
  PositionComponent,
  ScoreComponent,
} from "../components";
import { MovementSystem } from "./MovementSystem";

export interface CollisionResult {
  pickedBoost?: number;
}

/**
 * Resolves what happens when a player occupies a cell: boost pickups grant
 * score and are destroyed. Obstacles are handled upstream by MovementSystem,
 * so this system only needs to look at boost entities.
 */
export const CollisionSystem = {
  run(world: GameWorld, playerId: string): CollisionResult {
    const playerEntity = MovementSystem.findPlayerEntity(world, playerId);
    if (playerEntity === undefined) return {};

    const playerPos = world.getComponent<PositionComponent>(
      playerEntity,
      COMPONENTS.Position,
    );
    if (!playerPos) return {};

    const result: CollisionResult = {};

    for (const be of world.query(COMPONENTS.Boost, COMPONENTS.Position)) {
      const bp = world.getComponent<PositionComponent>(be, COMPONENTS.Position);
      if (!bp || bp.x !== playerPos.x || bp.y !== playerPos.y) continue;

      const boost = world.getComponent<BoostComponent>(be, COMPONENTS.Boost);
      const score = world.getComponent<ScoreComponent>(
        playerEntity,
        COMPONENTS.Score,
      );
      if (boost && score) {
        score.value += boost.amount;
        result.pickedBoost = boost.amount;
      }
      world.destroyEntity(be);
      break;
    }

    return result;
  },
};
