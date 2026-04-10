import { ComponentName } from "./components";

export type EntityId = number;

/**
 * A per-game ECS world. Entities are opaque integer ids, components are
 * stored in per-component maps (archetype-lite), and systems are plain
 * functions that query the world.
 */
export class GameWorld {
  readonly gameId: string;
  private nextEntityId = 1;
  private stores: Map<ComponentName, Map<EntityId, unknown>> = new Map();
  private entities: Set<EntityId> = new Set();

  constructor(gameId: string) {
    this.gameId = gameId;
  }

  createEntity(): EntityId {
    const id = this.nextEntityId++;
    this.entities.add(id);
    return id;
  }

  destroyEntity(entity: EntityId): void {
    this.entities.delete(entity);
    for (const store of this.stores.values()) {
      store.delete(entity);
    }
  }

  addComponent<T>(entity: EntityId, name: ComponentName, data: T): void {
    let store = this.stores.get(name);
    if (!store) {
      store = new Map();
      this.stores.set(name, store);
    }
    store.set(entity, data);
  }

  getComponent<T>(entity: EntityId, name: ComponentName): T | undefined {
    return this.stores.get(name)?.get(entity) as T | undefined;
  }

  hasComponent(entity: EntityId, name: ComponentName): boolean {
    return this.stores.get(name)?.has(entity) ?? false;
  }

  removeComponent(entity: EntityId, name: ComponentName): void {
    this.stores.get(name)?.delete(entity);
  }

  /**
   * Return every entity that carries all of the given components.
   * Iterates the smallest store for efficiency.
   */
  query(...names: ComponentName[]): EntityId[] {
    if (names.length === 0) return [];
    let smallest: Map<EntityId, unknown> | undefined;
    for (const name of names) {
      const store = this.stores.get(name);
      if (!store) return [];
      if (!smallest || store.size < smallest.size) smallest = store;
    }
    const result: EntityId[] = [];
    for (const id of smallest!.keys()) {
      let ok = true;
      for (const name of names) {
        if (!this.stores.get(name)!.has(id)) {
          ok = false;
          break;
        }
      }
      if (ok) result.push(id);
    }
    return result;
  }

  allEntities(): EntityId[] {
    return Array.from(this.entities);
  }
}
