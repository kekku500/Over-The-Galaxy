package game.world.sync;

import game.RenderState;
import game.world.World;
import game.world.entities.Entity;
import game.world.entities.LightSource;
import game.world.gui.Component;

import java.util.ArrayList;
import java.util.List;

import controller.Camera;

public class UpdateRequest<T> implements Request{
	
	public List<Integer> changedWorlds = new ArrayList<Integer>();

	private Action action;
	private T item;
	
	private Request waitFor;
	private int id = -1;
	
	public UpdateRequest(Action action, T t){
		setAction(action);
		item = t;
	}
	
	public UpdateRequest(Action t, T et, int id){
		this(t, et);
		this.id = id;
	}
	
	public int getID(){
		return id;
	}
	
	public void waitFor(Request req){
		waitFor = req;
	}
	
	public void setAction(Action t){
		action = t;
		if(getAction() == Action.UPDATE || getAction() == Action.UPDATEALL){
			changedWorlds.add(RenderState.updatingId);
		}
	}
	
	public Status requestStatus(World world){
		if(waitFor != null)
			if(!waitFor.isDone()){
				return Status.IDLE;
			}
		if(getAction() == Action.CAMERAFOCUS){
			if(item instanceof Entity){
				if(((Entity)item).getWorld() == null){
					return Status.IDLE;
				}
			}
		}
		if(getAction() == Action.UPDATE)
			return Status.FINAL;
		if(changedWorlds.contains(world.getUniqueID())){
			return Status.IDLE;
		}else
			if(changedWorlds.size() >= 2)
				return Status.FINAL;	
		changedWorlds.add(world.getUniqueID());
		return Status.CONTINUE;
	}
	
	public boolean isDone(){
		if(changedWorlds.size() >= 3){
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
	
	public String toString(){
		return "UpdateRequest: " + item + " " + getAction();
	}

}
