package game.world;

import game.Game;
import game.world.entities.Box;
import game.world.entities.Entity;

import java.util.ArrayList;

import threading.RenderThread;
import main.Main;

public class RenderState {

	
	private boolean readOnly = false;
	private boolean updating = false;
	
	private int frame;
	
	private int id;
	
	public RenderState(int id){
		this.id = id;
	}
	
	public int getFrameCount(){
		return frame;
	}
	
	public void setFrameCount(int f){
		frame = f;
	}
	
	public int getId(){
		return id;
	}
	
	public void setReadOnly(boolean b){
		readOnly = b;
	}
	
	public boolean isReadOnly(){
		return readOnly;
	}
	
	public boolean isUpdating(){
		return updating;
	}
	
	public void setUpdating(boolean b){
		updating = b;
	}

}
