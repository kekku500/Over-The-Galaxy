package math;

import java.nio.FloatBuffer;

import main.state.Copyable;

import org.lwjgl.BufferUtils;

public class Vector4f extends javax.vecmath.Vector4f implements Copyable<Vector4f>{

	private static final long serialVersionUID = 1L;
	
	public Vector4f(){
		super();
	}
	
	public Vector4f(Vector3f v, float w){
		super(v.x, v.y, v.z, w);
	}
	
	public Vector4f(float x, float y, float z, float w){
		super(x, y, z, w);
	}
	
	public Vector4f add(Vector4f v2){
		super.add(v2);
		return this;
	}
	
	public Vector4f setr(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	
	public Vector4f mul(float m){
		scale(m);
		return this;
	}
	
	public Vector3f vec3(){
		return new Vector3f(x, y, z);
	}
	
	public Vector4f mul(Matrix4f m){
		return setr(
			m.m00*x + m.m01*y + m.m02*z + m.m03*w,
			m.m10*x + m.m11*y + m.m12*z + m.m13*w,
			m.m20*x + m.m21*y + m.m22*z + m.m23*w,
			m.m30*x + m.m31*y + m.m32*z + m.m33*w);
	}
	
	public Vector4f mulTra(Matrix4f m){
		return setr(
			m.m00*x + m.m10*y + m.m20*z + m.m30*w,
			m.m01*x + m.m11*y + m.m21*z + m.m31*w,
			m.m02*x + m.m12*y + m.m22*z + m.m32*w,
			m.m03*x + m.m13*y + m.m23*z + m.m33*w);
	}
	
	public Vector4f set(Vector4f v){
		return setr(v.x, v.y, v.z, v.w);
	}
	
	public Vector4f negater(){
		negate();
		return this;
	}
	
	public boolean isZero(){
		if(x == 0 && y == 0 && z == 0 && w == 0)
			return true;
		return false;
	}
	
	public FloatBuffer fb(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		float[] array = {x, y, z, w};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	@Override
	public Vector4f copy(){
		return new Vector4f(x, y, z, w);
	}

}
