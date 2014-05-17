package main.state;

import main.Config;
import main.IntroState;
import main.PlayState;
import main.state.threading.ThreadManager;

import org.lwjgl.Sys;

/**
 * Game prepares all game states such as intro, menu, ingame and starts selected state.
 * This class starts update and render thread using start() method.
 */
public class Game extends ThreadManager{

	private final int PLAYGAME = 0;
	private final int INTRO = 1;
	
	/**
	 * Creates all game states
	 */
	public Game(){
		super("Over-The-Galaxy v" + Config.VERSION);
		
		//Create states
		addState(new PlayState(PLAYGAME));
		addState(new IntroState(INTRO));
	}
	
	/**
	 * Starting the threads
	 */
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
		if(Config.DEBUG_MODE)
			System.out.println(t);
	}
	
	public static <T> void print(T t){
		if(Config.DEBUG_MODE)
			System.out.print(t);
	}

}
