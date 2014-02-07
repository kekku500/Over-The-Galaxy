package utils;

import javax.vecmath.Vector3f;

public class BoundingAxis {
	
	private Vector3f min;
	private Vector3f max;
	
	public BoundingAxis(Vector3f...vectors){
		Vector3f[] minmax = Utils.getMinMaxVectors(vectors);
		min = minmax[0];
		max = minmax[1];
	}
	
	private float pitch, yaw, roll;
	public void setRelation(float pitch, float yaw, float roll){
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	
	public boolean destroyed(float p, float y,float r){
		if(p != pitch || y != yaw || r != roll)
			return true;
		return false;
	}
	
	public Vector3f getMax(){
		return max;
	}
	
	public Vector3f getMin(){
		return min;
	}


}
