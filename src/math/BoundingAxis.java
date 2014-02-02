package math;

public class BoundingAxis {
	
	private Vector3fc min;
	private Vector3fc max;
	
	public BoundingAxis(Vector3fc...vectors){
		Vector3fc[] minmax = Vector3fc.getMinMaxVectors(vectors);
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
	
	public Vector3fc getMax(){
		return max;
	}
	
	public Vector3fc getMin(){
		return min;
	}

}
