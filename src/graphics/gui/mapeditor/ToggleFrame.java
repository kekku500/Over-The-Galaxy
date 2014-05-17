package graphics.gui.mapeditor;

import de.matthiasmann.twl.ResizableFrame;

public class ToggleFrame implements Runnable{
	
	CloseableFrame frame;
	
	public ToggleFrame(CloseableFrame frame){
		this.frame = frame;
	}

	@Override
	public void run() {
		if(frame.isVisible()){
			frame.hide();
		}else{
			frame.show();
		}
		
	}
	


}
