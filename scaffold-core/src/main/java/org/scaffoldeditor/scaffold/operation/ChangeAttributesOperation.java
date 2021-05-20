package org.scaffoldeditor.scaffold.operation;

import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.VectorAttribute;
import org.scaffoldeditor.scaffold.math.Vector;

/**
 * Update the attributes of an entity.
 * @author Igrium
 */
public class ChangeAttributesOperation implements Operation {
	
	private Entity target;
	private Map<String, Attribute<?>> attributes = new HashMap<>();
	private Vector newPosition = null;
	
	private Map<String, Attribute<?>> old = new HashMap<>();
	private Vector oldPosition = null;
	
	/**
	 * Create a change attributes operation.
	 * @param target Entity to change the attributes of.
	 * @param attributes Attributes to change.
	 */
	public ChangeAttributesOperation(Entity target, Map<String, Attribute<?>> attributes) {
		this.target = target;
		this.attributes = attributes;
	}
	
	@Override
	public boolean execute() {
		// Ensure position is properly set on entity.
		if (attributes.containsKey("position")) {
			newPosition = ((VectorAttribute) attributes.get("position")).getValue();
			oldPosition = target.getPosition();
			attributes.remove("position");
			target.setPosition(newPosition);
		}
		
		for (String name : attributes.keySet()) {		
			old.put(name, target.getAttribute(name));
			target.setAttribute(name, attributes.get(name), true);
		}
		target.onUpdateAttributes();
		return true;
	}

	@Override
	public void undo() {
		for (String name : old.keySet()) {
			target.setAttribute(name, old.get(name), true);
		}
		if (oldPosition != null) {
			target.setPosition(oldPosition);
		}
		target.onUpdateAttributes();
	}

	@Override
	public void redo() {
		if (newPosition != null) {
			target.setPosition(newPosition);
		}
		for (String name : attributes.keySet()) {
			target.setAttribute(name, attributes.get(name), true);
		}
		target.onUpdateAttributes();
	}

	@Override
	public String getName() {
		return "Change entity attributes";
	}

}
