package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import math.Matrix4f;
import math.Vector3f;

import org.lwjgl.BufferUtils;

public class Utils {
	
	public static String getFB(FloatBuffer fb){

		float[] f = new float[fb.limit()];
		fb.get(f);
		fb.rewind();
		return Arrays.toString(f);
	}
	
	public static String getIB(IntBuffer fb){

		int[] f = new int[fb.limit()];
		fb.get(f);
		fb.rewind();
		return Arrays.toString(f);
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
	
	public static String removeFileExt(String extensionFile){
		return extensionFile.substring(0, extensionFile.indexOf("."));
	}
	
	
	public static float rads(float degrees){
		return (float)Math.toRadians(degrees);
	}
	
	public static float cos(float val){
		return (float)Math.cos(val);
	}
	
	public static float sin(float val){
		return (float)Math.sin(val);
	}
    
	public static FloatBuffer combineFloatBuffers(FloatBuffer...fbs){
        FloatBuffer send = BufferUtils.createFloatBuffer(fbs[0].capacity()*fbs.length);
        for(FloatBuffer add: fbs){
        	send.put(add);
        }
        send.flip();
        return send;
	}
	
	public static FloatBuffer combineFloatBuffers(Matrix4f...fbs){
        FloatBuffer send = BufferUtils.createFloatBuffer(16*fbs.length);
        for(Matrix4f add: fbs){
        	send.put(add.fb());
        }
        send.flip();
        return send;
	}
	
	public static FloatBuffer combineFBIrregular(FloatBuffer...fbs){
		int size = 0;
		for(FloatBuffer fb: fbs)
			size += fb.capacity();
		
        FloatBuffer send = BufferUtils.createFloatBuffer(size);
        for(FloatBuffer add: fbs){
        	send.put(add);
        }
        send.flip();
        return send;
	}
	
    public static String readFileAsString(String filename) {
        StringBuilder source = new StringBuilder();
        
		File f = new File(filename);
	    FileInputStream in = null;
		try {
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            
            try {
            	String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            }
            catch(Exception exc) {
            	exc.printStackTrace();
            }
            finally {
            	try {
            		reader.close();
            	}
            	catch(Exception exc) {
            		exc.printStackTrace();
            	}
            }
        }
        catch(Exception exc) {
        	exc.printStackTrace();
        }
        finally {
        	try {
        		in.close();
        	}
        	catch(Exception exc) {
        		exc.printStackTrace();
        	}
        }
        
        return source.toString();
    }

}
