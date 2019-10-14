package org.metaversemedia.scaffold.level.entity.logic;

import java.util.List;

import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.level.io.Input;
import org.metaversemedia.scaffold.level.io.Output;
import org.metaversemedia.scaffold.logic.Datapack;
import org.metaversemedia.scaffold.logic.MCFunction;

/**
 * This class relays io from it's inputs to it's outputs
 * 
 * @author Sam54123
 */
public class Relay extends Entity {

	public Relay(Level level, String name) {
		super(level, name);
		setAttribute("delay", 0);
		
		// Called to activate the relay.
		registerInput(new Input(this) {

			@Override
			public String getName() {
				return "Trigger";
			}

			@Override
			public boolean takesArgs() {
				return false;
			}

			@Override
			public String getCommand(Entity instigator, Entity caller, String[] args) {
				
				if ((float) getAttribute("delay") <= 0) { // if delay < 0, ignore it.
					return "function "+getLevel().getDatapack().formatFunctionCall(getFunctionName());
				}  else {
					return "schedule function "+getLevel().getDatapack().formatFunctionCall(getFunctionName())+" "+getAttribute("delay");
				}
			}

		});
	}

	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributes = super.getAttributeFields();
		attributes.add(new AttributeDeclaration("delay", Integer.class));
		return attributes;
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		super.compileLogic(datapack);

		// Compile relay function
		MCFunction function = new MCFunction(getFunctionName());
		datapack.functions.add(function);

		String[] outputCommands = compileOutput("OnTrigger", this);

		for (String s : outputCommands) {
			function.addCommand(s);
		}

		return true;
	}
	
	/**
	 * Get the name of the function this relay will generate.
	 * @return Function name.
	 */
	public String getFunctionName() {
		return "relay_" + getName();
	}

}