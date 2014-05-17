package input;

/**
 * Every entity which has implemented InputHandler interface will be check for inputs
 * using methods below.
 */

public interface InputHandler {

	public void handleKey(KbEvent event);
	
	public void handleMouse(MsEvent event);

}
