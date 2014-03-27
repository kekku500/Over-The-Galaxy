package utils.math;

import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;

import com.bulletphysics.linearmath.QuaternionUtil;

import utils.Utils;

public class Matrix4f extends javax.vecmath.Matrix4f{

	private static final long serialVersionUID = 1L;
	
	public static final Matrix4f biasMatrix = new Matrix4f(new float[]{
															0.5f, 0.0f, 0.0f, 0.0f, 
															0.0f, 0.5f, 0.0f, 0.0f,
															0.0f, 0.0f, 0.5f, 0.0f,
															0.5f, 0.5f, 0.5f, 1.0f});
	
	public static final Matrix4f biasMatrixInverse = new Matrix4f(new float[]{
															2.0f, 0.0f, 0.0f, 0.0f,
															0.0f, 2.0f, 0.0f, 0.0f, 
															0.0f, 0.0f, 2.0f, 0.0f, 
															-1.0f, -1.0f, -1.0f, 1.0f});

	public Matrix4f(Quat4f q, Vector3f v, float f){
		super(q, v, f);
	}
	
	public Matrix4f(FloatBuffer fb){
		set(fb);
	}
	
	public Matrix4f(float[] vals){
		super(vals);
	}
	
	public Matrix4f(){
		super();
	}
	
	public Matrix4f mulReverse(Matrix4f m2){
		super.mul(m2, this);
		return this;
	}
	
	public void scaleRelative(float s){
		scaleRelative(s, s, s);
	}
	
	public void translateRelative(float x, float y, float z){
		Matrix4f transMat = Matrix4f.translationMatrix(new Vector3f(x, y, z));
		mulReverse(transMat);
	}
	
	
	public void scaleRelative(float x, float y, float z){
		Matrix4f scaleMatrix = Matrix4f.scaleMatrix(new Vector3f(x, y, z));
		mulReverse(scaleMatrix);
	}
	
	public void rotateRelative(Quat4f q){
		Matrix4f rotatedMatrix = new Matrix4f();
		rotatedMatrix.setIdentity();
		q.normalize();
		rotatedMatrix.setRotation(q);
		mulReverse(rotatedMatrix);
	}
	
	public void rotateRelative(float radians, Vector3f around){
		Quat4f q = new Quat4f();
		Matrix4f rotationMatrix = new Matrix4f();
		QuaternionUtil.setRotation(q, around, radians);
		rotationMatrix.set(q);
		mulReverse(rotationMatrix);
	}
	
	public Matrix4f mul(Matrix4f m2){
		super.mul(this, m2);
		return this;
	}
	
	public void translate(float x, float y, float z){
		Matrix4f transMat = Matrix4f.translationMatrix(new Vector3f(x, y, z));
		mul(transMat);
	}
	
	
	public void scale(float x, float y, float z){
		Matrix4f scaleMatrix = Matrix4f.scaleMatrix(new Vector3f(x, y, z));
		mul(scaleMatrix);
	}
	
	public void rotate(Quat4f q){
		Matrix4f rotatedMatrix = new Matrix4f();
		rotatedMatrix.setIdentity();
		q.normalize();
		rotatedMatrix.setRotation(q);
		mul(rotatedMatrix);
	}
	
	public void rotate(float radians, Vector3f around){
		Quat4f q = new Quat4f();
		Matrix4f rotationMatrix = new Matrix4f();
		QuaternionUtil.setRotation(q, around, radians);
		rotationMatrix.set(q);
		mul(rotationMatrix);
	}
	
	public void rotateLwjgl(float radian, Vector3f axis){
		org.lwjgl.util.vector.Matrix4f m = new org.lwjgl.util.vector.Matrix4f();
		m.rotate(radian, axis.lwjglVector3f());
		set(m);
	}
	
	public Vector4f mul(Vector4f u){
		Vector4f v = new Vector4f();
		
		v.x = get(0) * u.x + get(4) * u.y + get(8) * u.z + get(12) * u.w;
		v.y = get(1) * u.x + get(5) * u.y + get(9) * u.z + get(13) * u.w;
		v.z = get(2) * u.x + get(6) * u.y + get(10) * u.z + get(14) * u.w;
		v.w = get(3) * u.x + get(7) * u.y + get(11) * u.z + get(15) * u.w;
		
		return v;
	}
	
	public Matrix4f transposeGet(){
		super.transpose();
		return this;
	}
	
