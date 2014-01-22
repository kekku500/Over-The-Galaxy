package threading;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import game.State;

import java.util.ArrayList;
import java.util.PriorityQueue;

import main.Main;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;


public abstract class ThreadManager {
	
	public static ThreadManager threadManager; //Static reference to this object
	
	private static ArrayList<State> states = new ArrayList<State>();
	private int activeStateId = -1;
	
	Thread updateThread = new Thread(new UpdateThread(this));
	Thread renderThread = new Thread(new RenderThread(this));
	
	//True if threads are starting loop
	private boolean renderThreadReady = false;
	private boolean updateThreadReady = false;
	
	//If false, renderThread sleeps
	public static boolean newStuffToRender = true;
	
	public ThreadManager(String title){
		Main.debugPrint("ThreadManager constructor");
		
		//Initial window title
		Display.setTitle(title);
	}
	
	public int getActiveStateId(){
		return activeStateId;
	}
	
	public void enterState(int stateId){
		activeStateId = stateId;
	}
	
	public State getState(int stateId){
		for(State state: states)
			if(state.getId() == stateId)
				return state;
		return null;
	}
	
	public static void addState(State state){
		states.add(state);
	}
	
	public static ArrayList<State> getStates(){
		return states;
	}
	
	public boolean isRenderReady(){
		return renderThreadReady;
	}
	
	public void setRenderReady(boolean b){
		renderThreadReady = b;
	}
	
	public boolean isUpdateReady(){
		return updateThreadReady;
	}
	
	public void setUpdateReady(boolean b){
		updateThreadReady = b;
	}
	

	public void endGame(){
		//Update thread has stopped, also stop rendering
		renderThread.interrupt();
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
	
	/**
	 * Initialize all states and start one
	 */
	public abstract void initStates();
	
	


	

	
}
