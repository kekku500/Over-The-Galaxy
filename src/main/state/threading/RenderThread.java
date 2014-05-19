package main.state.threading;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import graphics.Graphics2D;
import graphics.Graphics3D;
import graphics.gui.GameUserInterface;
import input.LWJGLInput;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import main.Config;
import main.state.Game;
import main.state.RenderState;
import main.state.State;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import resources.Resources;
import resources.texture.Spritesheet;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

/**
 * Initializes OpenGL display, resources, input,  gui, all states. 
 * After initialization begins calling current active state render method (loop).
 */

public class RenderThread implements Runnable{
	
	private ThreadManager threadManager;
	
	public static Spritesheet spritesheet;
	
	private GameUserInterface gameUI;
	private ThemeManager theme;
	private GUI gui;
	
	private LWJGLInput input;
	private LWJGLRenderer renderer;
	
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
	        Display.setVSyncEnabled(true);
	        Display.create();
	        setDisplayMode(Config.DISPLAY_WIDTH, Config.DISPLAY_HEIGHT, Config.DISPLAY_FULLSCREEN);
	        Display.setLocation(0, 0);
	        Display.setResizable(true);
	    }catch (LWJGLException e){
	        e.printStackTrace();
	        System.exit(-1); 
	    }
	    
	    //System.out.println(GL11.glGetInteger(GL11.GL_VENDOR));
	    
	    input = new LWJGLInput();
	    
		try{
			renderer = new LWJGLRenderer();
			
			renderer.setViewport(0, 0, Config.DISPLAY_WIDTH, Config.DISPLAY_HEIGHT);
			gameUI = new GameUserInterface();
			gui = new GUI(gameUI, renderer, input);
			gui.setTheme("");
			URL f = new File("res/ui/simple.xml").toURI().toURL();
			//theme = ThemeManager.createThemeManager(f, renderer);
			gui.applyTheme(theme);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(!Config.NO_GAME_RENDER){
		    Resources.loadResources(Resources.RESOURCESPATH);
		    
		    spritesheet = new Spritesheet("HUD2.png",75);
		    
		    Graphics3D.init();
		    Graphics3D.resized(Config.VIEWPORT_WIDTH, Config.VIEWPORT_HEIGHT);
		    Graphics2D.init();
		}
	}
	

	@Override
	public void run(){
		State activeState = null;

			init();
			
			//initialize all states
			for(State state: threadManager.getStates()){
				state.setGameUI(gameUI);
				state.setInput(input);
				state.init();
			}
			
			activeState = threadManager.getActiveState();
			
			Game.println("Starting renderThread loop");
			threadManager.setRenderReady(true);
			synchronized (threadManager.updateThread) {	
				//update thread ready = update thread sleeping, so interrupt
				if(threadManager.isUpdateReady()){

					System.out.println("notifying update thread");
					threadManager.updateThread.notify();

					//threadManager.updateThread.interrupt();
				}
			}
		while(threadManager.updateThread.isAlive()){
			//updateDisplayFps();
			
			//Check if state has been changed
			if(threadManager.getActiveStateId() != activeState.getId()){
				activeState = threadManager.getActiveState();
				//activeState.callRenderInit();
			}

			//Check if screen has been resized
			if(Display.wasResized()){
				if(!Config.NO_GAME_RENDER){
					
					activeState.resized(Display.getWidth(), Display.getHeight());
				}
				if(Config.MAP_EDITOR_MODE)
					renderer.setViewport(0, 0, Display.getWidth(), Display.getHeight());
			}
		    
			//Get state ready for rendering
			RenderState latestState = activeState.getLatestState();
			int renderingFrame = latestState.getFrameCount();
			latestState.setRendering(true);
			
			//Prepare input for reading
			activeState.getInput().renderBegin(); 
			
			if(!Config.NO_GAME_RENDER){
				activeState.render(); //RENDER
			}
			if(Config.MAP_EDITOR_MODE){
				if(Config.NO_GAME_RENDER){
					Graphics2D.perspective2D(Display.getWidth(), Display.getHeight());
					glClear(GL_COLOR_BUFFER_BIT);
				}
				
				gui.update();
			}
			Display.update();
			
			//clear up checked input
			activeState.getInput().renderEnd();
			latestState.setRendering(false);
			
			//Nothing new to render? then wait
			if(activeState.getLatestState().getFrameCount() == renderingFrame){
				synchronized (threadManager.renderThread) {
					try {
						threadManager.renderThread.wait();
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
			}
		}
		Game.println("Render thread stopped");
		
		endGame();
	}
	
	public void endGame(){
		Game.println("Cleaning up resources");
		dispose(); //Clean up GPU
		gui.destroy();
		theme.destroy();
		Display.destroy();
		Game.println("Game ended successfully!");
		System.exit(0);
	}
	
	/**
	 * Clears up memory
	 */
	private void dispose(){
		if(!Config.NO_GAME_RENDER){
			Resources.destoryResources();
			Graphics3D.dispose();
			Graphics2D.dispose();
		}

		
		for(State state: threadManager.getStates()){
			state.dispose();
		}
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
