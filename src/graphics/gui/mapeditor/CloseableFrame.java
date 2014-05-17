package graphics.gui.mapeditor;

import de.matthiasmann.twl.ResizableFrame;

public class CloseableFrame extends ResizableFrame{
	
	public CloseableFrame(boolean noclose){
		super();
	}
	
	public CloseableFrame(){
		super();
		addCloseCallback();
	}
	
    public void show() {
        setVisible(true);
        requestKeyboardFocus();
    }

    public void hide() {
        setVisible(false);
    }
    
    public void addCloseCallback() {
        addCloseCallback(new Runnable() {
            public void run() {
                hide();
            }
        });
    }

}
