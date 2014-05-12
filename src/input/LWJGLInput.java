package input;

import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import state.RenderState;
import state.StateVariable;
import utils.HashSet;
import utils.LinkedList;
import utils.R;
import world.graphics.Graphics3D;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.input.Input;

public class LWJGLInput implements Input{
	
	private static Set<InputReciever> globalInputs = new HashSet<InputReciever>();
	private Set<InputReciever> inputs = new HashSet<InputReciever>();
	
	private LinkedList<KbEvent> keyboardEventsForRendering = new LinkedList<KbEvent>();
	private LinkedList<KbEvent> keyboardEventsAvailableInRender = new LinkedList<KbEvent>();
	
	private LinkedList<MsEvent> mouseEventsForRendering = new LinkedList<MsEvent>();
	private LinkedList<MsEvent> mouseEventsAvailableInRender = new LinkedList<MsEvent>();
	
	private int dxEvent; 
	private int dyEvent; 
	private int dwheelEvent; 
	
	private boolean wasActive;
	
	public void updateInputState(){
		if(Keyboard.isCreated()) {
            while(Keyboard.next()) {
            	KbEvent event = new KbEvent(Keyboard.getEventKey(),
                        Keyboard.getEventCharacter(),
                        Keyboard.getEventKeyState());
            	checkKeyboardInput(event);
            	keyboardEventsForRendering.offer(new KbEvent(Keyboard.getEventKey(),
                        Keyboard.getEventCharacter(),
                        Keyboard.getEventKeyState()));
            }
        }
		
        if(Mouse.isCreated()) {
            while(Mouse.next()) {
            	MsEvent event = new MsEvent(Mouse.getEventX(), Mouse.getEventY(),
            			Mouse.getEventButton(), Mouse.getEventButtonState(),
            			Mouse.getEventDX(), Mouse.getEventDY(), Mouse.getEventDWheel());
            	checkMouseInput(event);
            	mouseEventsForRendering.offer(new MsEvent(Mouse.getEventX(), Mouse.getEventY(),
            			Mouse.getEventButton(), Mouse.getEventButtonState(),
            			Mouse.getEventDX(), Mouse.getEventDY(), Mouse.getEventDWheel()));
            }
        }
        
        int mx = Mouse.getDX();
        dxEvent = mx;

        int my = Mouse.getDY();
        dyEvent = my;

        int dwheel = Mouse.getDWheel() / 120;
        dwheelEvent = dwheel;


	}
	
	public void renderBegin(){
		while(!keyboardEventsForRendering.isEmpty()){
			keyboardEventsAvailableInRender.offer(keyboardEventsForRendering.poll());
			//System.out.println("kb loop");
		}
		while(!mouseEventsForRendering.isEmpty()){
			mouseEventsAvailableInRender.offer(mouseEventsForRendering.poll());
			//System.out.println("ms loop");
		}

	}
	
	public void renderEnd(){
		keyboardEventsAvailableInRender.clear();
		mouseEventsAvailableInRender.clear();
	}

	/**
	 * Input updating for gui.
	 */
	@Override
	public boolean pollInput(GUI gui) {
		boolean active = Display.isActive();
		if(wasActive && !active){
			wasActive = false;
			return false;
		}
		wasActive = active;
		
		for(KbEvent e: keyboardEventsAvailableInRender){
            gui.handleKey(e.key,e.character,e.state);
		}
		
		if(!Mouse.isGrabbed())
			for(MsEvent e: mouseEventsAvailableInRender){
				gui.handleMouse(e.x, gui.getHeight() - e.y - 1, e.button, e.state);
				
				if(e.dwheel != 0)
					gui.handleMouseWheel(e.dwheel);
			}
		
		return true;
	}
	
	//Inputs
	public void addInput(InputReciever i){
		inputs.add(i);
	}
	
	public void removeInput(InputReciever i){
		inputs.remove(i);
	}
	
	/**
	 * Add only inputs which are independent from world states.
	 * @param i
	 */
	public static void addGlobalInput(InputReciever i){
		globalInputs.add(i);
	}
	
	public static void removeGlobalInput(InputReciever i){
		globalInputs.remove(i);
	}
	
	public void checkMouseInput(MsEvent event){
			if(event.state){
				int m = event.button;
				for(InputReciever e: globalInputs)
					e.checkMouseInput(m);
				for(InputReciever e: inputs){
					e.checkMouseInput(m);
				}
				Graphics3D.checkMouseInput(m);
			}
	}
	
	public void checkKeyboardInput(KbEvent event){
			if(event.state){
				int k = event.key;
				for(InputReciever e: globalInputs)
					e.checkKeyboardInput(k);
				for(InputReciever e: inputs){
					e.checkKeyboardInput(k);
				}
				Graphics3D.checkKeyboardInput(k);
			}
	}
	
	public int getMouseDX(){
		return dxEvent;
	}
	
	public int getMouseDY(){
		return dyEvent;
	}
	
	public int getMouseDWheel(int state){
		return dwheelEvent;
	}

}
