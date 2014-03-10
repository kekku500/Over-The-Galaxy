package utils.math;

public class Transform extends com.bulletphysics.linearmath.Transform{
	
	private float scaleLog = 1;
	
	public void scale(float s){
		scaleLog *= s;
		Matrix4f m = new Matrix4f();
		getMatrix(m);
		m.set(0, m.get(0)*s);
		m.set(5, m.get(5)*s);
		m.set(10, m.get(10)*s);
		set(m);
	}
	
	public void resetScale(){
		scale(1/scaleLog);
	}

}
