package game;

import game.world.World;
import game.world.entities.Entity;
import game.world.subworlds.DynamicWorld;

public class RenderState {
	
	//STATUSES
	private boolean rendering = false; //while rendering
	private boolean updating = false; //while being updated, not usable for rendering

	public static int updatingId = -1; //These variables are changed and used for calculations.
	public static int upToDateId = 0; //The most up to date variables, used for copying upToDate variables to updating.
	public static int renderingId = -1; //Used for rendering
	
	//How many times has been updated
	private int frame;
	
	private int id;
	
	private World world;
	
	public RenderState(State state, int id, int frame){
		world = new World(state, 0);
		this.id = id;
		this.frame = frame;
	}
	
	public World getWorld(){
		return world;
	}
	
	public void copyFrom(RenderState state){
		World betterWorld = state.getWorld();
		world.setCamera(betterWorld.getCamera().copy());
		//world.getStaticWorld().setEntities(betterWorld.getStaticWorld().getCopiedEntities());
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
