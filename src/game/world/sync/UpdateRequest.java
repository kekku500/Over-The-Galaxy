package game.world.sync;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import controller.Camera;
import game.RenderState;
import game.world.World;
import game.world.entities.Entity;
import game.world.gui.Component;

public class UpdateRequest implements Request{

	private Action action;
	private Entity entity;
	private Component component;
	private Type type;
	
	private int id = -1;
	
	public List<Integer> changedWorlds = new ArrayList<Integer>();
	
	private Request waitFor;
	
	private Camera cam;
	
	public UpdateRequest(Entity e){
		type = Type.ENTITY;
		entity = e;
	}
	
	public <T> UpdateRequest(Action action, T t){
		this.action = action;
		if(t instanceof Entity && !(t instanceof Camera)){
			type = Type.ENTITY;
			entity = (Entity)t;
		}else if(t instanceof Component){
			type = Type.COMPONENT;
			component = (Component)t;
		}else if(t instanceof Camera){
			type = Type.CAMERA;
			cam = (Camera)t;
		}
		typeUpdated();
	}
	
	public UpdateRequest(Action t, Entity e, int id){
		type = Type.ENTITY;
		this.id = id;
		action = t;
		entity = e;
		typeUpdated();
	}
	
	public int getID(){
		return id;
	}
	
	public void waitFor(Request req){
		waitFor = req;
	}
	
	public void setType(Action t){
		action = t;
		typeUpdated();
	}
	
	public void typeUpdated(){
		if(getAction() == Action.UPDATE || getAction() == Action.UPDATEALL){
			changedWorlds.add(RenderState.updatingId);
		}
	}
	
	public Status requestStatus(World world){
		if(waitFor != null)
			if(!waitFor.isDone()){
				//System.out.println("waitfor idle");
				return Status.IDLE;
			}
		if(getAction() == Action.CAMERAFOCUS){
			if(getEntity().getWorld() == null){
				return Status.IDLE;
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
	
	public Component getComponent(){
		return component;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public Camera getCamera(){
		return cam;
	}
	
	public Action getAction(){
		return action;
	}
	
	public Type getType(){
		return type;
	}
	
	public String toString(){
		return "UpdateRequest: " + getType() + " " + getAction();
	}

}
