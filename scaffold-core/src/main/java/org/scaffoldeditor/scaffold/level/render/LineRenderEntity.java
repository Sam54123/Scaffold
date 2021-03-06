package org.scaffoldeditor.scaffold.level.render;

import org.scaffoldeditor.nbt.math.Vector3f;
import org.scaffoldeditor.scaffold.level.entity.Entity;

/**
 * Renders a line in the editor.
 * @author Igrium
 *
 */
public class LineRenderEntity extends RenderEntity {
	
	private Vector3f endPos;
	private float red = 1;
	private float green = 1;
	private float blue = 1;
	private float alpha = 1;
	
	/**
	 * Create a line render entity.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Start position of the line.
	 * @param endPos     End position of the line.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 */
	public LineRenderEntity(Entity entity, Vector3f position, Vector3f endPos, String identifier) {
		super(entity, position, identifier);
		this.endPos = endPos;
	}
	
	/**
	 * Create a line render entity.
	 * 
	 * @param entity     Owning Scaffold entity.
	 * @param position   Start position of the line.
	 * @param endPos     End position of the line.
	 * @param identifier The unique identifier of this render entity, used to keep
	 *                   track of it in relation to its editor entity. Only one
	 *                   instance of this string may exist per Scaffold entity.
	 *                   Different Scaffold entities may share identifiers.
	 * @param red        Amount of red.
	 * @param green      Amount of green.
	 * @param blue       Amount of blue.
	 * @param alpha      How solid the line should be.
	 */
	public LineRenderEntity(Entity entity, Vector3f position, Vector3f endPos, String identifier, float red, float green, float blue, float alpha) {
		this(entity, position, endPos, identifier);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	/**
	 * Get the end position of the line.
	 */
	public Vector3f getEndPos() {
		return endPos;
	}
	
	public float getRed() {
		return red;
	}
	
	public float getGreen() {
		return green;
	}
	
	public float getBlue() {
		return blue;
	}
	
	public float getAlpha() {
		return alpha;
	}
}
