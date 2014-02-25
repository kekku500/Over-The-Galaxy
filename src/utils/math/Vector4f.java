package utils.math;

public class Vector4f extends javax.vecmath.Vector4f{

	private static final long serialVersionUID = 1L;
	
	public Vector4f(){
		super();
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

}
