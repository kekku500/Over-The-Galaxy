package game;

import java.util.Arrays;

import game.world.RenderState;

public abstract class State {
	
	//If false, renderThread sleeps
	private boolean newStuffToRender = true;
	
	//Multithreading rendering handling (synchronizing update and render threads)
	//To know which EntityVariables to use
	RenderState[] renderStates = {new RenderState(0), new RenderState(1), new RenderState(2)};
	
	//ABSTRACT
	public abstract void init();
	
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
		for(RenderState state: renderStates)
			//Select state which has higher frame count and is not updating
			if(state.getFrameCount() >= highestFrame && !state.isUpdating()){
				latestState = state;
				highestFrame = state.getFrameCount();
			}		
		return latestState;
	}
	
	/**
	 * Used for getting state for updating.
	 * @return RenderState which has the lowest frame count and is not read-only.
	 */
	public RenderState getOldestState(){
		RenderState oldestState = null; 
		int lowestFrame = -1;
		for(RenderState state: renderStates)
			//Must not be read-only and has lower frame count
			if(!state.isReadOnly() && (lowestFrame == -1 || state.getFrameCount() <= lowestFrame)){
				oldestState = state;
				lowestFrame = state.getFrameCount();
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
	
	public RenderState[] getRenderStates(){
		return renderStates;
	}
	
	public boolean newStuffToRender(){
		return newStuffToRender;
	}

}
