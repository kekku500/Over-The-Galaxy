package game.world.entities;


import org.lwjgl.opengl.GL11;

import utils.math.Vector3f;

public class Line extends AbstractEntity{
	
	private int lineWidth = 1;
	private float length;

	@Override
	public Entity getLinked() {
		Line e = new Line();
		length = e.length();
		lineWidth = e.getLineWidth();
		return e.linkTo(this);
	}
	
	public int getLineWidth(){
		return lineWidth;
	}
	
	public void setLineWidth(int i){
		lineWidth = i;
	}
	
	public void setLength(float l){
		length = l;
	}
	
	public float length(){
		return length;
	}

}
