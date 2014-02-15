package game.world.entities;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

public class Line extends AbstractEntity{
	
	private int lineWidth = 1;
	private float length;
	
	@Override
	public Vector3f getPosToMid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity copy() {
		Line e = new Line();
		length = e.length();
		lineWidth = e.getLineWidth();
		return copy2(e);
	}

	@Override
	public void firstUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void lastUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRender() {
		GL11.glLineWidth(lineWidth);
		
	}

	@Override
	public void endRender() {
		GL11.glLineWidth(1);
	}



	@Override
	public void calcBoundingSphere() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calcBoundingAxis() {
		// TODO Auto-generated method stub
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
