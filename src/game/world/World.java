package game.world;

import java.util.ArrayList;

import threading.ThreadManager;
import main.Main;
import game.State;
import game.world.entities.Entity;
import game.world.entities.EntityVariables;
import game.world.entities.Player;

public class World {
	
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	RenderState[] renderStates = {new RenderState(0), new RenderState(1), new RenderState(2)};
	
	private int EntityIdCount = 0;
	
	public World(){
	}
	
	public RenderState[] getRenderStates(){
		return renderStates;
	}
	
	//Update the scene which is the most behind
	public void update(float dt){
		RenderState oldestState = renderStates[0]; 
		int lowestFrame = -1;
		for(RenderState state: renderStates)
			if(!state.isReadOnly() && (lowestFrame == -1 || state.getFrameCount() <= lowestFrame)){
				oldestState = state;
				lowestFrame = state.getFrameCount();
			}
		RenderState latestState = getLatestState();
		Main.debugPrint("Updating " + oldestState.getId() + " at " + Main.getTime());
		oldestState.setUpdating(true);
		updateEntityVariables(latestState.getId(), oldestState.getId());
		oldestState.setFrameCount(getLatestState().getFrameCount()+1);
		checkPlayerInputRequests(latestState.getId(), dt);
		oldestState.setUpdating(false);
		//Main.debugPrint("New stuff to render");
	}
	
	private void checkPlayerInputRequests(int variableLoc, float dt){
		for(Entity e: entities){
			if(e instanceof Player){
				Player player = (Player)e;
				EntityVariables vars = player.getVariables(variableLoc);
				if(player.mustMoveRight()){
					vars.getPos().x += player.getMovementSpeed()*dt;
				}else if(player.mustMoveLeft()){
					vars.getPos().x -= player.getMovementSpeed()*dt;
				}
				if(player.mustMoveDown()){
					vars.getPos().y += player.getMovementSpeed()*dt;
				}else if(player.mustMoveUp()){
					vars.getPos().y -= player.getMovementSpeed()*dt;
				}
			}
		}
	}
	
	private void updateEntityVariables(int fromid, int toid){
		for(Entity e: entities){
			EntityVariables latestVariables = e.getVariables(fromid);
			EntityVariables oldVariables = e.getVariables(toid);
			oldVariables.setPos(latestVariables.getPos());
		}
	}
	
	public RenderState getLatestState(){
		RenderState latestState = renderStates[0]; //default value for state
		int highestFrame = 0;
		for(RenderState state: renderStates)
			if(state.getFrameCount() >= highestFrame && !state.isUpdating()){
				latestState = state;
				highestFrame = state.getFrameCount();
			}		
		return latestState;
	}
	
	public void render(){
		Main.debugPrint("States frames " + renderStates[0].getFrameCount() + " " + renderStates[1].getFrameCount() +" " + renderStates[2].getFrameCount());
		RenderState latestState = getLatestState();
		int renderingFrame = latestState.getFrameCount();
		
		latestState.setReadOnly(true); //Must not modify a state that is being rendered
		Main.debugPrint("Rendering " + latestState.getId() + " at " + Main.getTime());
		for(Entity e: getEntities())
			e.render();
		if(getLatestState().getFrameCount() == renderingFrame){
			Main.debugPrint("No new stuff to render at " + Main.getTime());
			ThreadManager.newStuffToRender = false;
		}
		latestState.setReadOnly(false);
	}
	
	public void addEntity(Entity e){
		e.setId(EntityIdCount);
		EntityIdCount++;
		e.setWorld(this);
		entities.add(e);
	}
	
	public ArrayList<Entity> getEntities(){
		return entities;
	}

}
