package input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputConfig {
	
	//Player
	public static int playerAccelerate = Keyboard.KEY_UP;
	public static int playerRotateRight = Keyboard.KEY_RIGHT;
	public static int playerRotateLeft = Keyboard.KEY_LEFT;
	
	//Graphics3D
	public final static int enableTexturing = Keyboard.KEY_F1;
	public final static int enableNormalMapping = Keyboard.KEY_F2;
	public final static int enableShadows = Keyboard.KEY_F3;
	public final static int enableShadowFiltering = Keyboard.KEY_F4;
	public final static int enableShadowOcclusion = Keyboard.KEY_F5;
	public final static int enableLightScattering = Keyboard.KEY_F6;
	public final static int showColorTexture = Keyboard.KEY_NUMPAD7;
	public final static int showNormalTexture = Keyboard.KEY_NUMPAD8;
	public final static int showDepthTexture = Keyboard.KEY_NUMPAD9;
	public final static int showMaterialAmbient = Keyboard.KEY_1;
	public final static int showMaterialDiffuse = Keyboard.KEY_2;
	public final static int showMaterialEmission = Keyboard.KEY_3;
	public final static int showMaterialShininess = Keyboard.KEY_4;
	public final static int showMaterialSpecular = Keyboard.KEY_5;
	public final static int showGodRaysTexture = Keyboard.KEY_NUMPAD5;
	public final static int showShadowOcclusionTexture = Keyboard.KEY_NUMPAD2;
	public final static int resetTexture = Keyboard.KEY_NUMPAD0;
	
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
	
	private static int mouseDx, mouseDy;
	private static int dwheel;
	
	public static int getRotationX(){
		return getMouseDX();
	}
	
	public static int getRotationY(){
		return getMouseDY();
	}
	
	public static int getRotationZ(){
		return getDWheel();
	}
	
	public static int getMouseDX(){
		return mouseDx;
	}
	
	public static int getMouseDY(){
		return mouseDy;
	}
	
	public static int getDWheel(){
		return dwheel;
	}
	
	public static void refresh(){
		mouseDx = Mouse.getDX();
		mouseDy = Mouse.getDY();
		dwheel = Mouse.getDWheel();
	}

}
