package game.world;

import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;
import static org.lwjgl.input.Keyboard.isKeyDown;

import java.util.ArrayList;

import threading.ThreadManager;
import main.Main;
import game.State;
import game.world.entities.Entity;
import game.world.entities.EntityVariables;
import game.world.entities.Player;

public class World {
	
	//Contains all objects
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	//To give every entity an id
	private int EntityIdCount = 0;
	
	public World(){
	}
	
	public void update(float dt){
		updateEntityVariables(); //set variables correct for current update
		
		checkPlayerInputRequests(dt);
	}
	
	private void checkPlayerInputRequests(float dt){
		for(Entity e: entities){
			if(e instanceof Player){
				Player player = (Player)e;
				EntityVariables vars = player.getVariables(EntityVariables.getUpToDate());
				if(isKeyDown(KEY_RIGHT)){
					vars.getPos().x += player.getMovementSpeed()*dt;
				}else if(isKeyDown(KEY_LEFT)){
					vars.getPos().x -= player.getMovementSpeed()*dt;
				}
				if(isKeyDown(KEY_DOWN)){
					vars.getPos().y += player.getMovementSpeed()*dt;
				}else if(isKeyDown(KEY_UP)){
					vars.getPos().y -= player.getMovementSpeed()*dt;
				}
			}
		}
	}
	
	private void updateEntityVariables(){
		for(Entity e: entities){
			EntityVariables latestVariables = e.getVariables(EntityVariables.getUpToDate());
			EntityVariables oldVariables = e.getVariables(EntityVariables.getUpdating());
			
			//Update pos
			oldVariables.setPos(latestVariables.getPos());
		}
	}
	
	public void render(){
		for(Entity e: getEntities())
			e.render();
	}
	
	public void addEntity(Entity e){
		EntityIdCount++;
		e.setId(EntityIdCount);
		e.setWorld(this);
		entities.add(e);
	}
	
	public ArrayList<Entity> getEntities(){
		return entities;
	}
}
