package input;

import org.lwjgl.input.Keyboard;

public class InputConfig {
	
	//Player
	public static int playerAccelerate = Keyboard.KEY_W;
	public static int playerMoveRight = Keyboard.KEY_D;
	public static int playerMoveLeft = Keyboard.KEY_A;
	public static int playerRollLeft = Keyboard.KEY_Q;
	public static int playerRollRight = Keyboard.KEY_E;
	
	//UpdateThread
	public static int instantQuit = Keyboard.KEY_ESCAPE;
	
	//Controller
	public static int translationBoost = Keyboard.KEY_LSHIFT;
	public static int translationForward = Keyboard.KEY_W;
	public static int translationBackward = Keyboard.KEY_S;
	public static int translationRight = Keyboard.KEY_D;
	public static int translationLeft = Keyboard.KEY_A;
	public static int translationUp = Keyboard.KEY_SPACE;
	public static int translationDown = Keyboard.KEY_LCONTROL;

}
