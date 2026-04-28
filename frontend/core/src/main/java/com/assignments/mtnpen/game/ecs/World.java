package com.example.penguinerace.game.ecs;

import java.util.*;

/**
 * Central ECS manager.
 *
 * The World stores:
 * - entities
 * - components attached to entities
 * - systems
 *
 * It also provides helper methods for querying entities based on which components they have.
 */
public class World {

    private int nextEntityId = 0;

    private final Map<Integer, Entity> entities = new HashMap<>();
    private final Map<Class<? extends Component>, Map<Integer, Component>> componentStores =
        new HashMap<>();
    private final List<System> systems = new ArrayList<>();

    /**
     * Creates a new entity and registers it in the world.
     *
     * @return the created Entity
     */
    public Entity createEntity() {
        Entity entity = new Entity(nextEntityId++);
        entities.put(entity.getId(), entity);
        return entity;
    }

    /**
     * Removes an entity and all its components from the world.
     *
     * @param entity the entity to remove
     */
    public void removeEntity(Entity entity) {
        if (entity == null) return;

        entities.remove(entity.getId());

        for (Map<Integer, Component> store : componentStores.values()) {
            store.remove(entity.getId());
        }
    }

    /**
     * Adds a component to an entity.
     *
     * If the entity already has a component of the same type,
     * it is replaced.
     *
     * @param entity the entity
     * @param component the component to add
     * @param <T> component type
     */
    public <T extends Component> void addComponent(Entity entity, T component) {
        if (entity == null || component == null) {
            throw new IllegalArgumentException("Entity and component must not be null.");
        }

        if (!entities.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity is not registered in this world.");
        }

        componentStores
            .computeIfAbsent(component.getClass(), key -> new HashMap<>())
            .put(entity.getId(), component);
    }

    /**
     * Gets a component of the given type from an entity.
     *
     * @param entity the entity
     * @param componentType the component class
     * @param <T> component type
     * @return the component, or null if not found
     */
    public <T extends Component> T getComponent(Entity entity, Class<T> componentType) {
        if (entity == null || componentType == null) return null;

        Map<Integer, Component> store = componentStores.get(componentType);
        if (store == null) return null;

        Component component = store.get(entity.getId());
        if (component == null) return null;

        return componentType.cast(component);
    }

    /**
     * Returns true if the entity has a component of the given type.
     *
     * @param entity the entity
     * @param componentType the component class
     * @param <T> component type
     * @return true if present, false otherwise
     */
    public <T extends Component> boolean hasComponent(Entity entity, Class<T> componentType) {
        if (entity == null || componentType == null) return false;

        Map<Integer, Component> store = componentStores.get(componentType);
        return store != null && store.containsKey(entity.getId());
    }

    /**
     * Removes a component of the given type from an entity.
     *
     * @param entity the entity
     * @param componentType the component class
     * @param <T> component type
     */
    public <T extends Component> void removeComponent(Entity entity, Class<T> componentType) {
        if (entity == null || componentType == null) return;

        Map<Integer, Component> store = componentStores.get(componentType);
        if (store != null) {
            store.remove(entity.getId());
        }
    }

    /**
     * Returns all currently registered entities.
     *
     * @return a copy of all entities
     */
    public Set<Entity> getAllEntities() {
        return new HashSet<>(entities.values());
    }

    /**
     * Returns all entities that have every component type listed.
     *
     * @param componentTypes required component classes
     * @return set of matching entities
     */
    @SafeVarargs
    public final Set<Entity> getEntitiesWith(Class<? extends Component>... componentTypes) {
        Set<Entity> result = new HashSet<>();

        for (Entity entity : entities.values()) {
            boolean matches = true;

            for (Class<? extends Component> componentType : componentTypes) {
                Map<Integer, Component> store = componentStores.get(componentType);

                if (store == null || !store.containsKey(entity.getId())) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                result.add(entity);
            }
        }

        return result;
    }

    /**
     * Adds a system to the world.
     *
     * Systems are updated in the order they are added.
     *
     * @param system the system to add
     */
    public void addSystem(System system) {
        if (system == null) {
            throw new IllegalArgumentException("System must not be null.");
        }
        systems.add(system);
    }

    /**
     * Returns the list of systems currently registered.
     *
     * @return copy of systems list
     */
    public List<System> getSystems() {
        return new ArrayList<>(systems);
    }
}
