package math;

import java.nio.FloatBuffer;

import main.state.Copyable;

import org.lwjgl.BufferUtils;

public class Vector2f extends javax.vecmath.Vector2f implements Copyable<Vector2f>{

	private static final long serialVersionUID = 1L;

	
	public Vector2f(){
		super();
	}

	public Vector2f(float x, float y) {
		super(x, y);
	}

	
	public FloatBuffer fb(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(2);
		float[] array = {x, y};
		fb.put(array);
		fb.flip();
		return fb;
	}


	@Override
	public Vector2f copy() {
		return new Vector2f(x, y);
	}
}
