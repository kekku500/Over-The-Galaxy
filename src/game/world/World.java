package game.world;

import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;
import static org.lwjgl.input.Keyboard.isKeyDown;
import game.world.entities.Entity;
import game.world.entities.EntityVariables;
import game.world.entities.Player;

import java.util.ArrayList;

import main.PlayState;
import controller.FPController;

public class World {
	
	//Contains all objects
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	//To give every entity an id
	private int EntityIdCount = 0;
	
	//Grid
	Grid grid = new Grid(this);
	
	private PlayState state;
	
	public World(PlayState state){
		this.state = state;
	}
	
	public FPController getCamera(){
		return state.getCamera();
	}
	
	public void update(float dt){
		updateEntityVariables(); //THIS LINE MUST BE FIRST
		
		grid.update();
		
		checkPlayerInputRequests(dt); //AFTER UPDATED VARIABLES
	}
	
	private void checkPlayerInputRequests(float dt){
		for(Entity e: entities){
			if(e instanceof Player){
				Player player = (Player)e;
				EntityVariables vars = player.getVariables(EntityVariables.getUpdating());
				if(isKeyDown(KEY_RIGHT)){
					vars.getPos().x -= player.getMovementSpeed()*dt;
				}else if(isKeyDown(KEY_LEFT)){
					vars.getPos().x += player.getMovementSpeed()*dt;
				}
				if(isKeyDown(KEY_DOWN)){
					vars.getPos().z -= player.getMovementSpeed()*dt;
				}else if(isKeyDown(KEY_UP)){
					vars.getPos().z += player.getMovementSpeed()*dt;
				}
			}
		}
	}
	
	/**
	 * Gets info from the most up to date and copies it to the updating one.
	 */
	private void updateEntityVariables(){
		for(Entity e: entities){
			EntityVariables latestVariables = e.getVariables(EntityVariables.getUpToDate());
			EntityVariables oldVariables = e.getVariables(EntityVariables.getUpdating());
			
			//Update pos
			oldVariables.setPos(latestVariables.getPos());
		}
	}
	
	public void render(){
		grid.render();
		for(Entity e: getEntities())
			e.render();
	}
	
	public void addEntity(Entity e){
		EntityIdCount++;
		e.setId(EntityIdCount);
		e.setWorld(this);
		entities.add(e);
	}
	
	public void dispose(){
		for(Entity e: getEntities()){
			e.dispose();
		}
		grid.dispose();
	}
	
	public ArrayList<Entity> getEntities(){
		return entities;
	}
}
