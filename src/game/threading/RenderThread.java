package game.threading;

import static org.lwjgl.opengl.GL11.*;
import game.Game;
import game.RenderState;
import game.State;
import game.world.gui.graphics.Graphics;

import java.util.ArrayList;

import main.Main;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RenderThread implements Runnable{
	
	private ThreadManager threadManager;
	
	private Graphics g;
	
	public RenderThread(ThreadManager threadManager){
		this.threadManager = threadManager;
	}
	
	/**
	 * OpenGL Initialization goes here.
	 */
	private void setupOpenGL(){
	    try{
	    	Main.debugPrint("Setting up display");
	        Display.setDisplayMode(new DisplayMode(0, 0));
	        Display.create();
	        setDisplayMode(Game.width, Game.height, Game.fullscreen);
	        Display.setResizable(true);
	    }catch (LWJGLException e){
	        e.printStackTrace();
	        System.exit(-1); 
	    }
	    
	    Main.debugPrint("Setting up OpenGL");
	    
	    //Setup OpenGL
	    perspective3D(); //Starting perspective
	    
	    //Initialize graphics class
	    g = new Graphics();
	    g.init();
	    
	    //hide the mouse
	    Mouse.setGrabbed(true);
	}
	
	public static void perspective3D(){
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	    //glViewport(0,0,Display.getWidth(), Display.getHeight());
	    glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
	    GLU.gluPerspective((float) Game.fov, Game.width / Game.height, Game.zNear, Game.zFar);
	    glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
	}
	
	public static void perspective2D(){   
	    glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
	    GLU.gluOrtho2D(0.0f, (float)Game.width, (float)Game.height, 0.0f);
	    glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
	    
	    GL11.glEnable(GL11.GL_BLEND); //For font
	    //GL11.glDisable(GL_CULL_FACE);
	}

	@Override
	public void run(){
		setupOpenGL();
		
		//Get Active State
		State activeState = threadManager.getActiveState();
		
		//Rendering loop
		Main.debugPrint("Starting renderThread loop");
		threadManager.setRenderReady(true);
		while(!Thread.interrupted()){
			//Display fps
			updateDisplayFps();
			//Check if state has been changed
			if(threadManager.getActiveStateId() != activeState.getId())
				activeState = threadManager.getActiveState();
			
		    // Clear the color information.
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			//Check if screen has been resized
			if(Display.wasResized())
				resized();
			
	        //set the modelview matrix back to the identity
	        GL11.glLoadIdentity();
		    
			RenderState latestState = activeState.getLatestState();
			//System.out.println("Rendering " + latestState.getId() + " " + activeState.getStatesCounts() + " (" + latestState.getFrameCount() + ")" + " at " + Main.getTime());
			int renderingFrame = latestState.getFrameCount();
			//Main.debugPrint("Frame states " + Arrays.toString(threadManager.getStatesCounts()));
			latestState.setRendering(true); //Must not modify a state that is being rendered
			//Main.debugPrint("Rendering RenderState " + latestState.getId() + " at " + Main.getTime());
			
			activeState.render(g); //RENDER
			
			latestState.setRendering(false);
			Display.update();
				
			//Check if got something new to render || sleep while not
			if(activeState.getLatestState().getFrameCount() == renderingFrame){
				//Main.debugPrint("No new stuff to render at " + Main.getTime());
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
		Main.debugPrint("Ending game");
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
