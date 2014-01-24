package threading;


import game.Game;
import game.State;
import game.world.RenderState;
import main.Main;

import org.lwjgl.opengl.Display;

public class UpdateThread implements Runnable{
	
	private ThreadManager threadManager;
	private float accumulator;
	
	public UpdateThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}

	@Override
	public void run(){
		//Get active State
		State activeState = threadManager.getActiveState();
		
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
			if(threadManager.getActiveStateId() != activeState.getId()){
				activeState = threadManager.getActiveState();
			}
			float dt = getDelta();

			RenderState oldestState = activeState.getOldestState();
			RenderState latestState = activeState.getLatestState();

			//System.out.println("Updating " + activeState.getId() + " " + activeState.getStatesCounts() + 
			//		" (" + oldestState.getFrameCount() + "->" + (latestState.getFrameCount()+1) + ")");
			//Main.debugPrint("Updating " + oldestState.getId() + " at " + Main.getTime());
			
			//Start updating
			oldestState.setUpdating(true);
			oldestState.setFrameCount(latestState.getFrameCount()+1);
			
			accumulator += dt; //How much time must be updated
			if(accumulator > Game.targetStep){ //Behind from real time, no time for thread sleep
				int framesBehind = (int)(accumulator / (Game.targetStep)); // How many frames is simulation behind
				//System.out.println(framesBehind + " <- " + accumulator);
				activeState.update(Game.targetStep*framesBehind);
				accumulator -= Game.targetStep*framesBehind;
			}else{ //Can update simulation ahead from real time and let update thread sleep
				activeState.update(Game.targetStep);
				accumulator -= Game.targetStep;			
			}
			//System.out.println("realTime " + realTime + " updatedTime " + updatedTime + " framesBehind");
			
			oldestState.setUpdating(false);
			activeState.setStuffToRender(true);
			//Updating done
			
			//Main.debugPrint("New stuff to render at " + Main.getTime());
			Display.sync(Game.fps); //Sleep until during the free time which is left from updating
			try {
				Thread.sleep((int)(1000*Game.targetStep));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Display has been closed
		threadManager.endGame();
	}
	
	//Currently not in use
	private boolean firstRun = true;
	private float lastFrame = Main.getTime();
	/**
	 * @return Returns time last frame took updating in getTime metric system
	 */
	public float getDelta(){
		if(firstRun){
			lastFrame = Main.getTime();
			firstRun = false;
			return 0;
		}
		float time = Main.getTime();
		float delta = (time - lastFrame);
		lastFrame = time;
		return delta;
	}

}