	public Matrix4f invertGet(){
		invert();
		return this;
	}
	
	public Matrix4f setIdentityGet(){
		super.setIdentity();
		return this;
	}
	
	public void set(int i, float val){
		setElement(i/4, i%4, val);
	}
	
	public Matrix4f set(FloatBuffer fb){
		float[] a = new float[16];
		fb.get(a);
		fb.flip();
		m00=a[0]; m01=a[1]; m02=a[2]; m03=a[3];
		m10=a[4]; m11=a[5]; m12=a[6]; m13=a[7];
		m20=a[8]; m21=a[9]; m22=a[10]; m23=a[11];
		m30=a[12]; m31=a[13]; m32=a[14]; m33=a[15];
		return this;
	}
	
	public float get(int i){
		return getElement(i/4, i%4);
	}
	

	
	public static Matrix4f viewMatrix(Vector3f eye, Vector3f center, Vector3f up, Vector3f pos){
		Matrix4f m = new Matrix4f();
		m.setRow(0, eye.x, center.x, up.x, 0.0f);
		m.setRow(1, eye.y, center.y, up.y, 0.0f);
		m.setRow(2, eye.z, center.z, up.z, 0.0f);
		m.setRow(3, -eye.dot(pos), -center.dot(pos), -up.dot(pos), 1.0f);
		return m;
	}

	public static Matrix4f translationMatrix(Vector3f v){
		Matrix4f m = new Matrix4f();
		m.setRow(0, 1.0f, 0.0f, 0.0f, 0.0f);
		m.setRow(1, 0.0f, 1.0f, 0.0f, 0.0f);
		m.setRow(2, 0.0f, 0.0f, 1.0f, 0.0f);
		m.setRow(3, v.x, v.y, v.z, 1.0f);
		return m;
	}
	
	public static Matrix4f scaleMatrix(Vector3f v){
		Matrix4f m = new Matrix4f();
		m.setRow(0, v.x, 0.0f, 0.0f, 0.0f);
		m.setRow(1, 0.0f, v.y, 0.0f, 0.0f);
		m.setRow(2, 0.0f, 0.0f, v.z, 0.0f);
		m.setRow(3, 0.0f, 0.0f, 0.0f, 1.0f);
		return m;
	}
	
	public Matrix4f copy(){
		return (Matrix4f)clone();
	}
	
	public FloatBuffer asFlippedFloatBuffer(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		float[] array = {m00, m01, m02, m03,
						 m10, m11, m12, m13,
						 m20, m21, m22, m23,
						 m30, m31, m32, m33};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	/**
	 * @return Converts this matrix to lwjgl matrix
	 */
	public org.lwjgl.util.vector.Matrix4f lwjglMatrix4f(){
		org.lwjgl.util.vector.Matrix4f m = new org.lwjgl.util.vector.Matrix4f();
		m.m00 = m00;		m.m10 = m10;		m.m20 = m20;		m.m30 = m30;
		m.m01 = m01;		m.m11 = m11;		m.m21 = m21;		m.m31 = m31;
		m.m02 = m02;		m.m12 = m12;		m.m22 = m22;		m.m32 = m32;
		m.m03 = m03;		m.m13 = m13;		m.m23 = m23;		m.m33 = m33;
		return m;
	}
	
	/**
	 * @return Converts this matrix to pure vecmath matrix
	 */
	public javax.vecmath.Matrix4f vecmathMatrix4f(){
		javax.vecmath.Matrix4f m = new javax.vecmath.Matrix4f();
		m.m00 = m00;		m.m10 = m10;		m.m20 = m20;		m.m30 = m30;
		m.m01 = m01;		m.m11 = m11;		m.m21 = m21;		m.m31 = m31;
		m.m02 = m02;		m.m12 = m12;		m.m22 = m22;		m.m32 = m32;
		m.m03 = m03;		m.m13 = m13;		m.m23 = m23;		m.m33 = m33;
		return m;
	}
	
	private Matrix4f set(org.lwjgl.util.vector.Matrix4f m){
		m00 = m.m00;		m10 = m.m10;		m20 = m.m20;		m30 = m.m30;
		m01 = m.m01;		m11 = m.m11;		m21 = m.m21;		m31 = m.m31;
		m02 = m.m02;		m12 = m.m12;		m22 = m.m22;		m32 = m.m32;
		m03 = m.m03;		m13 = m.m13;		m23 = m.m23;		m33 = m.m33;
		return this;
	}

}
