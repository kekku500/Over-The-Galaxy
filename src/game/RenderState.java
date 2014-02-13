package game;

import game.world.World;

public class RenderState {
	
	//STATUSES
	private boolean rendering = false; //while rendering
	private boolean updating = false; //while being updated, not usable for rendering

	public static int updatingId = -1; 
	public static int upToDateId = 0;
	public static int renderingId = -1;
	
	private int frame; //update counter
	
	private int id;
	
	private World world; //the most important part
	
	public RenderState(State state, int id, int frame){
		world = new World(state, 0);
		this.id = id;
		this.frame = frame;
	}
	
	public World getWorld(){
		return world;
	}
	
	public void setWorld(World w){
		world = w;
	}
	
	public void setRendering(boolean b){
		if(b)
			renderingId = id;
		else
			renderingId = -1;
		rendering = b;
	}
	
	public void setFrameCount(int f){
		frame = f;
	}
	
	//GET
	public void setUpdating(boolean b){
		if(b){ //true
			updatingId = id;
		}else{ //false
			updatingId = -1;
			upToDateId = id;
		}
		updating = b;
	}
	
	public void setId(int i){
		id = i;
	}

	public boolean isRendering(){
		return rendering;
	}
	
	public boolean isUpdating(){
		return updating;
	}
	
	public int getFrameCount(){
		return frame;
	}
	
	public int getId(){
		return id;
	}
	

}
