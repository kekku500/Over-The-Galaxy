package state;

import input.InputListener;

import java.util.Arrays;

import world.World;
import world.sync.RequestList;

public abstract class State{
	
	//If false, renderThread sleeps
	private boolean newStuffToRender = true;
	private boolean renderInitialized = false;
	
	//Multithreading rendering handling (synchronizing update and render threads)
	RenderState[] renderStates = new RenderState[3];
	private RequestList requestList = new RequestList();
	
	public State(){
		renderStates[0] = new RenderState(this, 0, 0);
		renderStates[1] = new RenderState(this, 1, -1);
		renderStates[2] = new RenderState(this, 2, -1);
		linkWorlds(renderStates[0].getWorld(), renderStates[1].getWorld(), renderStates[2].getWorld());
	}
	
	/**
	 * Gives all worlds same dynamic world and camera
	 * @param worlds
	 */
	private void linkWorlds(World...worlds){
		World mainWorld = worlds[0];
		mainWorld.init();
		
		//mainWorld.getCamera().openGLInitialization();
		
		for(int i=1;i<worlds.length;i++)
			worlds[i].setLink(mainWorld);
			//mainWorld.linkWorlds(worlds[i]);
	}
	
	public void callRenderInit(){
		if(!renderInitialized){
			renderInitialized = true;
			World world = getLatestState().getWorld();
			
			renderInit();
		}
	}
	
	public abstract void postRenderInit();
	
	//ABSTRACT
	public abstract void init();
	
	public abstract void renderInit();
	
	public void update1(float dt){
		InputListener.reset();
		
		update(dt);
	}
	
	public abstract void update(float dt);
	
	public abstract void render();
	
	public abstract void dispose();
	
	public abstract int getId();
	
	//SET
	public void setStuffToRender(boolean b){
		newStuffToRender = b;
	}
	
	//GET
	/**
	 * Used for getting frame which is the most up to date for rendering.
	 * @return RenderState which has the highest frame count and is not being updated.
	 */
	public RenderState getLatestState(){
		RenderState latestState = null;
		int highestFrame = 0;
		for(RenderState state: renderStates){
			//Select world which has higher frame count and is not updating
			if(state.getFrameCount() >= highestFrame && !state.isUpdating()){
				latestState = state;
				highestFrame = state.getFrameCount();
			}	
		}
		return latestState;
	}
	
	/**
	 * Used for getting state for updating.
	 * @return RenderState which has the lowest frame count and is not read-only.
	 */
	public RenderState getOldestState(){
		RenderState oldestState = null; 
		int lowestFrame = -2;
		for(RenderState state: renderStates){
			//Must not be rendering and has lower frame count
			if(!state.isRendering() && (lowestFrame == -2 || state.getFrameCount() <= lowestFrame)){
				oldestState = state;
				lowestFrame = state.getFrameCount();
			}		
		}
		return oldestState;
	}
	
	/**
	 * Overview of all RenderStates frame counts.
	 * @return
	 */
	public String getStatesCounts(){
		int[] counts = {0,0,0};
		int i = 0;
		for(RenderState state: renderStates){
			counts[i] = state.getFrameCount();
			i++;
		}
		return Arrays.toString(counts);
	}
	
	public RequestList getRequestList(){
		return requestList;
	}
	
	public RenderState getUpdatingState(){
		return renderStates[RenderState.updatingId];
	}
	
	public RenderState getRenderingState(){
		return renderStates[RenderState.renderingId];
	}
	
	public RenderState getUpToDateState(){
		return renderStates[RenderState.upToDateId];
	}
	
	public RenderState[] getRenderStates(){
		return renderStates;
	}
	
	public boolean newStuffToRender(){
		return newStuffToRender;
	}

}
