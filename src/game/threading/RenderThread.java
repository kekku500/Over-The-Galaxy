package game.threading;

import static org.lwjgl.opengl.GL11.glViewport;
import game.Game;
import game.RenderState;
import game.State;
import game.world.graphics.Graphics3D;
import game.world.gui.graphics.Graphics2D;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class RenderThread implements Runnable{
	
	private ThreadManager threadManager;
	
	private Graphics2D graphics2D;
	
	public RenderThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}
	
	/**
	 * OpenGL Initialization goes here.
	 */
	private void init(){
	    try{
	    	Game.print("Setting up display");
	        Display.setDisplayMode(new DisplayMode(0, 0));
	        Display.setVSyncEnabled(true);
	        Display.create();
	        setDisplayMode(Game.width, Game.height, Game.fullscreen);
	        Display.setLocation(0, 0);
	        Display.setResizable(true);
	    }catch (LWJGLException e){
	        e.printStackTrace();
	        System.exit(-1); 
	    }
	    
	    Game.print("Setting up OpenGL");
	   	Graphics3D.init();
	    
	    //Initialize 2d graphics class
	    graphics2D = new Graphics2D(); //used for texts
	    graphics2D.init();
	    
	    //hide the mouse
	    Mouse.setGrabbed(true);
	}
	

	@Override
	public void run(){
		init();
		
		//Get Active State
		State activeState = threadManager.getActiveState();
		
		//Rendering loop
		Game.print("Starting renderThread loop");
		threadManager.setRenderReady(true);
		while(!Thread.interrupted()){
			//Display fps
			updateDisplayFps();
			//Check if state has been changed
			if(threadManager.getActiveStateId() != activeState.getId())
				activeState = threadManager.getActiveState();

			//Check if screen has been resized
			if(Display.wasResized())
				resized();
		    
			RenderState latestState = activeState.getLatestState();
			//System.out.println("Rendering " + latestState.getId() + " " + activeState.getStatesCounts() + " (" + latestState.getFrameCount() + ")" + " at " + Main.getTime());
			int renderingFrame = latestState.getFrameCount();
			//Game.print("Frame states " + Arrays.toString(threadManager.getStatesCounts()));
			latestState.setRendering(true); //Must not modify a state that is being rendered
			//Game.print("Rendering RenderState " + latestState.getId() + " at " + Main.getTime());
			
			activeState.render(graphics2D); //RENDER
			
			latestState.setRendering(false);
			Display.update();
				
			//Check if got something new to render || sleep while not
			if(activeState.getLatestState().getFrameCount() == renderingFrame){
				//Game.print("No new stuff to render at " + Main.getTime());
				activeState.setStuffToRender(false);
			}
			while(!Thread.interrupted() && //Thread can still run
					!activeState.newStuffToRender() &&  //Nothing new to render
					threadManager.getActiveStateId() == activeState.getId()){ //State hasn't changed
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					//End while sleeping
					endGame();
				}
			}
			
		}
		//End normally
		endGame();
	}
	
	public void endGame(){
		Game.print("Ending game");
		dispose(); //Clean up GPU
		Display.destroy();
		System.exit(0);
	}
	
	/**
	 * Clears up memory
	 */
	private void dispose(){
		for(State state: threadManager.getStates()){
			state.dispose();
		}
	}
	
	public void resized(){
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
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
	private boolean setDisplayMode(int width, int height, boolean fullscreen){
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
	            System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
	            return false;
	        }

	        // Set the DisplayMode we've found
	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);

	        System.out.println("Selected DisplayMode: " + targetDisplayMode.toString());

	        // Generate a resized event
	        resized();

	        return true;
	    }
	    catch (LWJGLException e){
	        System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
	    }

	    return false;
	}

}
