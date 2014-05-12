package world.culling;

import state.Copyable;
import utils.math.Vector3f;

public class BoundingSphere implements Copyable<BoundingSphere>{
	
	public Vector3f pos;
	public float radius;
	
	public BoundingSphere(){
		pos = new Vector3f();
	}
	
	public BoundingSphere(Vector3f pos, float r){
		this.pos = pos;
		radius = r;
	}
	
	public String toString(){
		return pos + " r "+ radius;
	}
	
	@Override
	public BoundingSphere copy(){
		return new BoundingSphere(pos.copy(), radius);
	}

}
