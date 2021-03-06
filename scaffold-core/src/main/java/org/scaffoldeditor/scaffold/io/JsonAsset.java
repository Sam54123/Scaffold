package org.scaffoldeditor.scaffold.io;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonAsset extends AssetLoader<JSONObject> {
	
	public JsonAsset() {
		super(JSONObject.class);
	}
	
	public static void register() {
		AssetLoaderRegistry.registry.put("json", new JsonAsset());
	}

	@Override
	public JSONObject loadAsset(InputStream in) throws IOException {
		JSONTokener tokener = new JSONTokener(in);
		return new JSONObject(tokener);
	}

}
