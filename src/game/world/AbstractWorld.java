package game.world;

import game.world.entities.Entity;
import game.world.gui.graphics.Graphics;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWorld {
	
	private List<Entity> entities = new ArrayList<Entity>();
	
	public abstract void update(float dt);
	
	public abstract void render(Graphics g);
		
	public void dispose(){
		for(Entity e: entities){
			e.dispose();
		}
	}
	
	public void addEntity(Entity e){
		entities.add(e);
	}
	
	public List<Entity> getEntities(){
		return entities;
	}
	
	public void setEntities(List<Entity> e){
		entities = e;
	}
	
	public void replace(Entity replaceWith){
		List<Entity> entities = getEntities();
		for(int i=0;i<entities.size();i++){
			Entity e = entities.get(i);
			if(e.getId() == replaceWith.getId())
				entities.set(i, replaceWith);
		}
	}
	
	public List<Entity> getCopiedEntities(){
		List<Entity> newList = new ArrayList<Entity>();
		for(Entity e: entities){
			newList.add(e.copy());
		}
		return newList;
	}

}
