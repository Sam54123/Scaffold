package org.scaffoldeditor.scaffold.level.entity;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;

public final class EntityRegistry {
	
	/**
	 * The registry of entity types.
	 * <br>
	 * (type name, entity factory)
	 */
	public static final Map<String, EntityFactory<Entity>> registry = new HashMap<>();
	
	/**
	 * Spawn an entity.
	 * @param registryName Registry name of the entity to spawn.
	 * @param level Level to spawn in.
	 * @param name Name to assign.
	 * @return Newly created entity.
	 */
	public static Entity createEntity(String registryName, Level level, String name) {
		return createEntity(registryName, level, name, false);
	}
	
	/**
	 * Spawn an entity.
	 * @param registryName Registry name of the entity to spawn.
	 * @param level Level to spawn in.
	 * @param name Name to assign.
	 * @param supressUpdate Don't call <code>onUpdateAttributes()</code> when spawning entity.
	 * @return Newly created entity.
	 */
	public static Entity createEntity(String registryName, Level level, String name, boolean supressUpdate) {
		if (!registry.containsKey(registryName)) {
			System.err.println("Unknown entity type: "+registryName);
			return null;
		}
		
		Entity entity = registry.get(registryName).create(level, name);
		entity.registryName = registryName;
		if (!supressUpdate) {
			entity.onUpdateAttributes();
		}
		return entity;
	}
}