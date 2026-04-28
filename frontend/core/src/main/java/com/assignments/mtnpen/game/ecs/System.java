package com.example.penguinerace.game.ecs;

/**
 * Base class for all systems.
 *
 * A system contains game logic and processes entities
 * that have the required components.
 */
public abstract class System {

    protected final World world;

    public System(World world) {
        this.world = world;
    }
}
