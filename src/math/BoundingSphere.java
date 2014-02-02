package math;

import org.lwjgl.util.vector.Vector3f;

public class BoundingSphere {
	
	public Vector3f pos;
	public float radius;
	
	public BoundingSphere(Vector3f pos, float r){
		this.pos = pos;
		radius = r;
	}
	
	public String toString(){
		return pos + " r "+ radius;
	}

}
