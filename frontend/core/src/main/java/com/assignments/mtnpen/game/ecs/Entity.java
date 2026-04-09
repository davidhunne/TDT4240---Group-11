package com.example.penguinerace.game.ecs;

import java.util.Objects;

/**
 * Represents a single entity in the ECS.
 *
 * An entity is only an ID. All actual data is stored in components.
 */
public final class Entity {

    private final int id;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Entity)) return false;
        Entity other = (Entity) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "Entity{id=" + id + "}";
    }
}
