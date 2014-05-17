package graphics.culling;

import math.Vector3f;

public class BoundingCube extends BoundingAxis{
	
	private float size;
	
	public BoundingCube(Vector3f minPos, float size){
		min = minPos;
		max = min.copy().add(size);
		this.size = size;
		
	}
	
	public float getSize(){
		return size;
	}

}
