package org.metaversemedia.scaffold.level.entity;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.math.Vector;

/**
 * Base entity class in maps
 * @author Sam54123
 *
 */
public class Entity {
	/* Position of the entity in world space */
	private Vector position;
	
	/* Name of the entity */
	private String name;
	
	/* The level this entity belongs to */
	private Level level;
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	
	/**
	 * Construct a new entity with a name and a level.
	 * @param level	Level entity should belong to.
	 * @param name Entity name
	 */
	public Entity(Level level, String name) {
		this.name = name;
		this.level = level;
	}
	
	/**
	 * Get this entity's name.
	 * @return Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set this entity's name. (DON'T CALL MANUALLY!)
	 * @param name New name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get this entity's world position.
	 * @return Position
	 */
	public Vector getPosition() {
		return position;
	}
	
	/**
	 * Set this entity's world position.
	 * Called on entity creation.
	 * @param position New position
	 */
	public void setPosition(Vector position) {
		this.position = position;
	}
	
	/**
	 * Get the level this entity belongs to.
	 * @return Level
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Get a map of this entity's attributes.
	 * @return Attributes
	 */
	public Map<String, Object> attributes() {
		return attributes;
	}
	
	
	/**
	 * Set an attribute by name
	 * @param name Attribute name.
	 * @param value Attribute value.
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	
	/**
	 * Get an attribute by name
	 * @param name Attribute
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	
	/**
	 * Serialize this entity into a JSON object.
	 * @return Serialized entity
	 */
	public JSONObject serialize() {
		// Create object
		JSONObject object = new JSONObject();
		
		// Basic information
		object.put("type", getClass().getName());
		object.put("position", position.toJSONArray());
		
		// Attributes
		JSONObject attributeObject = new JSONObject();
		
		for (String key : attributes.keySet()) {
			if (attributes.get(key) != null) {
				attributeObject.put(key, attributes.get(key));
			}
		}
		
		object.put("attributes", attributeObject);
		
		return object;
	}

	
	/**
	 * Unserialize an entity fom a JSON object.
	 * @param level Level this entity should belong to
	 * @param name Name of the entity
	 * @param object JSON object to unserialize from
	 * @return Unserialized entity
	 */
	public static Entity unserialize(Level level, String name, JSONObject object) {
		try {
			// Create object
			Entity entity = new Entity(level, name);
			
			// Basic info
			entity.setPosition(Vector.fromJSONArray(object.getJSONArray("position")));
			
			// Attributes
			JSONObject attributes = object.getJSONObject("attributes");
			
			for (String key : attributes.keySet()) {
				Object attribute = attributes.get(key);
				entity.attributes().put(key, attribute);
			}
			
			entity.onUnserialized(object);
			return entity;
		} catch (JSONException e) {
			System.out.println("Improperly formatted entity: "+name);
			return null;
		}
		
	}
	
	/**
	 * Called when entity is unserialized for subclasses to act on.
	 * @param object JSONObject serialized from.
	 */
	public void onUnserialized(JSONObject object) {}
	
	/**
	 * Compile this entity's logic.
	 * @param logicFolder Folder comtaining level's function files.
	 * @return Success
	 */
	public boolean compileLogic(Path logicFolder) {
		return true;
	}
}
