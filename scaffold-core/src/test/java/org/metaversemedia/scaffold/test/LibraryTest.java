package org.metaversemedia.scaffold.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.metaversemedia.scaffold.core.Project;
import org.metaversemedia.scaffold.level.Level;
import org.metaversemedia.scaffold.level.entity.GameEntity;
import org.metaversemedia.scaffold.math.Vector;
import org.metaversemedia.scaffold.nbt.Schematic;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.stream.NBTInputStream;

public class LibraryTest {

	@Test
	public void test() {
//		Project project = Project.init("/Users/h205p1/Documents/ProgramingProjects/Scaffold/testProject", "Test Project");
		Project project = Project.init("C:\\Users\\Sam54123\\Documents\\Minecraft\\MapdevUtils\\Scaffold\\testProject", "Test Project");

		
		Level level = new Level(project);
		
		GameEntity ent1 = (GameEntity) level.newEntity(GameEntity.class, "ent1", new Vector(0,0,0));

		level.saveFile("maps/testLevel.mclevel");
		level.compile(project.assetManager().getAbsolutePath("game/saves/world"));
		
//		try {
//			Schematic schematic = Schematic.fromFile(project.assetManager().findAsset("schematics/fort_concord.schematic").toFile());
//			System.out.println(Short.valueOf(schematic.dataAtIndex(950)));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
	