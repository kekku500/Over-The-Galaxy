package utils.math;

import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;

import com.bulletphysics.linearmath.QuaternionUtil;

import state.Copyable;
import utils.Utils;

public class Vector3f extends javax.vecmath.Vector3f implements Copyable<Vector3f>{

	private static final long serialVersionUID = 1L;
	
	public final static Vector3f X = new Vector3f(1, 0, 0);
	public final static Vector3f Y = new Vector3f(0, 1, 0);
	public final static Vector3f Z = new Vector3f(0, 0, 1);
	
	public Vector3f(javax.vecmath.Vector3f v){
		super(v.x, v.y, v.z);
	}
	
	public Vector3f(){
		super();
	}
	
	public Vector3f(float x, float y, float z){
		super(x, y, z);
	}
	
	public Vector3f(final float[] vals){
		set(vals[0], vals[1], vals[2]);
	}
	
	public Vector3f(final Vector3f v){
		super(v);
	}
	
	public Vector3f(Vector4f v){
		super(v.x, v.y, v.z);
	}
	
	/**
	 * setr stands for Set Return, meaning this vector is returned for chaining.
	 * Same as void javax.vecmath.Vector3.set(float x, float y, float z)
	 */
	public Vector3f setr(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public Vector3f set(final Vector3f v){
		return setr(v.x, v.y, v.z);
	}
	
	
	/**
	 * addr stands for Add Return, meaning this vector is returned for chaining.
	 * Same as void javax.vecmath.Vector3.add(float x, float y, float z)
	 */
	public Vector3f addr(float x, float y, float z){
		return setr(this.x + x, this.y + y, this.z + z);
	}
	
	public Vector3f add(final Vector3f v){
		return addr(v.x, v.y, v.z);
	}
	
	public Vector3f add(float v){
		return addr(v, v, v);
	}
	
	/**
	 * addr stands for Add Return, meaning this vector is returned for chaining.
	 * Same as void javax.vecmath.Vector3.add(float x, float y, float z)
	 */
	public Vector3f subr(float x, float y, float z){
		return setr(this.x - x, this.y - y, this.z - z);
	}
	
	public Vector3f sub(final Vector3f v){
		return subr(v.x, v.y, v.z);
	}
	
	public Vector3f sub(float v){
		return subr(v, v, v);
	}

	public Vector3f scl(float scalar){
		return setr(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	
	public Vector3f scl(final Vector3f v){
		return setr(x * v.x, y * v.y, z * v.z);
	}
	
	public float len(){
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public float len2(){
		return x * x + y * y + z * z;
	}
	
	/**
	 * Whether vectors are equal.
	 */
	public boolean equal(final Vector3f v){
		return x == v.x && y == v.y && z == v.z;
	}
	
	public float dst(final Vector3f vector){
		final float dx = vector.x - x;
		final float dy = vector.y - y;
		final float dz = vector.z - z;
		return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public float dst2(final Vector3f vector){
		final float dx = vector.x - x;
		final float dy = vector.y - y;
		final float dz = vector.z - z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	public Vector3f nor(){
		final float len2 = len2();
		if(len2 == 0f || len2 == 1f) 
			return this;
		return scl(1f / (float)Math.sqrt(len2));
	}
	
	public float dot(final Vector3f v){
		return x * v.x + y * v.y + z * v.z;
	}
	
	public Vector3f crs(final Vector3f v){
		return setr(y * v.z - z * v.y,
				z * v.x - x * v.z,
				x * v.y - y * v.x);
	}
	

	/**
	 * It is assumed that 4th component of the vector (w) is 1.0f;
	 */
	public Vector3f mul(final Matrix4f m){
		return setr(
			m.m00*x + m.m01*y + m.m02*z + m.m03*1.0f,
			m.m10*x + m.m11*y + m.m12*z + m.m13*1.0f,
			m.m20*x + m.m21*y + m.m22*z + m.m23*1.0f);
	}
	
	/**
	 * It is assumed that 4th component of the vector (w) is 1.0f;
	 */
	public Vector3f mulTra(final Matrix4f m){
		return setr(
			m.m00*x + m.m10*y + m.m20*z + m.m30*1.0f,
			m.m01*x + m.m11*y + m.m21*z + m.m31*1.0f,
			m.m02*x + m.m12*y + m.m22*z + m.m32*1.0f);
	}
	
	public Vector3f mul(final Matrix3f m){
		return setr(
			m.m00*x + m.m01*y + m.m02*z,
			m.m10*x + m.m11*y + m.m12*z,
			m.m20*x + m.m21*y + m.m22*z);
	}
	
	public Vector3f mulTra(final Matrix3f m){
		return setr(
			m.m00*x + m.m10*y + m.m20*z,
			m.m01*x + m.m11*y + m.m21*z,
			m.m02*x + m.m12*y + m.m22*z);
	}

	public Vector3f prj(final Matrix4f m){
		final float proj_div = 1f / (x * m.m30 + y * m.m31 + z * m.m32 + m.m33);
		
		return setr((x * m.m00 + y * m.m01 + z * m.m02 + m.m03) * proj_div, 
				 (x * m.m10 + y * m.m11 + z * m.m12 + m.m13) * proj_div, 
				 (x * m.m20 + y * m.m21 + z * m.m22 + m.m23) * proj_div);
	}
	
	/**
	 * 4th component of the vector is 0f,
	 * meaning only rotation is applied.
	 */
	public Vector3f rot(final Matrix4f m){
		return setr(
			m.m00*x + m.m01*y + m.m02*z,
			m.m10*x + m.m11*y + m.m12*z,
			m.m20*x + m.m21*y + m.m22*z);
	}
	
	/**
	 * Multiplied by the transpose of matrix m and 4th component of the vector is 0f,
	 * meaning only rotation is applied.
	 */
	public Vector3f unrot(final Matrix4f m){
		return setr(
			m.m00*x + m.m10*y + m.m20*z,
			m.m01*x + m.m11*y + m.m21*z,
			m.m02*x + m.m12*y + m.m22*z);
	}
	
	public boolean isZero(){
		if(x == 0 && y == 0 && z == 0)
			return true;
		return false;
	}
	
	/**
	 * Same as negate, but returns this vector.
	 * @return
	 */
	public Vector3f negater(){
		return setr(-x, -y, -z);
	}
	
    public Vector3f rotateGet(float angle, Vector3f aroundThis){
		Matrix4f rotationMatrix = new Matrix4f();
		rotationMatrix.setAsRotation(angle, aroundThis);
		
		mulTra(rotationMatrix);
		
		return this;
		
    }
	
	public Vector3f invert(){
		x = 1/x;
		y = 1/y;
		z = 1/z;
		return this;
	}

	public FloatBuffer fb(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(3);
		float[] array = {x, y, z};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	public FloatBuffer fb(float w){
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		float[] array = {x, y, z, w};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	public javax.vecmath.Vector3f vecmath(){
		return new javax.vecmath.Vector3f(x,y,z);
	}
	
	public Vector4f vec4(float w){
		return new Vector4f(this, w);
	}
	
	public Vector3f lerp(Vector3f cur, float alpha){
		x = cur.x * alpha + x * (1.0f - alpha);
		y = cur.y * alpha + y * (1.0f - alpha);
		z = cur.z * alpha + z * (1.0f - alpha);
		return this;
	}
	
	@Override
	public Vector3f copy(){
		return new Vector3f(x, y, z);
	}

}
