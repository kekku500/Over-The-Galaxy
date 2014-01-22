package threading;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

import java.util.ArrayList;
import java.util.PriorityQueue;

import game.Game;
import game.State;
import main.Main;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class RenderThread implements Runnable{
	
	private ThreadManager threadManager;
	
	public RenderThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}
	
	private void setupOpenGL(){
	    try{
	    	Main.debugPrint("Setting up display");
	        Display.setDisplayMode(new DisplayMode(0, 0));
	        Display.create();
	        setDisplayMode(800, 600, false);
	        Display.setResizable(true);
	    }catch (LWJGLException e){
	        e.printStackTrace();
	        System.exit(-1); // There is no point in running without hardware acceleration right?
	    }
	    
	    Main.debugPrint("Setting up OpenGL");
	    // Setup OpenGL
	    glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();

	    glMatrixMode(GL_MODELVIEW);
	    glOrtho(0, 800, 600, 0, 1, -1);
	    glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	@Override
	public void run(){
		setupOpenGL();
		
		//Rendering loop
		Main.debugPrint("Starting renderThread loop");
		State activeState = threadManager.getState(threadManager.getActiveStateId());
		threadManager.setRenderReady(true);
		

		while(!Thread.interrupted()){
			updateDisplayFps();
			//Check if state has been changed
			if(threadManager.getActiveStateId() != activeState.getId())
				activeState = threadManager.getState(threadManager.getActiveStateId());

		    // Clear the color information.
		    glClear(GL_COLOR_BUFFER_BIT);
		    // Set the color to white.
		    glColor3f(1, 1, 1);
		    
		    activeState.render();
			
			Display.update();
			
			//Check if got something new to render, sleep while not
			while(!Thread.interrupted() && !ThreadManager.newStuffToRender){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
					endGame();
				}
			}
			
		}
		
		endGame();
	}
	
	private float lastFPS = Main.getTime();
	private int fps = 0;
	private ArrayList<Integer> allFps = new ArrayList<Integer>();
	private int howManyFirstSkip = 5; //Skip first few fps to get better average accuracy
	private void updateDisplayFps(){
		if(Main.getTime() - lastFPS > 1){
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
	
	
	public void endGame(){
		Main.debugPrint("Ending game");
		dispose(); //Clean up GPU
		Display.destroy();
		System.exit(0);
	}
	
	private void dispose(){
		threadManager.getState(threadManager.getActiveStateId()).dispose();
	}
	
	
	public void resized(){
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
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
