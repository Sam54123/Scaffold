package org.scaffoldeditor.scaffold.level.entity.logic;

import java.util.Collections;

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.EntityFactory;
import org.scaffoldeditor.scaffold.level.entity.EntityRegistry;
import org.scaffoldeditor.scaffold.level.render.BillboardRenderEntity;
import org.scaffoldeditor.scaffold.logic.Datapack;

/**
 * Fires an output on level load.
 * 
 * @author Igrium
 */
public class Auto extends Entity {
	
	public static void Register() {
		EntityRegistry.registry.put("logic_auto", new EntityFactory<Entity>() {		
			@Override
			public Entity create(Level level, String name) {
				return new Auto(level, name);
			}
		});
	}

	public Auto(Level level, String name) {
		super(level, name);
	}
	
	@Override
	public void onAdded() {
		super.onAdded();
		updateRenderEntities();
	}
	
	@Override
	public void onUpdateAttributes(boolean noRecompile) {
		super.onUpdateAttributes(noRecompile);
		updateRenderEntities();
	}

	@Override
	public void updateRenderEntities() {
		super.updateRenderEntities();
		updateRenderEntities(Collections.singleton(
				new BillboardRenderEntity(this, getPosition(), "billboard", "scaffold:textures/editor/auto.png")));
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		if (!super.compileLogic(datapack)) {
			return false;
		}
		
		String[] loadCommands = compileOutput("OnLoad", this);
		
		for (String s : loadCommands) {
			getLevel().initFunction().addCommand(s);
		}
		
		String[] tickCommands = compileOutput("OnTick", this);
		
		for (String s : tickCommands) {
			getLevel().tickFunction().addCommand(s);
		}
		
		return true;
	}
	
	@Override
	public String getRenderAsset() {
		return "scaffold/textures/editor/auto.png";
	}

}
