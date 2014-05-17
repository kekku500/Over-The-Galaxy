package math;

import java.nio.FloatBuffer;

import main.state.Copyable;

import org.lwjgl.BufferUtils;

public class Matrix3f extends javax.vecmath.Matrix3f implements Copyable<Matrix3f>{
	
	private static final long serialVersionUID = 1L;

	public Matrix3f(){
		super();
	}
	
	public Matrix3f(float[] m){
		super(m);
	}

	public Matrix3f(Matrix4f in) {
		super();
		m00 = in.m00;
		m01 = in.m01;
		m02 = in.m02;
		m10 = in.m10;
		m11 = in.m11;
		m12 = in.m12;
		m20 = in.m20;
		m21 = in.m21;
		m22 = in.m22;
	}

	
	public FloatBuffer fb(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(9);
		float[] array = {m00, m01, m02,
						 m10, m11, m12,
						 m20, m21, m22};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	public float[] get(){
		float[] array = {m00, m01, m02,
						 m10, m11, m12,
						 m20, m21, m22};
		return array;
	}
	
	public Matrix3f inv(){
		invert();
		return this;
	}
	
	public Matrix3f trans(){
		transpose();
		return this;
	}

	@Override
	public Matrix3f copy() {
		return new Matrix3f(get());
	}

}
