package input;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import world.graphics.Graphics3D;

/**
 * Can be used to store input classes and check for input.
 * Can check input events only once per loop (Not recommended to use this class for input checking
 * because World state already has a InputListener implemented)
 * @author Kevin
 */

public class InputListener {
	
	//Inputs
	
	private static Set<Input> globalInputs = new HashSet<Input>();
	private Set<Input> inputs = new HashSet<Input>();
	
	private static boolean hasListenedKeyboard = false;
	private static boolean hasListenedMouse = false;
	
	public InputListener(){}
	
	
	public void addInput(Input i){
		inputs.add(i);
	}
	
	public void removeInput(Input i){
		inputs.remove(i);
	}
	
	/**
	 * Add only inputs which are independent from world states.
	 * @param i
	 */
	public static void addGlobalInput(Input i){
		globalInputs.add(i);
	}
	
	public static void removeGlobalInput(Input i){
		globalInputs.remove(i);
	}
	
	public static void reset(){
		hasListenedKeyboard = false;
		hasListenedMouse = false;
	}
	
	public void checkMouseInput() throws Exception{
		if(!hasListenedMouse)
			hasListenedMouse = true;
		else
			throw new Exception("Unable to receive input twice per loop!");
		while(Mouse.next()){
			if(Mouse.getEventButtonState()){
				int m = Mouse.getEventButton();
				for(Input e: globalInputs)
					e.checkMouseInput(m);
				for(Input e: inputs){
					e.checkMouseInput(m);
				}
			}
		}
	}
	
	public void checkKeyboardInput() throws Exception{
		if(!hasListenedKeyboard)
			hasListenedKeyboard = true;
		else
			throw new Exception("Unable to receive input twice per loop!");
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				int k = Keyboard.getEventKey();
				for(Input e: globalInputs)
					e.checkKeyboardInput(k);
				for(Input e: inputs){
					e.checkKeyboardInput(k);
				}		
				Graphics3D.checkKeyboardInput(k);
				Graphics3D.checkMouseInput(k);
			}
		}
	}

}
