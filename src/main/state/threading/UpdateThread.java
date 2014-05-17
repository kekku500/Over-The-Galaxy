package main.state.threading;


import input.InputConfig;
import main.Config;
import main.state.Game;
import main.state.RenderState;
import main.state.State;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

/**
 * Calls current active state update method. Game states (intro, menu, ingame) consist
 * of 3 renderstates (do no confuse state and renderstate). State has 3 RenderStates; updating
 * rendering and uptodate.
 */
public class UpdateThread implements Runnable{
	
	private ThreadManager threadManager;
	
	public static boolean SLOWDOWNALERT = false;
	
	public UpdateThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}

	@Override
	public void run(){
		System.out.println("In run of update");
		State activeGameState = threadManager.getActiveState();
		
		threadManager.setUpdateReady(true);
		if(!threadManager.isRenderReady()){
			Game.println("Update thread is waiting for render thread to get ready...");
			synchronized (threadManager.updateThread) {
				try {
					threadManager.updateThread.wait();
				} catch (InterruptedException e) {
					e.getStackTrace();
				}
			}
		}
		
		Game.println("Starting updateThread loop");
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(InputConfig.instantQuit)){
			if(threadManager.getActiveStateId() != activeGameState.getId()){
				activeGameState = threadManager.getActiveState();
			}
			float dt = getDelta();

			RenderState oldestState = activeGameState.getOldestState();
			RenderState latestState = activeGameState.getLatestState();

			oldestState.setUpdating(true);
			oldestState.setFrameCount(latestState.getFrameCount()+1);
			
			activeGameState.beginUpdate(dt);

			oldestState.setUpdating(false);

			//Wake up RenderThread if it's sleeping
			if(RenderState.rendering() == -1){ 
				synchronized (threadManager.renderThread) {
					threadManager.renderThread.notify();
				}
				//threadManager.renderThread.interrupt();
			}

			Display.sync(Config.FPS); 
		}
		
		Game.println("Update thread stopped!");
		//Also stop render thread if it's sleeping
		threadManager.renderThread.interrupt();
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