package main;

public class Config {
	
	/** ----------------------------- GENERAL ---------------------------------- */
	
	public static final String VERSION = "2.0";
	
	public static boolean DEBUG_MODE = true;
	
	/** ------------------------ GRAPHICS SETTINGS ----------------------------- */
	
	/** How many frames are rendered per second */
	public static int FPS = 60;
	
	/** In how big steps physics is updated, smaller equals more accurate physics */
	public static float TARGET_STEP = 1f/FPS;
	
	/** OpenGL display screen mode */
	public static boolean DISPLAY_FULLSCREEN = false;
	
	/** OpenGL display screen width */
	public static int DISPLAY_WIDTH = 800; 
	
	/** OpenGL display screen height */
	public static int DISPLAY_HEIGHT = 600; 
	
	/** Game camera viewport width */
	public static int VIEWPORT_WIDTH = 800;
	
	/** Game camera viewport height*/
	public static int VIEWPORT_HEIGHT = 600;
	
	/** Game camera frustum vertical angle in degrees */
	public static int FOV = 45;
	
	/** Game camera frustum zNear (how close to the camera objects are still rendered */
	public static float Z_NEAR = 1f;
	
	/** Game camera frustum zFar (how far player can see) */
	public static float Z_FAR = 3000f;
	
	
	/** ------------------------------ MAP EDITOR  ----------------------------- */
	
	public static boolean MAP_EDITOR_MODE = false;
	
	/**Disables updating and rendering of the game for better ui debugging*/
	public static final boolean NO_GAME_RENDER = false;

}
