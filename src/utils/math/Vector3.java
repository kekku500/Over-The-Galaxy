package utils.math;

import javax.vecmath.Vector3f;


public class Vector3 extends Vector3f{
	
	public Vector3(){
		super();
	}
	
	public Vector3(float x, float y, float z){
		super(x, y, z);
	}

	public boolean equals(Vector3f v2){
		if(x == v2.x && y == v2.y && z == v2.z)
			return true;
		return false;
	}
	
	public Vector3 getAdd(Vector3f v2){
		return new Vector3(x+v2.x,y+v2.y,z+v2.z);
	}
	
	public Vector3 getNegate(){
		return new Vector3(-x,-y,-z);
	}
	
	public Vector3 getMultiply(float m){
		return new Vector3(x*m, y*m, z*m);
	}
	
	public Vector3 copy(){
		return new Vector3(x, y, z);
	}
	
	public boolean isZero(){
		if(x == 0 && y == 0 && z == 0)
			return true;
		return false;
	}

}
