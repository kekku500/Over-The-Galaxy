package game.world.sync;

import game.RenderState;
import game.world.World;
import game.world.entities.Entity;
import game.world.gui.Component;
import game.world.sync.Request.Status;
import game.world.sync.Request.Action;
import game.world.sync.Request.Type;

import java.util.ArrayList;
import java.util.List;

public class RenderRequest implements Request{
	
	private Action action;
	private Entity entity;
	private Component component;
	private Type type;
	
	private int id = -1;
	
	private boolean done;
	
	private List<Integer> changedWorlds = new ArrayList<Integer>();
	
	public void done(){
		done = true;
	}
	
	public RenderRequest(Action t, Entity e){
		type = Type.ENTITY;
		action = t;
		entity = e;
	}
	
	public RenderRequest(Action t, Component e){
		type = Type.COMPONENT;
		action = t;
		component = e;
	}
	
	public int getID(){
		return id;
	}
	
	public Status requestStatus(World world){
		return Status.FINAL;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public Action getAction(){
		return action;
	}
	
	public Component getComponent(){
		return component;
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	public Type getType(){
		return type;
	}

	@Override
	public void waitFor(Request req) {
		// TODO Auto-generated method stub
	}
	
	public String toString(){
		return "RenderRequest: " + getType() + " at " + getEntity();
	}

}
