package input;

import graphics.Graphics3D;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import utils.HashSet;
import utils.LinkedList;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.input.Input;

/**
 * Class to handle all of OpenGL input; such as when to call input checking method.
 */
public class LWJGLInput implements Input{
	
	private Set<InputHandler> inputs = new HashSet<InputHandler>();
	
	private BlockingQueue<KbEvent> keyboardEventsForRendering = new LinkedBlockingQueue<KbEvent>();
	private LinkedList<KbEvent> keyboardEventsAvailableInRender = new LinkedList<KbEvent>();
	
	private BlockingQueue<MsEvent> mouseEventsForRendering = new LinkedBlockingQueue<MsEvent>();
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
            	for(InputHandler i: inputs)
            		i.handleKey(event);
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
            	for(InputHandler i: inputs)
            		i.handleMouse(event);
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
		KbEvent kbEvent = null;
		while((kbEvent = keyboardEventsForRendering.poll()) != null){
			keyboardEventsAvailableInRender.offer(kbEvent);
		}
		MsEvent msEvent = null;
		while((msEvent = mouseEventsForRendering.poll()) != null){
			mouseEventsAvailableInRender.offer(msEvent);
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
	public void addInput(InputHandler i){
		inputs.add(i);
	}
	
	public void removeInput(InputHandler i){
		inputs.remove(i);
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
