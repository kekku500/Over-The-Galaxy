package utils.math;

import state.Copyable;

public class Quat4f extends javax.vecmath.Quat4f implements Copyable<Quat4f>{

	private static final long serialVersionUID = 1L;
	
	public Quat4f(){
		super();
	}
	
	public Quat4f(Quat4f q){
		super(q);
	}
	
	public Quat4f(float x, float y, float z, float w){
		super(x, y, z, w);
	}

	@Override
	public Quat4f copy() {
		return new Quat4f(x, y, z, w);
	}
	
	public static Quat4f getRotation(Vector3f axis, float angle_in_rads){
        float d = axis.length();
        assert (d != 0f);
        float s = (float)Math.sin(angle_in_rads * 0.5f) / d;
        return new Quat4f(axis.x * s, axis.y * s, axis.z * s, (float) Math.cos(angle_in_rads * 0.5f));
	}

}
