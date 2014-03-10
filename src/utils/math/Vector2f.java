package utils.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Vector2f extends javax.vecmath.Vector2f{

	public Vector2f(float x, float y) {
		super(x, y);
	}

	
	public FloatBuffer asFlippedFloatBuffer(){
		FloatBuffer fb = BufferUtils.createFloatBuffer(2);
		float[] array = {x, y};
		fb.put(array);
		fb.flip();
		return fb;
	}
}
