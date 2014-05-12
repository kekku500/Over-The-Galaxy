package utils.math;

import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;


import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;

import state.Copyable;
import state.Game;
import utils.Utils;

import com.bulletphysics.linearmath.QuaternionUtil;

//OpenGL uses column-major matrix ordering.

public class Matrix4f extends javax.vecmath.Matrix4f implements Copyable<Matrix4f>{
	


	private static final long serialVersionUID = 1L;
	
	public static final Matrix4f BIASMATRIX = new Matrix4f(new float[]{
															0.5f, 0.0f, 0.0f, 0.0f, 
															0.0f, 0.5f, 0.0f, 0.0f,
															0.0f, 0.0f, 0.5f, 0.0f,
															0.5f, 0.5f, 0.5f, 1.0f});
	
	public static final Matrix4f BIASMATRIXINV = new Matrix4f(new float[]{
															2.0f, 0.0f, 0.0f, 0.0f,
															0.0f, 2.0f, 0.0f, 0.0f, 
															0.0f, 0.0f, 2.0f, 0.0f, 
															-1.0f, -1.0f, -1.0f, 1.0f});

	public Matrix4f(){
		super();
	}
	
	public Matrix4f(float[] vals){
		super(vals);
	}
	
	public Matrix4f(FloatBuffer fb){
		set(fb);
	}
	
	public Matrix4f(Quat4f q, Vector3f v, float f){
		super(q, v, f);
	}
	
	public Matrix4f mul(Matrix4f m2){
		super.mul(this, m2);
		return this;
	}
	
	public static Matrix4f perspectiveMatrix(float fov, float aspect, float zNear, float zFar){
		Matrix4f m = new Matrix4f();
		float f = 1/(float)Math.tan(Utils.rads(fov)/2);
		float nmf = zNear-zFar;
		m.setRow(0, f/aspect, 0f, 0f, 0f);
		m.setRow(1, 0f, f, 0f, 0f);
		m.setRow(2, 0f, 0f, (zFar+zNear)/nmf, 2*zFar*zNear/nmf);
		m.setRow(3, 0f, 0f, -1f, 0f);
		m.trans();
		return m;
	}
	
	public static Matrix4f  toOpenGLViewMatrx(Matrix4f viewMatrix){
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		Vector3f viewRay = new Vector3f(ray[0], ray[1], ray[2]);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
		
		float[] pos = new float[4];
		viewMatrix.getColumn(3, pos);
		Vector3f posVector = new Vector3f(pos[0], pos[1], pos[2]);
		
		return viewMatrixDirectional(posVector, viewRay, upVector);
	}
	
	public static Matrix4f viewMatrix(Vector3f eye, Vector3f center, Vector3f up, Vector3f pos){
		Matrix4f m = new Matrix4f();
		m.setRow(0, eye.x, center.x, up.x, 0.0f);
		m.setRow(1, eye.y, center.y, up.y, 0.0f);
		m.setRow(2, eye.z, center.z, up.z, 0.0f);
		m.setRow(3, -eye.dot(pos), -center.dot(pos), -up.dot(pos), 1.0f);
		return m;
	}
	
	public static Matrix4f viewMatrixPositional(Vector3f cameraPosition, Vector3f lookAtPosition, Vector3f upOrientation){
		return viewMatrixDirectional(cameraPosition, lookAtPosition.copy().add(cameraPosition.copy().negater()), upOrientation);
	}
	
	public static Matrix4f viewMatrixDirectional(Vector3f cameraPosition, Vector3f lookAtDirection, Vector3f upOrientation){
		Matrix4f m = new Matrix4f();
		
		Vector3f f = lookAtDirection.copy().nor();
		
		Vector3f upii = upOrientation.copy().nor();
		
		Vector3f s = f.copy().crs(upii);
		
		Vector3f sii = s.copy().nor();
		
		Vector3f u = sii.copy().crs(f);
		
		m.setRow(0, s.x, s.y, s.z, 0);
		m.setRow(1, u.x, u.y, u.z, 0);
		m.setRow(2, -f.x, -f.y, -f.z, 0);
		m.setRow(3, 0, 0, 0, 1.0f);
		m.trans();
		
		Matrix4f transMat = Matrix4f.translationMatrix(cameraPosition.copy().negater());
		m.mulLeft(transMat);
		
		return m;
	}
	
