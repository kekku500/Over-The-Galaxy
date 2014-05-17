package main.state;

import java.io.Serializable;

import graphics.gui.GameUserInterface;
import input.LWJGLInput;

/**
 *  Essential properties of a state. 3 RenderState classes are used to keep track
 *  of state variables.
 */
public abstract class State{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATE_COUNT = 3;
	public static final int FIRST_STATE = 0;
	
	private RenderState[] renderStates;
	
	private GameUserInterface gameUI;
	private LWJGLInput input;
	
	public State(){
		renderStates = new RenderState[STATE_COUNT];
		for(int i = 0;i<STATE_COUNT;i++)
			renderStates[i] = new RenderState(i, (i == FIRST_STATE ? 0 : -1));
	}
	
	/**
	 * This method is called from RenderThread
	 */
	public abstract void init();
	
	/**
	 * This method is always called from UpdateThread
	 * @param dt
	 */
	public abstract void update(float dt);
	
	/**
	 * This method is always called from RenderThread
	 */
	public abstract void render();
	
	/**
	 * This method is always called from RenderThread
	 */
	public abstract void dispose();
	
	public abstract int getId();
	
	/**
	 * This method is always called from RenderThread
	 */
	public abstract void resized(float width, float height);
	
	public void setInput(LWJGLInput input){
		this.input = input;
	}
	
	public void beginUpdate(float dt){
		input.updateInputState();
			
		update(dt);
	}
	
	public LWJGLInput getInput(){
		return input;
	}
	
	public void setGameUI(GameUserInterface globalUI){
		this.gameUI = globalUI;
	}
	
	public GameUserInterface getGameUI(){
		return gameUI;
	}

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
	
	public RenderState getUpdatingState(){
		return renderStates[RenderState.updating()];
	}
	
	public RenderState getRenderingState(){
		return renderStates[RenderState.rendering()];
	}
	
	public RenderState getUpToDateState(){
		return renderStates[RenderState.uptodate()];
	}
	
	public RenderState[] getRenderStates(){
		return renderStates;
	}
	
}
