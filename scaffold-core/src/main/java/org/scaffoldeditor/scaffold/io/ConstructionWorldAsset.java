package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

import org.scaffoldeditor.nbt.io.ConstructionFormat;
import org.scaffoldeditor.nbt.schematic.Construction;

public class ConstructionWorldAsset extends AssetLoader<Construction> {
	
	public static void register() {
		AssetLoaderRegistry.registry.put("cworld", new ConstructionWorldAsset());
	}

	public ConstructionWorldAsset() {
		super(Construction.class);
	}
	
	private ConstructionFormat format = new ConstructionFormat();

	@Override
	public Construction loadAsset(InputStream in) throws IOException {
		return format.parse(in);
	}
	
}
