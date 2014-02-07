package game;

import main.IntroState;
import main.Main;
import main.PlayState;
import game.Game;
import game.threading.ThreadManager;

public class Game extends ThreadManager{
	

	public static int fps = 60; //framerate
	public static float targetStep = 1f/fps; //16 milliseconds is one frame if fps is 60
	public static int width = 800;
	public static int height = 600;
	public static boolean fullscreen = false;
	public static int fov = 45;
	public static float zNear = 0.001f;
	public static float zFar = 400;

	//States
	private final int PLAYGAME = 0;
	private final int INTRO = 1;
	
	public Game(String title){
		super(title);
		addState(new PlayState(PLAYGAME));
		addState(new IntroState(INTRO));
	}
	
	public void start(){
		Main.debugPrint("Starting the game");
		startThreads();
	}
	
	public void initStates(){
		getState(PLAYGAME).init();
		getState(INTRO).init();
		enterState(PLAYGAME);
	}

}
