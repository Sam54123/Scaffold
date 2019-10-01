package org.metaversemedia.scaffold.level;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.metaversemedia.scaffold.core.Constants;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.entity.Entity;
import org.metaversemedia.scaffold.logic.Datapack;
import org.metaversemedia.scaffold.logic.MCFunction;
import org.metaversemedia.scaffold.math.Vector;

/**
 * Represents a single level file
 * @author Sam54123
 *
 */
public class Level {
	
	/* The project this level belongs to */
	private Project project;
	
	/* All the entities in the map */
	private Map<String, Entity> entities = new HashMap<String, Entity>();
	
	private LevelData levelData = new LevelData(this);
	
	/* Game functions. ONLY EXIST DURING COMPILATION */
	private MCFunction initFunction;
	private MCFunction tickFunction;
	
	private String name = "level";
	
	/* The name that shows up in the Minecraft world menu */
	private String prettyName = "Level";
	
	/**
	 * Create a new level
	 * @param project Project to create level in
	 */
	public Level(Project project) {
		this.project = project;
	}

	/**
	 * Create a new level
	 * @param project Project to create level in
	 * @param name Level name.
	 */
	public Level(Project project, String prettyName) {
		this.project = project;
		setPrettyName(prettyName);
	}
	
	/**
	 * Get the Project the level is a part of
	 * @return Assigned project
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Returns a Map with all this level's entities
	 * @return Map Entities
	 */
	public Map<String, Entity> getEntities() {
		return entities;
	}
	
	/**
	 * Get an entity by name.
	 * @param name Entity name.
	 * @return Entity
	 */
	public Entity getEntity(String name) {
		return entities.get(name);
	}
	
	/**
	 * Get this level's LevelData.
	 * @return LevelData
	 */
	public LevelData levelData() {
		return levelData;
	}
	
	/**
	 * Get this map's name.
	 * @return Map name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set this map's name.
	 * Removes all spaces.
	 * @param name New name.
	 */
	public void setName(String name) {
		// Remove spaces
		this.name = name.replaceAll("\\s+","");
	}
	
	/**
	 * Set the name that this level shows up as in the world browser.
	 * @param name New name
	 */
	public void setPrettyName(String name) {
		prettyName = name;
	}
	
	/**
	 * Get the name that this level shows up as in the world browser.
	 * @return Pretty name
	 */
	public String getPrettyName() {
		return prettyName;
	}
	
	/**
	 * Get this map's init function
	 * ONLY EXISTS DURING COMPILATION
	 * @return Init function
	 */
	public MCFunction initFunction() {
		return initFunction;
	}
	
	/**
	 * Get this map's init function
	 * ONLY EXISTS DURING COMPILATION
	 * @return Tick function
	 */
	public MCFunction tickFunction() {
		return tickFunction;
	}
	
	/**
	 * Create a new entity.
	 * @param entityType Class of entity object to create
	 * @param name Name of entity
	 * @param position Position of entity
	 * @return Newly created entity
	 */
	public Entity newEntity(Class<? extends Entity> entityType, String name, Vector position) {
		// Make sure entity with name doesn't already exist
		while (entities.get(name) != null) {
			// Attempt to increment number
			if (Character.isDigit(name.charAt(name.length()-1))) {
				int lastNum = Character.getNumericValue(name.charAt(name.length()-1))+1;
				name = name.substring(0,name.length() - 1) + lastNum;
			} else {
				name = name+'1';
			}

		}
		// Create entity
		Entity entity = null;
		try {
			entity = entityType.getDeclaredConstructor(new Class[] {Level.class,String.class}).newInstance(this, name);
			entity.setPosition(position);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		
		// Add to entities list
		entities.put(name, entity);
		
		return entity;
	}
	
	/**
	 * Serialize this level into a JSONObject.
	 * @return Serialized level
	 */
	public JSONObject serialize() {
		JSONObject object = new JSONObject();
		
		object.put("editorVersion", Constants.VERSION);
		object.put("prettyName", getPrettyName());
		
		// Add all maps
		JSONObject entities = new JSONObject();
		
		for (String key : this.entities.keySet()) {
			entities.put(key, this.entities.get(key).serialize());
		}
		
		object.put("entities", entities);
		object.put("data", levelData.getData());
		
		return object;
	}
	
	/**
	 * Unserialize a level from a JSONObject.
	 * @param project Project the level should belong to.
	 * @param object Serialized level.
	 * @return Unserlailized level.
	 */
	public static Level unserialize(Project project, JSONObject object) {
		Level level = new Level(project);
		
		// Unserialize JSON
		try {		
			level.setPrettyName(object.optString("prettyName"));
			
			JSONObject entities = object.getJSONObject("entities");
			level.levelData = new LevelData(level, object.getJSONObject("data"));
			
			for (String key : entities.keySet()) {
				// Find entity class
				String className = entities.getJSONObject(key).getString("type");
				
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(className);
					
					// Involk unserialize method
					level.entities.put(key, (Entity) entityClass.getMethod("unserialize",
							new Class[] {Level.class, String.class, JSONObject.class})
							.invoke(null, level, key, entities.getJSONObject(key)));
					
				} catch (ClassNotFoundException 
						| IllegalAccessException 
						| IllegalArgumentException 
						| InvocationTargetException 
						| NoSuchMethodException 
						| SecurityException e) {
					System.out.println("Unable to instantiate class "+className);
				}
			}
			
		} catch (JSONException e) {
			System.out.println("Improperly formatted level!");
			return null;
		}
		
		return level;
	}
	
