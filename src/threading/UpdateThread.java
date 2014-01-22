package threading;


import static org.lwjgl.opengl.GL11.*;
import main.Main;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

import game.Game;
import game.State;

public class UpdateThread implements Runnable{
	
	private ThreadManager threadManager;
	private float accumulator;
	
	public UpdateThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}

	@Override
	public void run(){
		State activeState = threadManager.getState(threadManager.getActiveStateId());
		
		threadManager.setUpdateReady(true);
		while(!threadManager.isRenderReady()){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Main.debugPrint("Starting updateThread loop");
		while(!Display.isCloseRequested()){
			//Check if state has changed
			if(threadManager.getActiveStateId() != activeState.getId())
				activeState = threadManager.getState(threadManager.getActiveStateId());
			
			float dt = getDelta();
			accumulator += dt; 
			while(accumulator >= Game.targetStep){
				activeState.update(Game.targetStep);
				threadManager.newStuffToRender = true;
				Main.debugPrint("New stuff to render at " + Main.getTime());
				accumulator -= Game.targetStep;
			}
			Display.sync(Game.fps);
			
		}
		
		threadManager.endGame();
	}
	
	private float lastFrame = Main.getTime();
	/**
	 * @return Returns time last frame took updating in milliseconds
	 */
	public float getDelta(){
		float time = Main.getTime();
		float delta = (time - lastFrame);
		lastFrame = time;
		return delta;
	}

}
