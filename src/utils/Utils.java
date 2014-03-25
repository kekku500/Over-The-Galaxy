package utils;

import game.Game;
import game.resources.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import utils.math.Matrix4f;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class Utils {
	
	public static String getFB(FloatBuffer fb){
		fb.rewind();
		float[] f = new float[fb.limit()];
		fb.get(f);
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
	
	
	public static float rads(float degrees){
		return (float)Math.toRadians(degrees);
	}
    
    public static Vector3f rotate(Vector3f v, float angle, Vector3f l){
    	Vector4f vecPosMod = new Vector4f(v.x, v.y, v.z, 1.0f);
    	
    	Matrix4f transMat = new Matrix4f();
    	transMat.rotate((float)Math.toRadians(angle), l);
    	transMat.transform(vecPosMod);
    	return new Vector3f(vecPosMod.x, vecPosMod.y, vecPosMod.z);
    }
    
	public static FloatBuffer combineFloatBuffers(FloatBuffer...fbs){
        FloatBuffer send = BufferUtils.createFloatBuffer(fbs[0].capacity()*fbs.length);
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
