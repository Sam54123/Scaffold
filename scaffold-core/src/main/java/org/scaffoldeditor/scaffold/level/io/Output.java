package org.scaffoldeditor.scaffold.level.io;

import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.AttributeRegistry;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.serialization.XMLSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Output implements XMLSerializable {
	
	private Entity owner;
	private String trigger = "";
	private String target = "";
	private String inputName = "";
	private List<Attribute<?>> args = new ArrayList<>();
	
	public Output(Entity owner) {
		this.owner = owner;
	}
	
	public Output(Entity owner, String trigger, String target, String inputName) {
		this.owner = owner;
		this.trigger = trigger;
		this.target = target;
		this.inputName = inputName;
	}
	
	public Output(Entity owner, String trigger, String target, String inputName, List<Attribute<?>> args) {
		this(owner, trigger, target, inputName);
		this.args = args;
	}
	
	/**
	 * Get this output's owning entity (the entity that fires the output).
	 */
	public Entity getOwner() {
		return owner;
	}
	
	/**
	 * Get the string the owning entity can use to determine when to fire this output.
	 * @return The output's trigger string.
	 */
	public String getTrigger() {
		return trigger;
	}
	
	/**
	 * Set the string the owning entity uses to determine when to fire this output. 
	 * @param trigger The output's trigger string.
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	/**
	 * Get the name of the target entity.
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Set the target entity.
	 * @param target New target entity's name.
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Get the target input name.
	 * @return The input on the target entity that will be fired.
	 */
	public String getInputName() {
		return inputName;
	}

	/**
	 * Set the target input name.
	 * @param inputName The input on the target entity that will be fired.
	 */
	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	/**
	 * Get the arguements that this output will pass to the target at compile time.
	 * @return A mutable list of arguements.
	 */
	public List<Attribute<?>> getArgs() {
		return args;
	}
	
	/**
	 * Compile this output.
	 * @return The list of commands that should be called when this output is triggered.
	 */
	public List<Command> compile() {
		Entity entity = owner.getLevel().getEntity(target);
		if (entity == null) {
			throw new IllegalStateException("Tried to compile an output with a target entity that does not exist: "+target);
		}
		
		return entity.compileInput(inputName, args, getOwner());
	}
	
	@Override
	public Element serialize(Document document) {
		Element element = document.createElement("output");
		element.setAttribute("trigger", trigger);		
		element.setAttribute("target", target);
		element.setAttribute("inputName", inputName);
		
		for (Attribute<?> attribute : getArgs()) {
			element.appendChild(attribute.serialize(document));
		}
		
		return element;
	}
	
	/**
	 * Deserialize an output from XML.
	 * @param element Element to deserialize.
	 * @param owner Owning entity.
	 * @return Deserialized output.
	 */
	public static Output deserialize(Element element, Entity owner) {
		String trigger = element.getAttribute("trigger");
		String target = element.getAttribute("target");
		String inputName = element.getAttribute("inputName");
		Output output = new Output(owner, trigger, target, inputName);
		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				output.getArgs().add(AttributeRegistry.deserializeAttribute((Element) children.item(i)));
			}
		}
		
		return output;
	}
	
	@Override
	public Output clone() {
		return new Output(owner, trigger, target, inputName, args);
	}
}
