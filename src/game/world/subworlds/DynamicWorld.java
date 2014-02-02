package game.world.subworlds;

import game.world.AbstractWorld;
import game.world.entities.Entity;
import game.world.gui.graphics.Graphics;

import java.util.ArrayList;
import java.util.List;

public class DynamicWorld extends AbstractWorld{
	
	@Override
	public void update(float dt) {
		for(Entity e: getEntities()){
			e.update(dt);
		}
	}

	@Override
	public void render(Graphics g) {
		for(Entity e: getEntities()){
			e.render();
		}
		
	}

	@Override
	public void dispose() {
		for(Entity e: getEntities()){
			e.dispose();
		}
	}
	

}