	/**
	 * Save this level to a file
	 * @param file File to save to
	 * @return Success
	 */
	public boolean saveFile(File file) {
		JSONObject serialized = serialize();
		
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			serialized.write(writer, 4, 0);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to write to file "+file);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Save this level to a file
	 * @param file File to save to
	 * @return Success
	 */
	public boolean saveFile(String file) {
		return saveFile(project.assetManager().getAbsolutePath(file).toFile());
	}
	
	/**
	 * Load a level from a file.
	 * @param project Project to load into
	 * @param file File to load
	 * @return Loaded level
	 */
	public static Level loadFile(Project project, Path file) {
		
		JSONObject serialized;
		try {
			serialized = loadJSON(file);
		} catch (JSONException e) {
			System.out.println("Improperly formatted file: "+file.toString());
			return null;
		} catch (IOException e) {
			System.out.println("Unable to read file "+file.toString());
			return null;
		}
		
		Level level = unserialize(project, serialized);
		level.setName(file.getFileName().toString()); // Set level name
		
		if (level.getPrettyName() == null) {
			level.setPrettyName(level.getName());
		}
		
		return level;
	}
	
	/**
	 * Load a level from a file.
	 * @param project Project to load into
	 * @param file File to load
	 * @return Loaded level
	 */
	public static Level loadFile(Project project, String file) {
		return loadFile(project, project.assetManager().getAbsolutePath(file));
	}
	
	/**
	 * Compile level logic
	 * @param dataPath Path to datapack folder
	 * @return success
	 */
	public boolean compileLogic(Path dataPath) {
		// Create tick and init functions
		initFunction = new MCFunction("init");
		tickFunction = new MCFunction("tick");
		
		// Create datapack
		Datapack datapack = new Datapack(project, name);
		datapack.functions.add(initFunction);
		datapack.functions.add(tickFunction);
		
		datapack.loadFunctions.add(datapack.formatFunctionCall(initFunction));
		datapack.tickFunctions.add(datapack.formatFunctionCall(tickFunction));
		
		// Compile entities
		for (String key : entities.keySet()) {
			entities.get(key).compileLogic(datapack);
		}
		
		// Compile datapack
		try {
			datapack.compile(dataPath.resolve(project.getName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Compile this level into a playable Minecraft world (UNFINISHED).
	 * @param compileTarget Folder to compile into (saves/name).
	 * @return Success
	 */
	public boolean compile(Path compileTarget) {
		try {
			// Delete folder if exists
			if (compileTarget.toFile().exists()) {
				FileUtils.deleteDirectory(compileTarget.toFile());
			}
			
			// Make world folder
			compileTarget.toFile().mkdir();
			
			// Setup and compile logic
			Path datapackFolder = compileTarget.resolve("datapacks");
			datapackFolder.toFile().mkdir();
			if (!compileLogic(datapackFolder)) {
				return false;
			}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to compile level!");
			return false;
		}
		
	}
	
	/**
	 * Compile level logic
	 * @param dataPath Path to datapack folder
	 * @return success
	 */
	public boolean compileLogic(String dataPath) {
		return compileLogic(project.assetManager().getAbsolutePath(dataPath));
	}
	
	/* Load a JSONObject from a file */
	private static JSONObject loadJSON(Path inputPath) throws IOException, JSONException {
		List<String> jsonFile = Files.readAllLines(inputPath);
		JSONObject jsonObject = new JSONObject(String.join("", jsonFile));
		return jsonObject;
	}
	
	
}
