package org.scaffoldeditor.scaffold.sdoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.io.AssetManager;

/**
 * Represents the documentation of an entity. Includes descriptions and pretty names.
 * @author Igrium
 */
public class SDoc {
	private String description;
	public final List<ComponentDoc> attributes = new ArrayList<>();
	public final List<ComponentDoc> inputs = new ArrayList<>();
	public final List<ComponentDoc> outputs = new ArrayList<>();
	
	public SDoc(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String write() {
		String output = description + System.lineSeparator();
		for (ComponentDoc comp : attributes) {
			output += writeComponent(comp);
		}
		for (ComponentDoc comp : inputs) {
			output += writeComponent(comp);
		}
		for (ComponentDoc comp : outputs) {
			output += writeComponent(comp);
		}
		return output.strip();
	}
	
	private String writeComponent(ComponentDoc component) {
		return "@"+component.write()+System.lineSeparator();
	}
	
	/**
	 * Parse an SDoc file.
	 * @param input Text content of file.
	 * @return Parsed SDoc.
	 */
	public static SDoc parse(String input) {
		int descriptionEnd = input.indexOf('@');
		if (descriptionEnd == -1) {
			return new SDoc(input.strip());
		}
		
		String description = input.substring(0, descriptionEnd);
		description = description.strip();
		
		SDoc doc = new SDoc(description);

		String[] components = input.substring(descriptionEnd).split("@");
		for (String str : components) {
			if (str.length() == 0) continue;	
			ComponentDoc component = ComponentDoc.parse(str);
			
			if (component.getType().equals("attribute")) {
				doc.attributes.add(component);
			} else if (component.getType().equals("input")) {
				doc.inputs.add(component);
			} else if (component.getType().equals("output")) {
				doc.outputs.add(component);
			} else {
				LogManager.getLogger().error("Unknown component type: "+component);
			}
		}
		
		return doc;
	}
	
	/**
	 * Load an SDoc file from an asset.
	 * @param assetManager Asset manager to use.
	 * @param asset Asset path to load.
	 * @param parent Parent SDoc object. May be null.
	 * @return Loaded asset.
	 */
	public static SDoc loadAsset(AssetManager assetManager, String asset, SDoc parent) {
		if (!(assetManager.getLoader(asset).isAssignableTo(SDoc.class))) {
			throw new IllegalArgumentException("Unable to load SDoc from asset "+asset+" because it has the wrong file extension!");
		}
		
		try {
			SDoc doc = (SDoc) assetManager.loadAsset(asset, false);
			if (parent != null) {
				return merge(parent, doc);
			} else {
				return doc;
			}
		} catch (IOException e) {
			LogManager.getLogger().error("Error loading Scaffold documentation!", e);
			return new SDoc("[error loading documentation]");
		}
	}
	
	@Override
	public SDoc clone() {
		SDoc doc = new SDoc(description);
		attributes.stream().map(comp -> comp.clone()).forEachOrdered(comp -> doc.attributes.add(comp));
		inputs.stream().map(comp -> comp.clone()).forEachOrdered(comp -> doc.inputs.add(comp));
		outputs.stream().map(comp -> comp.clone()).forEachOrdered(comp -> doc.outputs.add(comp));
		return doc;
	}
	
	
	public static SDoc merge(SDoc parent, SDoc child) {
		SDoc doc = new SDoc(child.getDescription());
		parent.attributes.stream().filter(comp -> !ComponentDoc.containsName(child.attributes, comp.getName()))
				.forEachOrdered(doc.attributes::add);
		parent.inputs.stream().filter(comp -> !ComponentDoc.containsName(child.inputs, comp.getName()))
				.forEachOrdered(doc.inputs::add);
		parent.outputs.stream().filter(comp -> !ComponentDoc.containsName(child.outputs, comp.getName()))
		.forEachOrdered(doc.outputs::add);
		
		doc.attributes.addAll(child.attributes);
		doc.inputs.addAll(child.inputs);
		doc.outputs.addAll(child.outputs);
		
		return doc;
	}
}
