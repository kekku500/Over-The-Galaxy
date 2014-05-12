package world.culling;

import state.Copyable;
import utils.math.Vector3f;

public class BoundingAxis implements Copyable<BoundingAxis>{
	
	protected Vector3f min;
	protected Vector3f max;
	
	public BoundingAxis(){
		min = new Vector3f();
		max = new Vector3f();
	}
	
	public BoundingAxis(Vector3f min, Vector3f max){
		this.min = min;
		this.max = max;
	}

	public Vector3f getMax(){
		return max;
	}
	
	public Vector3f getMin(){
		return min;
	}
	
	public boolean intersects(BoundingAxis b){
		  return(max.x > b.getMin().x &&
				    min.x < b.getMax().x &&
				    max.y > b.getMin().y &&
				    min.y < b.getMax().y &&
				    max.z > b.getMin().z &&
				    min.z < b.getMax().z);
	}
	
	public void set(Vector3f min, Vector3f max){
		this.min = min;
		this.max = max;
	}
	
	@Override
	public String toString(){
		return "Min " + getMin() + " Max " + getMax();
	}
	
	public BoundingAxis copy(){
		return new BoundingAxis(min.copy(), max.copy());
	}


}
