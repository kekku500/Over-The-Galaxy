package input;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
	
	public InputConfig(){}
	
	public void changeInput(){
		Properties prop = new Properties();
		InputStream input = null;
		try{
			input = new FileInputStream("res/config/keyconfig.properties");
			
			prop.load(input);
			
			playerAccelerate = Integer.parseInt(prop.getProperty("playerAccelerate"));
			playerMoveRight = Integer.parseInt(prop.getProperty("playerRotateRight"));
			playerMoveLeft = Integer.parseInt(prop.getProperty("playerRotateLeft"));
			playerRollRight = Integer.parseInt(prop.getProperty("playerRollRight"));
			playerRollLeft = Integer.parseInt(prop.getProperty("playerRollLeft"));
		//	playerRollBack = Integer.parseInt(prop.getProperty("playerRollBack"));
		//	playerRollForward = Integer.parseInt(prop.getProperty("playerRollForward"));
			
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
