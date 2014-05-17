package graphics.gui.mapeditor;

import math.Vector3f;
import math.Vector4f;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.ColorSelector;
import de.matthiasmann.twl.model.ColorSpaceHSL;

public class ColorPickerWindow extends CloseableFrame{
	
	public ColorSelector cs;
	
	private Vector4f lightColor;
	
	public ColorPickerWindow(){
		super();
		setTheme("resizableframe-title");
		cs = new ColorSelector(new ColorSpaceHSL());
		add(cs);
		
		cs.addCallback(new Runnable(){

			@Override
			public void run() {
				if(lightColor != null){
					Color color = cs.getColor();
					lightColor.set(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
				}
			}});
	}
	
	public void setVector(Vector4f lightColor){
		this.lightColor = lightColor;
	}

}
