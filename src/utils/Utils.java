package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import main.Main;

public class Utils {
	
	public static String getFloatBufferContents(FloatBuffer fb){
		fb.rewind();
		float[] f = new float[fb.limit()];
		fb.get(f);
		return Arrays.toString(f);
	}
	
	public static float startTime;
	public static void startTimer(){
		startTime = System.currentTimeMillis();
	}
	
	public static float getElapsedTime(){
		float finishTime = System.currentTimeMillis();
		return (finishTime - startTime);
	}
	
	public static Vector3f[] getMinMaxVectors(Vector3f...vectors){
		Vector3f max = new Vector3f(vectors[0].x, vectors[0].y, vectors[0].z);
		Vector3f min = new Vector3f(vectors[0].x, vectors[0].y, vectors[0].z);
		for(int i = 1;i<vectors.length;i++){
			if(vectors[i].x > max.x)
				max.x = vectors[i].x;
			else if(vectors[i].x < min.x)
				min.x = vectors[i].x;
			if(vectors[i].y > max.y)
				max.y = vectors[i].y;
			else if(vectors[i].y < min.y)
				min.y = vectors[i].y;
			if(vectors[i].z > max.z)
				max.z = vectors[i].z;
			else if(vectors[i].z < min.z)
				min.z = vectors[i].z;
		}
		return new Vector3f[]{min, max};
		
	}
	
	public static Vector3f copy(Vector3f v){
		return new Vector3f(v.x, v.y, v.z);
	}
	
	public static float rads(float degrees){
		return (float)Math.toRadians(degrees);
	}
	
	public static FloatBuffer asFloatBuffer(float[] values){
		FloatBuffer fb = BufferUtils.createFloatBuffer(values.length);
		fb.put(values);
		fb.flip();
		return fb;
	}

}
