package state;

import input.InputReciever;
import input.LWJGLInput;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import utils.R;
import world.EntityManager;
import world.gui.GameUserInterface;
import world.sync.RequestList;

public abstract class State{
	
	public static final int STATE_COUNT = 3;
	public static final int FIRST_STATE = 0;
	
	//Multithreading rendering handling (synchronizing update and render threads)
	RenderState[] renderStates;
	private RequestList requestList = new RequestList();
	
	private GameUserInterface gameUI;
	
	LWJGLInput input;
	
	public State(){
		input = new LWJGLInput();
		renderStates = new RenderState[STATE_COUNT];
		for(int i = 0;i<STATE_COUNT;i++)
			renderStates[i] = new RenderState(i, (i == FIRST_STATE ? 0 : -1));
	}
	
	public void setInput(LWJGLInput input){
		this.input = input;
	}
	
	public LWJGLInput getInput(){
		return input;
	}
	
	//ABSTRACT
	
	public abstract void init();
	
	public void setGameUI(GameUserInterface globalUI){
		this.gameUI = globalUI;
	}
	
	public GameUserInterface getGameUI(){
		return gameUI;
	}
	
	public void beginUpdate(float dt){
		input.updateInputState();
			
		update(dt);
	}
	
	public abstract void update(float dt);
	
	public abstract void render();
	
	public abstract void dispose();
	
	public abstract int getId();
	
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
		String[] counts = {"0","0","0"};
		int i = 0;
		for(RenderState state: renderStates){
			counts[i] = Integer.toString(state.getFrameCount());
			if(state.isUpdating())
				counts[i] += "(u)";
			if(state.isRendering())
				counts[i] += "(r)";
			i++;
		}
		return "[" + counts[0] + " " + counts[1] + " " + counts[2] + "]";
	}
	
	
	public int getPreviousStateId(){
		int updatingFrame = getUpdatingState().getFrameCount(); //highest count
		for(RenderState s: getRenderStates()){
			if(s.getFrameCount() == updatingFrame-1){
				return s.getId();
			}
		}
		
		return -1;
	}
	
	public abstract void resized(float width, float height);
	
	public RequestList getRequestList(){
		return requestList;
	}
	
	public RenderState getUpdatingState(){
		return renderStates[RenderState.getUpdatingId()];
	}
	
	public RenderState getRenderingState(){
		return renderStates[RenderState.getRenderingId()];
	}
	
	public RenderState getUpToDateState(){
		return renderStates[RenderState.getUpToDateId()];
	}
	
	public RenderState[] getRenderStates(){
		return renderStates;
	}
	



}