	public static Matrix4f translationMatrix(Vector3f v){
		return translationMatrix(v.x, v.y, v.z);
	}
	
	public static Matrix4f translationMatrix(float x, float y, float z){
		Matrix4f m = new Matrix4f();
		m.setRow(0, 1.0f, 0.0f, 0.0f, 0.0f);
		m.setRow(1, 0.0f, 1.0f, 0.0f, 0.0f);
		m.setRow(2, 0.0f, 0.0f, 1.0f, 0.0f);
		m.setRow(3, x, y, z, 1.0f);

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

	public Matrix4f mulLeft(Matrix4f m2){
		super.mul(m2, this);
		return this;
	}
	
	public void scaleLeft(float s){
		scaleleft(s, s, s);
	}
	
	public void translateLeft(float x, float y, float z){
		Matrix4f transMat = Matrix4f.translationMatrix(new Vector3f(x, y, z));
		mulLeft(transMat);
	}
	
	
	public void scaleleft(float x, float y, float z){
		Matrix4f scaleMatrix = Matrix4f.scaleMatrix(new Vector3f(x, y, z));
		mulLeft(scaleMatrix);
	}
	
	public void rotateLeft(Quat4f q){
		Matrix4f rotatedMatrix = new Matrix4f();
		rotatedMatrix.setIdentity();
		q.normalize();
		rotatedMatrix.setRotation(q);
		mulLeft(rotatedMatrix);
	}
	
	public void rotateLeft(float radians, Vector3f around){
		Quat4f q = new Quat4f();
		Matrix4f rotationMatrix = new Matrix4f();
		QuaternionUtil.setRotation(q, around, radians);
		rotationMatrix.set(q);
		mulLeft(rotationMatrix);
	}
	
	public Vector4f mul(Vector4f u){
		Vector4f v = new Vector4f();
		
		v.x = get(0) * u.x + get(4) * u.y + get(8) * u.z + get(12) * u.w;
		v.y = get(1) * u.x + get(5) * u.y + get(9) * u.z + get(13) * u.w;
		v.z = get(2) * u.x + get(6) * u.y + get(10) * u.z + get(14) * u.w;
		v.w = get(3) * u.x + get(7) * u.y + get(11) * u.z + get(15) * u.w;
		
		return v;
	}
	

	public void set(int index, float val){
		setElement(index/4, index%4, val);
	}
	
	public Matrix4f set(FloatBuffer fb){
		float[] a = new float[16];
		fb.get(a);
		fb.flip();
		super.set(a);

		return this;
	}
	
	public float get(int i){
		return getElement(i/4, i%4);
	}
	
	public Matrix4f setAsRotation(float angle, Vector3f rotationNormal){
		setIdentity();
		Quat4f rotation = new Quat4f();
		
		QuaternionUtil.setRotation(rotation, rotationNormal, Utils.rads(angle));
		
		setRotation(rotation);

		return this;
	}
	
	public FloatBuffer fb(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		float[] array = {m00, m01, m02, m03,
						 m10, m11, m12, m13,
						 m20, m21, m22, m23,
						 m30, m31, m32, m33};
		fb.put(array);
		fb.flip();
		return fb;
	}
	
	public float[] get(){
		float[] array = {m00, m01, m02, m03,
						 m10, m11, m12, m13,
						 m20, m21, m22, m23,
						 m30, m31, m32, m33};
		return array;
	}
	
	public Matrix4f trans(){
		super.transpose();
		return this;
	}
	
	public Matrix4f inv(){
		invert();
		return this;
	}
	
	public Matrix4f idy(){
		super.setIdentity();
		return this;
	}
	
	@Override
	public Matrix4f copy(){
		return new Matrix4f(get());
	}

}
