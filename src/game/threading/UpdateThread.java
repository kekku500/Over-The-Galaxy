package game.threading;


import game.Game;
import game.RenderState;
import game.State;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class UpdateThread implements Runnable{
	
	private ThreadManager threadManager;
	private float accumulator;
	
	public static boolean SLOWDOWNALERT = false;
	
	public UpdateThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}

	@Override
	public void run(){
		//Get active State
		State activeGameState = threadManager.getActiveState();
		
		//Wait for render thread to be ready
		threadManager.setUpdateReady(true);
		while(!threadManager.isRenderReady()){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Game.print("Starting updateThread loop");
		
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			//Check if state has changed
			if(threadManager.getActiveStateId() != activeGameState.getId()){
				activeGameState = threadManager.getActiveState();
			}
			float dt = getDelta();

			RenderState oldestState = activeGameState.getOldestState();
			RenderState latestState = activeGameState.getLatestState();

			//System.out.println("Updating " + oldestState.getId() + " " + activeState.getStatesCounts() + 
			//		" (" + oldestState.getFrameCount() + "->" + (latestState.getFrameCount()+1) + ")" + " at " + Main.getTime());

			oldestState.setUpdating(true);
			oldestState.setFrameCount(latestState.getFrameCount()+1);

			accumulator += dt; //How much time must be updated
			if(accumulator > Game.targetStep){ //Behind from real time, no time for thread sleep
				int framesBehind = (int)(accumulator / (Game.targetStep)); // How many frames is simulation behind
				//System.out.println(framesBehind + " <- " + accumulator);
				if(framesBehind >= 10)
					SLOWDOWNALERT = true;
				else
					SLOWDOWNALERT = false;
				activeGameState.update(Game.targetStep*framesBehind);
				accumulator -= Game.targetStep*framesBehind;
			}else{ //Can update simulation ahead from real time and let update thread sleep
				SLOWDOWNALERT = false;
				activeGameState.update(Game.targetStep);
				accumulator -= Game.targetStep;		
			}
			//System.out.println("realTime " + realTime + " updatedTime " + updatedTime + " framesBehind");
			oldestState.setUpdating(false);
			activeGameState.setStuffToRender(true);
			//Updating done
			//System.out.println("Done updating " + oldestState.getId());
			//Game.print("New stuff to render at " + Main.getTime());
			Display.sync(Game.fps); //Sleep until during the free time which is left from updating

		}
		
		//Display has been closed
		threadManager.endGame();
	}
	
	//Currently not in use
	private boolean firstRun = true;
	private float lastFrame = Game.getTime();
	/**
	 * @return Returns time last frame took updating in getTime metric system
	 */
	public float getDelta(){
		if(firstRun){
			lastFrame = Game.getTime();
			firstRun = false;
			return 0;
		}
		float time = Game.getTime();
		float delta = (time - lastFrame);
		lastFrame = time;
		return delta;
	}

}