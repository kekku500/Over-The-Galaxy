package threading;


import game.Game;
import game.State;
import game.world.RenderState;
import main.Main;

import org.lwjgl.opengl.Display;

public class UpdateThread implements Runnable{
	
	private ThreadManager threadManager;
	
	public UpdateThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}

	@Override
	public void run(){
		//Get active State
		State activeState = threadManager.getState(threadManager.getActiveStateId());
		
		//Wait for render thread to be ready
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
			
			
			RenderState oldestState = threadManager.getOldestState();
			RenderState latestState = threadManager.getLatestState();
			//Main.debugPrint("Updating " + oldestState.getId() + " at " + Main.getTime());
			oldestState.setUpdating(true);
			oldestState.setFrameCount(latestState.getFrameCount()+1);
			
			activeState.update(Game.targetStep); //UPDATE
			
			oldestState.setUpdating(false);
			threadManager.setStuffToRender(true);
			//Main.debugPrint("New stuff to render at " + Main.getTime());
			Display.sync(Game.fps); //Sleep until fps is 60
		}
		
		//Display has been closed
		threadManager.endGame();
	}
	
	//Currently not in use
	private float lastFrame = Main.getTime();
	/**
	 * @return Returns time last frame took updating in getTime metric system
	 */
	public float getDelta(){
		float time = Main.getTime();
		float delta = (time - lastFrame);
		lastFrame = time;
		return delta;
	}

}