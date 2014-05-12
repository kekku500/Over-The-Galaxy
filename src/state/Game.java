package state;

import main.IntroState;
import main.PlayState;

import org.lwjgl.Sys;

import state.threading.ThreadManager;

public class Game extends ThreadManager{
	
	public static final boolean MAP_EDITOR_MODE = false;
	
	/**Disables updating and rendering of the game for better
	 * ui debugging
	 */
	public static final boolean NO_RENDER = false;
	
	public static boolean debug = true;
	
	public static final String VERSION = "1.9";
	
	public static int fps = 60;
	public static float targetStep = 1f/fps; //16 milliseconds is one frame if fps is 60
	public static int displayWidth = 800; //Display screen width
	public static int displayHeight = 600; //Display screen height
	public static int viewportWidth = 800; //camera width
	public static int viewportHeight = 600; //camera height
	public static boolean fullscreen = false;
	public static int fov = 45;
	public static float zNear = 1f;
	public static float zFar = 3000;
	public static float shadowZFar = 300;

	//States
	private final int PLAYGAME = 0;
	private final int INTRO = 1;
	
	public Game(){
		super("Over-The-Galaxy v" + VERSION);
		
		//Create states
		addState(new PlayState(PLAYGAME));
		addState(new IntroState(INTRO));
	}
	
	public void start(){
		Game.println("Starting the threads");
		startThreads();
	}
	
	public void initStates(){
		enterState(PLAYGAME);
	}
	
	/**
	* Get the time in seconds
	*
	* @return The system time in seconds
	*/
	public static float getTime(){
		return Sys.getTime() * 1f / Sys.getTimerResolution() / 1f;
	}
	
	/**
	 * Prints t in console if Game.debug == true
	 * @param t
	 */
	public static <T> void println(T t){
		if(Game.debug)
			System.out.println(t);
	}
	
	public static <T> void print(T t){
		if(Game.debug)
			System.out.print(t);
	}

}
