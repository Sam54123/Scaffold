package org.scaffoldeditor.scaffold.level.entity;

import org.scaffoldeditor.scaffold.math.Vector;

/**
 * Represents a box-shaped entity where the bounds can be manually resized.
 * @author Igrium
 */
public interface BrushEntity {
	
	/**
	 * Set the bounds of this brush in world space. Bounds are independent of entity origin.
	 * @param newBounds A two-element array denoting the opposite corners of the brush's bounding box.
	 * @param suppressUpdate If true, block update listeners (compilers, etc) are not triggered.
	 */
	public void setBounds(Vector[] newBounds, boolean suppressUpdate);
	
	/**
	 * Get the bounds of this brush in world space.
	 * @return A two-element array denoting the opposite corners of the brush's bounding box.
	 */
	Vector[] getBounds();
}
