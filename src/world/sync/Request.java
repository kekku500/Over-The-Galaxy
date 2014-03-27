package world.sync;

import java.util.ArrayList;
import java.util.List;

import state.RenderState;
import world.World;
import world.entity.gui.Component;
import controller.Camera;

/**
 * A class for updating all worlds.
 * Request queue is completed at the beginning of world update method.
 * @author Kevin
 * @param <T>
 */
public class Request<T>{
	
	public enum Action{
		ADD, //Add Entity to the world
		REMOVE, //Remove Entity from the world
		RELINKNEXT, //Links object with same id again in the next state
		RELINKALL} //Links objects in all world states
	
	public enum Status{
		FINAL, //One more world left to update
		IDLE, //Current world has done the request
		CONTINUE} //Request not done yet (more world states to update)
	
	public List<Integer> updatedWorlds = new ArrayList<Integer>();

	private Action action;
	private T item;
	
	private int id = -1;
	
	public Request(Action a, T t){
		setAction(a);
		item = t;
	}
	
	public Request(Action a, T t, int id){
		this(a, t);
		this.id = id;
	}

	public void setAction(Action t){
		action = t;
		//World where relinking request is created, is considered updated world.
		if(getAction() == Action.RELINKNEXT || getAction() == Action.RELINKALL){
			updatedWorlds.add(RenderState.updatingId);
		}
	}
	
	public Status requestStatus(World world){
		if(getAction() == Action.RELINKNEXT) //update only once
			return Status.FINAL;
		if(updatedWorlds.contains(world.getID())){
			return Status.IDLE;
		}else
			if(updatedWorlds.size() >= 2)
				return Status.FINAL;	
		updatedWorlds.add(world.getID());
		return Status.CONTINUE;
	}
	
	public boolean isDone(){
		if(updatedWorlds.size() >= 3){
			return true;
		}
		return false;
	}
	
	public T getItem(){
		return item;
	}
	
	public Action getAction(){
		return action;
	}
	
	public int getID(){
		return id;
	}
	
	public String toString(){
		return "UpdateRequest [Item=" + getItem() + " Action=" + getAction() + "]";
	}

}
