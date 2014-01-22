package threading;

import game.State;
import game.world.RenderState;

import java.util.ArrayList;

import main.Main;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public abstract class ThreadManager {
	
	//States
	private static ArrayList<State> states = new ArrayList<State>();
	private int activeStateId = -1;
	
	//Threading
	Thread updateThread = new Thread(new UpdateThread(this));
	Thread renderThread = new Thread(new RenderThread(this));
	//True if threads are starting loop
	private boolean renderThreadReady = false;
	private boolean updateThreadReady = false;
	//If false, renderThread sleeps
	private boolean newStuffToRender = true;
	
	//Multithreading rendering handling (synchronizing update and render threads)
	RenderState[] renderStates = {new RenderState(0), new RenderState(1), new RenderState(2)};
	
	public ThreadManager(String title){
		//Initial window title
		Display.setTitle(title);
	}
	
	public void init(){
		//Initialize states
		Main.debugPrint("Initializing states");
		initStates();
	}
	
	//Contains main game loop
	public void startThreads(){
		Main.debugPrint("Game initialization");
		init();
		
		//Starting threads
		renderThread.start();
		updateThread.start();
		
		//Create thread manager loop here!
		while(true){
			try { //Wait for render thread to get ready
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//Change states
			if(isRenderReady()){
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
					break;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0)){
					Main.debugPrint("Set state to 0");
					enterState(0);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)){
					Main.debugPrint("Set state to 1");
					enterState(1);
				}
			}
		}
	}
	
	public void endGame(){
		//Update thread has stopped, also stop rendering
		renderThread.interrupt();
	}
	
	public static void addState(State state){
		states.add(state);
	}
	
	//Abstract
	/**
	 * Initialize all states and start one
	 */
	public abstract void initStates();
	
	
	//SET
	/**
	 * Overview of all RenderStates frame counts.
	 * @return
	 */
	public int[] getStatesCounts(){
		int[] counts = {0,0,0};
		int i = 0;
		for(RenderState state: renderStates){
			counts[i] = state.getFrameCount();
			i++;
		}
		return counts;
	}
	
	public void setStuffToRender(boolean b){
		newStuffToRender = b;
	}
	
	public void enterState(int stateId){
		activeStateId = stateId;
	}
	
	public void setUpdateReady(boolean b){
		updateThreadReady = b;
	}
	
	public void setRenderReady(boolean b){
		renderThreadReady = b;
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
	
	public State getState(int stateId){
		for(State state: states)
			if(state.getId() == stateId)
				return state;
		return null;
	}
	
	public ArrayList<State> getStates(){
		return states;
	}
	
	public RenderState[] getRenderStates(){
		return renderStates;
	}
	
	public boolean newStuffToRender(){
		return newStuffToRender;
	}
	
	public int getActiveStateId(){
		return activeStateId;
	}
	
	public boolean isUpdateReady(){
		return updateThreadReady;
	}
	
	public boolean isRenderReady(){
		return renderThreadReady;
	}
	
}