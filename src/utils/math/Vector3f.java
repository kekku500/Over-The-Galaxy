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
	
	public Vector3f mul(Vector3f v){
		x *= v.x;
		y *= v.y;
		z *= v.z;
		return this;
	}
	
	public Vector3f mul(Matrix3f m){
		Vector3f r = new Vector3f();
		r.x = m.m00*x + m.m01*y + m.m02*z;
		r.y = m.m10*x + m.m11*y + m.m12*z;
		r.z = m.m20*x + m.m21*y + m.m22*z;
		
		set(r);
		
		return this;
	}

	public Vector3f mul(Matrix4f m){
		Vector3f r = new Vector3f();
		
		r.x = m.m00*x + m.m10*y + m.m20*z + m.m30*1.0f;
		r.y = m.m01*x + m.m11*y + m.m21*z + m.m31*1.0f;
		r.z = m.m02*x + m.m12*y + m.m22*z + m.m32*1.0f;
		
		set(r);
		
		return this;
	}
	
    public Vector3f rotateGet(float angle, Vector3f l){
    	Vector4f vecPosMod = new Vector4f(x, y, z, 1.0f);
    	
    	Matrix4f transMat = new Matrix4f();
    	transMat.rotateLwjgl((float)Math.toRadians(angle), l);
    	transMat.transform(vecPosMod);
    	return new Vector3f(vecPosMod.x, vecPosMod.y, vecPosMod.z);
    }
	
	public Vector3f addGet(float x, float y, float z){
		Vector3f a = copy();
		a.x += x;
		a.y += y;
		a.z += z;
		return a;
	}
	
	public Vector3f scaleGet(float m){
		super.scale(m);
		return this;
	}
	
	public Vector3f add(float v){
		x +=v;
		y +=v;
		z +=v;
		return this;
	}
	
	public Vector3f reverse(){
		x = 1/x;
		y = 1/y;
		z = 1/z;
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
