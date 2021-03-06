package org.scaffoldeditor.scaffold.level.entity;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.FloatAttribute;

/**
 * Defines an entity that can be rotated on the X and Y axis
 * @author Igrium
 *
 */
public abstract class Rotatable extends Entity {

	public Rotatable(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public Map<String, Attribute<?>> getDefaultAttributes() {
		Map<String, Attribute<?>> map = new HashMap<>();
		map.put("rotX", new FloatAttribute(0));
		map.put("rotY", new FloatAttribute(0));
		return map;
	}
	
	/**
	 * Get the entity's X rotation.
	 * @return X Degrees
	 */
	public float rotX() {
		return ((FloatAttribute) getAttribute("rotX")).getValue();
	}
	
	/**
	 * Get the entity's Y rotation.
	 * @return Y Degrees
	 */
	public float rotY() {
		return ((FloatAttribute) getAttribute("rotY")).getValue();
	}
	
	/**
	 * Set the entity's X rotation.
	 * @param deg X Degrees
	 */
	public void setRotX(float deg) {
		setAttribute("rotX", new FloatAttribute(deg));
	}
	
	/**
	 * Set the entity's Y rotation.
	 * @param deg Y Degrees
	 */
	public void setRotY(float deg) {
		setAttribute("rotY", new FloatAttribute(deg));
	}

}
