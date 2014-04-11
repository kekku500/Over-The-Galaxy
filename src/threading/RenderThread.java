package threading;

import static org.lwjgl.opengl.GL11.glViewport;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import resources.Resources;
import resources.texture.Spritesheet;
import state.Game;
import state.RenderState;
import state.State;
import world.World;
import world.entity.gui.HudExample;
import world.entity.gui.hud.ShipStat;
import world.graphics.Graphics2D;
import world.graphics.Graphics3D;

public class RenderThread implements Runnable{
	
	public static int displayWidth, displayHeight;
	
	private ThreadManager threadManager;
	public static Spritesheet spritesheet;
	
	public RenderThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}
	
	/**
	 * OpenGL Initialization goes here.
	 */
	private void init(){
	    try{
	    	Game.println("Setting up display");
	        Display.setDisplayMode(new DisplayMode(0, 0));
	        //Display.setVSyncEnabled(true);
	        Display.create();
	        setDisplayMode(Game.width, Game.height, Game.fullscreen);
	        Display.setLocation(0, 0);
	        Display.setResizable(true);
	    }catch (LWJGLException e){
	        e.printStackTrace();
	        System.exit(-1); 
	    }
	    
	    //Load models
	    Resources.loadResources(Resources.RESOURCESPATH);
	    spritesheet = new Spritesheet("HUD2.png",75);
	    
	    Graphics3D.init();
	    Graphics2D.init();
	    
	    //HUD INIT EXAMPLE
	    HudExample.init();
	    ShipStat.init();
	    
	}
	

	@Override
	public void run(){
		init();
		
		//initialize all states
		for(State state: threadManager.getStates()){
			state.init();
		}
		
		//Get Active State
		State activeState = threadManager.getActiveState();
		//activeState.callRenderInit();
		
		//Rendering loop
		Game.println("Starting renderThread loop");
		threadManager.setRenderReady(true);
		while(true){
			boolean interrupted = Thread.interrupted();
			if(interrupted)
				break;
			//Display fps
			updateDisplayFps();
			//Check if state has been changed
			if(threadManager.getActiveStateId() != activeState.getId()){
				activeState = threadManager.getActiveState();
				//activeState.callRenderInit();
			}

			//Check if screen has been resized
			if(Display.wasResized()){
				resized();
			}
		    
			//Get state ready for rendering
			RenderState latestState = activeState.getLatestState();
			
			//System.out.println("Rendering " + latestState.getId() + " " + activeState.getStatesCounts() + " (" + latestState.getFrameCount() + ")" + " at " + Main.getTime());
			int renderingFrame = latestState.getFrameCount();
			//Game.print("Frame states " + Arrays.toString(threadManager.getStatesCounts()));
			latestState.setRendering(true); //Must not modify a state that is being rendered
			//Game.print("Rendering RenderState " + latestState.getId() + " at " + Main.getTime());
			activeState.render(); //RENDER
			latestState.setRendering(false);
			Display.update();
				
			//Check if got something new to render || sleep while not
			if(activeState.getLatestState().getFrameCount() == renderingFrame){
				//Game.print("No new stuff to render at " + Main.getTime());
				activeState.setStuffToRender(false);
			}
			while(!interrupted && //Thread can still run
					!activeState.newStuffToRender() &&  //Nothing new to render
					threadManager.getActiveStateId() == activeState.getId()){ //State hasn't changed
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Render thread stopped while sleeping");
					//End while sleeping
					endGame();
				}
			}
			
		}
		System.out.println("Render thread stopped normally");
		//End normally
		endGame();
	}
	
	public void endGame(){
		Game.println("Ending game");
		dispose(); //Clean up GPU
		Display.destroy();
		System.exit(0);
	}
	
	/**
	 * Clears up memory
	 */
	private void dispose(){
		Resources.destoryResources();
		Graphics3D.dispose();
		Graphics2D.dispose();
		ShipStat.dispose();
		
		//hud dispose example
		HudExample.dispose();
		for(State state: threadManager.getStates()){
			state.dispose();
		}
	}
	
	public void resized(){
		displayWidth = Display.getWidth();
		displayHeight = Display.getHeight();
		//glViewport(0, 0, displayWidth, displayHeight);
		Graphics3D.resized(displayWidth, displayHeight);
	}
	
	//Display fps counter
	private float lastFPS = Game.getTime();
	private int fps = 0;
	private ArrayList<Integer> allFps = new ArrayList<Integer>();
	private int howManyFirstSkip = 5; //Skip first few fps to get better average accuracy
	private void updateDisplayFps(){
		if(Game.getTime() - lastFPS > 1){
			allFps.add(fps);
			if(allFps.size() > howManyFirstSkip){
				int average = 0;
				for(int f: allFps.subList(howManyFirstSkip-1, allFps.size()-1)){
					average +=f;
				}
				average /= allFps.subList(howManyFirstSkip-1, allFps.size()-1).size();
				Display.setTitle("FPS: " + fps + " (" + average + ")");
			}else{
				Display.setTitle("FPS: " + fps);
			}
			fps = 0;
			lastFPS += 1;
		}
		fps++;
	}
	
	/**
	 * Sets a DisplayMode after selecting for a better one.
	 * @param width The width of the display.
	 * @param height The height of the display.
	 * @param fullscreen The fullscreen mode.
	 *
	 * @return True if switching is successful. Else false.
	 */
	public static boolean setDisplayMode(int width, int height, boolean fullscreen){
	    // return if requested DisplayMode is already set
	    if ((Display.getDisplayMode().getWidth() == width) &&
	        (Display.getDisplayMode().getHeight() == height) &&
	        (Display.isFullscreen() == fullscreen))
	        return true;

	    try{
	        // The target DisplayMode
	        DisplayMode targetDisplayMode = null;

	        if (fullscreen){
	            // Gather all the DisplayModes available at fullscreen
	            DisplayMode[] modes = Display.getAvailableDisplayModes();
	            int freq = 0;

	            // Iterate through all of them
	            for (DisplayMode current: modes){
	                // Make sure that the width and height matches
	                if ((current.getWidth() == width) && (current.getHeight() == height)){
	                    // Select the one with greater frequency
	                    if ((targetDisplayMode == null) || (current.getFrequency() >= freq)){
	                        // Select the one with greater bits per pixel
	                        if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())){
	                            targetDisplayMode = current;
	                            freq = targetDisplayMode.getFrequency();
	                        }
	                    }
	                    // if we've found a match for bpp and frequency against the 
	                    // original display mode then it's probably best to go for this one
	                    // since it's most likely compatible with the monitor
	                    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
	                        (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())){
	                        targetDisplayMode = current;
	                        break;
	                    }
	                }
	            }
	        }
	        else{
	            // No need to query for windowed mode
	            targetDisplayMode = new DisplayMode(width, height);
	        }

	        if (targetDisplayMode == null){
	        	 Game.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
	            return false;
	        }

	        // Set the DisplayMode we've found
	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);

	        Game.println("Selected DisplayMode: " + targetDisplayMode.toString());

	        // Generate a resized event

	        return true;
	    }
	    catch (LWJGLException e){
	    	Game.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
	    }

	    return false;
	}

}
