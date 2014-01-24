package threading;

import game.State;

import java.util.ArrayList;

import main.Main;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public abstract class ThreadManager {
	
	//States
	private static ArrayList<State> states = new ArrayList<State>();
	private State activeState;
	
	//Threading
	Thread updateThread = new Thread(new UpdateThread(this));
	Thread renderThread = new Thread(new RenderThread(this));
	
	//True if threads are starting loop
	private boolean renderThreadReady = false;
	private boolean updateThreadReady = false;

	public ThreadManager(String title){
		System.out.println("threadmanager constructor");
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
		//CODE BELOW IS JUST FOR TEsTING
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
				if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0) && getActiveStateId() != 0){
					Main.debugPrint("Set state to 0");
					enterState(0);
				}else if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1) && getActiveStateId() != 1){
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
	public void enterState(int stateId){
		activeState = getState(stateId);
	}
	
	public void setUpdateReady(boolean b){
		updateThreadReady = b;
	}
	
	public void setRenderReady(boolean b){
		renderThreadReady = b;
	}
	
	//GET
	public State getState(int stateId){
		for(State state: states)
			if(state.getId() == stateId)
				return state;
		return null;
	}
	
	public ArrayList<State> getStates(){
		return states;
	}
	
	public int getActiveStateId(){
		return activeState.getId();
	}
	
	public boolean isUpdateReady(){
		return updateThreadReady;
	}
	
	public boolean isRenderReady(){
		return renderThreadReady;
	}
	
	public State getActiveState(){
		return activeState;
	}
	
}