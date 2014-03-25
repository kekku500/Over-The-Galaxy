package game.world.sync;

import game.world.World;
import game.world.gui.Component;

import java.util.ArrayList;
import java.util.List;

public class RenderRequest<T> implements Request{
	
	private Action action;
	
	private int id = -1;
	
	private boolean done;
	
	private T item;
	
	public void done(){
		done = true;
	}
	
	public RenderRequest(Action t, T e){
		item = e;
		action = t;
	}
	
	public int getID(){
		return id;
	}
	
	public Status requestStatus(World world){
		return Status.FINAL;
	}
	
	public Action getAction(){
		return action;
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	public T getItem(){
		return item;
	}


	@Override
	public void waitFor(Request req) {
		// TODO Auto-generated method stub
	}
	
	public String toString(){
		return "RenderRequest: " + item;
	}

}
