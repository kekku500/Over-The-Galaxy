package main.state.threading;

import java.util.Set;

import main.state.Game;
import main.state.State;

import org.lwjgl.opengl.Display;

import utils.HashSet;


/**
 * Handles communication of states between update and render thread.
 */
public abstract class ThreadManager {
	
	//States
	private static Set<State> states = new HashSet<State>();
	private State activeState;
	
	//Threading
	Thread updateThread = new Thread(new UpdateThread(this));
	Thread renderThread = new Thread(new RenderThread(this));
	
	//True if threads are starting loop
	private boolean renderThreadRunning = false;
	private boolean updateThreadRunning = false;

	public ThreadManager(String title){
		Display.setTitle(title);
	}
	
	public void init(){
		//Initialize states
		Game.println("Initializing states");
		initStates();
	}
	
	//Contains main game loop
	public void startThreads(){
		Game.println("Game initialization");
		init();
		
		updateThread.start();
		renderThread.start();
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
	public void enterState(int stateId){
		activeState = getState(stateId);
	}
	
	public void setUpdateReady(boolean b){
		updateThreadRunning = b;
	}
	
	public void setRenderReady(boolean b){
		renderThreadRunning = b;
	}
	
	//GET
	public State getState(int stateId){
		for(State state: states)
			if(state.getId() == stateId)
				return state;
		return null;
	}
	
	public Set<State> getStates(){
		return states;
	}
	
	public int getActiveStateId(){
		return activeState.getId();
	}
	
	public boolean isUpdateReady(){
		return updateThreadRunning;
	}
	
	public boolean isRenderReady(){
		return renderThreadRunning;
	}
	
	public State getActiveState(){
		return activeState;
	}
	
}