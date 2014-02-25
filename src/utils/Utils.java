package utils;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import utils.math.Matrix4f;
import utils.math.Vector3f;
import utils.math.Vector4f;

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
	
	public static FloatBuffer asFloatBuffer(Vector4f v){
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		fb.put(v.x).put(v.y).put(v.z).put(v.w);
		fb.flip();
		return fb;
	}
	
    /**
	* @param values the float values that are to be turned into a FloatBuffer
	*
	* @return a FloatBuffer readable to OpenGL (not to you!) containing values
	*/
    public static FloatBuffer asFlippedFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }
    
    public static Vector3f rotate(Vector3f v, float angle, Vector3f l){
    	Vector4f vecPosMod = new Vector4f(v.x, v.y, v.z, 1.0f);
    	
    	Matrix4f transMat = new Matrix4f();
    	transMat.rotate((float)Math.toRadians(angle), l);
    	transMat.transform(vecPosMod);
    	return new Vector3f(vecPosMod.x, vecPosMod.y, vecPosMod.z);
    }
    
	public static FloatBuffer combineFloatBuffers(FloatBuffer...fbs){
        FloatBuffer send = BufferUtils.createFloatBuffer(16*fbs.length);
        for(FloatBuffer add: fbs){
        	send.put(add);
        }
        send.flip();
        return send;
	}

}
