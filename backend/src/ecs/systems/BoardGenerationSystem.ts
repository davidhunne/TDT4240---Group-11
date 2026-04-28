import { GameWorld } from "../GameWorld";
import {
  COMPONENTS,
  BoostComponent,
  ObstacleComponent,
  PositionComponent,
} from "../components";

export const BOARD_SIZE = 100;
const OBSTACLE_COUNT = 16;
const BOOST_COUNT = 28;
const DEFAULT_BOOST_AMOUNT = 10;
const FLAG_CLEAR_RADIUS = 6;

/**
 * Populates a fresh world with a 100x100 grid of obstacle and boost entities.
 * The spawn cell (0,0) is reserved so new players never land on a hazard.
 */
export const BoardGenerationSystem = {
  run(world: GameWorld): void {
    const used = new Set<string>();
    for (let px = 10; px <= 90; px += 20) {
      for (let py = 0; py <= 4; py++) {
        used.add(`${px},${py}`);
      }
    }

    // Reserve a clear zone around the finish flag so reaching it isn't blocked
    // by a wall of obstacles right at the goal.
    const flagX = Math.floor(BOARD_SIZE / 2);
    const flagY = BOARD_SIZE - 1;
    for (let dx = -FLAG_CLEAR_RADIUS; dx <= FLAG_CLEAR_RADIUS; dx++) {
      for (let dy = -FLAG_CLEAR_RADIUS; dy <= 1; dy++) {
        const x = flagX + dx;
        const y = flagY + dy;
        if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) continue;
        if (dx * dx + dy * dy <= FLAG_CLEAR_RADIUS * FLAG_CLEAR_RADIUS) {
          used.add(`${x},${y}`);
        }
      }
    }

    const place = (
      count: number,
      create: (x: number, y: number) => void,
    ): void => {
      let placed = 0;
      while (placed < count) {
        const x = Math.floor(Math.random() * BOARD_SIZE);
        const y = Math.floor(Math.random() * BOARD_SIZE);
        const key = `${x},${y}`;
        if (used.has(key)) continue;
        used.add(key);
        create(x, y);
        placed++;
      }
    };

    place(OBSTACLE_COUNT, (x, y) => {
      const e = world.createEntity();
      world.addComponent<PositionComponent>(e, COMPONENTS.Position, { x, y });
      world.addComponent<ObstacleComponent>(e, COMPONENTS.Obstacle, {
        blocks: true,
      });
    });

    place(BOOST_COUNT, (x, y) => {
      const e = world.createEntity();
      world.addComponent<PositionComponent>(e, COMPONENTS.Position, { x, y });
      world.addComponent<BoostComponent>(e, COMPONENTS.Boost, {
        amount: DEFAULT_BOOST_AMOUNT,
      });
    });
  },
};
