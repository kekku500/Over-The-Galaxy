package utils.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Vector3f extends javax.vecmath.Vector3f{

	private static final long serialVersionUID = 1L;
	
	public Vector3f(){
		super();
	}
	
	public Vector3f(float x, float y, float z){
		super(x, y, z);
	}
	
	public Vector3f add(Vector3f v2){
		super.add(v2);
		return this;
	}
	
	public Vector3f mul(float m){
		scale(m);
		return this;
	}
	
	public Vector3f getNegate(){
		negate();
		return this;
	}
	
	public Vector3f(javax.vecmath.Vector3f v){
		super(v.x, v.y, v.z);
	}
	
	public Vector3f copy(){
		return (Vector3f)clone();
	}
	
	public FloatBuffer asFlippedFloatBuffer(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(3);
		float[] array = {x, y, z};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	public FloatBuffer asFlippedFloatBuffer(float w){
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		float[] array = {x, y, z, w};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	public javax.vecmath.Vector3f vecmathVector3f(){
		return new javax.vecmath.Vector3f(x,y,z);
	}
	
	public org.lwjgl.util.vector.Vector3f lwjglVector3f(){
		return new org.lwjgl.util.vector.Vector3f(x, y, z);
	}
	
	public boolean isZero(){
		if(x == 0 && y == 0 && z == 0)
			return true;
		return false;
	}
	
	public static float dot(Vector3f u, Vector3f v){
		return u.x * v.x + u.y * v.y + u.z * v.z;
	}

}
