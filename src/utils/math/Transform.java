package utils.math;

import javax.vecmath.Quat4f;

import state.Copyable;

public class Transform extends com.bulletphysics.linearmath.Transform implements Copyable<Transform>{
	
	private float scaleLog = 1;
	private Vector3f scaleLogV = new Vector3f(1,1,1);
	
	public Transform(){
		super();
	}
	
	public Transform(Matrix4f m4){
		super(m4);	
	}
	
	public Matrix4f getOpenGLViewMatrix(){
		Matrix4f viewMatrix = new Matrix4f();
		getMatrix(viewMatrix);
		
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		Vector3f viewRay = new Vector3f(ray[0], ray[1], ray[2]);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
		
		float[] pos = new float[4];
		viewMatrix.getColumn(3, pos);
		Vector3f posVector = new Vector3f(pos[0], pos[1], pos[2]);
		
		return Matrix4f.viewMatrixDirectional(posVector, viewRay, upVector);
	}
	
	public void scale(float s){
		scaleLog *= s;
		Matrix4f m = new Matrix4f();
		getMatrix(m);
		m.set(0, m.get(0)*s);
		m.set(5, m.get(5)*s);
		m.set(10, m.get(10)*s);
		set(m);
	}
	
	public void setRotation(Quat4f q){
		super.setRotation(q);
		scale(scaleLog);
	}
	
	public void scale(Vector3f s){
		scaleLogV.scl(s);
		Matrix4f m = new Matrix4f();
		getMatrix(m);
		m.set(0, m.get(0)*s.x);
		m.set(5, m.get(5)*s.y);
		m.set(10, m.get(10)*s.z);
		set(m);
	}
	
	public void resetScale(){
		scale(1/scaleLog);
	}
	
	public void resetScaleV(){
		scale(scaleLogV.invert());
	}

	@Override
	public Transform copy() {
		/*Quat4f q4 = new Quat4f();
		getRotation(q4);
		
		Transform t = new Transform(new Matrix4f(
				new Quat4f(q4.x,q4.y,q4.z,q4.w),
				new Vector3f(origin), 1));*/
		Matrix4f m4 = new Matrix4f();
		getMatrix(m4);
		return new Transform(m4);
	}

}
