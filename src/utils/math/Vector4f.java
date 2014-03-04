package utils.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Vector4f extends javax.vecmath.Vector4f{

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
	
	public Vector4f mul(float m){
		scale(m);
		return this;
	}
	
	public void setPositive(){
		if(x < 0)
			x = 0;
		if(y < 0)
			y = 0;
		if(z < 0)
			z = 0;
		if(w < 0)
			w = 0;
	}
	
	public Vector4f getNegate(){
		negate();
		return this;
	}
	
	public Vector4f copy(){
		return (Vector4f)clone();
	}
	
	public boolean isZero(){
		if(x == 0 && y == 0 && z == 0 && w == 0)
			return true;
		return false;
	}
	
	
	public FloatBuffer asFlippedFloatBuffer(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		float[] array = {x, y, z, w};
		fb.put(array);
		fb.flip();
		return fb;
	}

}
