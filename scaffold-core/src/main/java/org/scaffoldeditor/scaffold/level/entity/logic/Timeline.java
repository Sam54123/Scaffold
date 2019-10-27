package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.List;
import org.json.JSONObject;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.MapAttribute;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;

public class Timeline extends Entity {
	
	public Timeline(Level level, String name) {
		super(level, name);
		setAttribute("outputs", new MapAttribute());
	}
	
	/**
	 * Get the scoreboard objective the timeline is using to store it's frame.
	 * @return Frame objective name.
	 */
	public String getFrameObjective() {
		return (getLevel().getName()+"."+getName()+".frame").toLowerCase();
	}
	
	/**
	 * Get the scoreboard objective used to represent whether this timeline is playing.
	 * @return Playing objective name.
	 */
	public String getPlayingObjective() {
		return (getLevel().getName()+"."+getName()+".playing").toLowerCase();
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributes = super.getAttributeFields();
		attributes.add(new AttributeDeclaration("outputs", MapAttribute.class));
		return attributes;
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		if  (!super.compileLogic(datapack)) {
			return false;
		}
		
		getLevel().initFunction().addCommand("scoreboard objectives add "+getFrameObjective()+" dummy");
		getLevel().initFunction().addCommand("scoreboard objectives add "+getPlayingObjective()+" dummy");
		
		MCFunction timelineTick = new MCFunction(getName()+".tick");
		writeTickFunction(timelineTick);
		
		getLevel().getDatapack().functions.add(timelineTick);
		// Make level run function every tick if playing.
		getLevel().tickFunction()
				.addCommand("execute as " + getLevel().getScoreboardEntity().getTargetSelector()
						+ " if score @s "+ getPlayingObjective() + " matches 1 run "
						+ getLevel().getDatapack().formatFunctionCall(timelineTick));
		
		return true;
	}
	
	/**
	 * Write the function that the timeline calls each frame.
	 * Runs in the context of the map scoreboard entity.
	 * @param tickFunction tick function to write to.
	 */
	protected void writeTickFunction(MCFunction tickFunction) {
		// Compile output list.
		MapAttribute outputs = ((MapAttribute) getAttribute("outputs"));
		for (String frame : outputs.keySet()) {
			
			if (hasOutput(outputs.get(frame))) { // Only run of output connction is set.
				// Compile single output.
				String[] commands = compileOutput(outputs.get(frame), this);
				
				for (String c : commands) {
					tickFunction.addCommand("execute if score @s "+getFrameObjective()+" matches "+frame+" run "+c);
				}
				
			}
			
		}
		
		tickFunction.addCommand("scoreboard players add @s "+getFrameObjective()+" 1");
	}
	
	@Override
	public JSONObject serialize() {
		JSONObject serialized = super.serialize();
		MapAttribute outputs = (MapAttribute) getAttribute("outputs");
		
		// Make sure outputs are serialized properly.
		serialized.getJSONObject("attributes").put("outputs", new JSONObject(outputs));
		
		return serialized;
	}
	
	@Override
	protected void onUnserialized(JSONObject object) {
		super.onUnserialized(object);
		setAttribute("outputs", new MapAttribute(object.getJSONObject("attributes").getJSONObject("outputs")));
	}
}
