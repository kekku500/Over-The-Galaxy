package game;

import game.threading.ThreadManager;
import main.IntroState;
import main.PlayState;

import org.lwjgl.Sys;

public class Game extends ThreadManager{
	
	public static boolean debug = true;
	
	public static final String VERSION = "0.53";
	
	public static final String RESOURCESPATH = "res\\";
	public static final String MODELPATH = "models\\";
	public static final String SHADERPATH = "shaders\\";

	public static int fps = 500;
	public static float targetStep = 1f/fps; //16 milliseconds is one frame if fps is 60
	public static int width = 800;
	public static int height = 600;
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
		Game.print("Starting the threads");
		startThreads();
	}
	
	public void initStates(){
		getState(PLAYGAME).init();
		getState(INTRO).init();
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
	public static <T> void print(T t){
		if(Game.debug)
			System.out.println(t);
	}

}
