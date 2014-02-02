package math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Vector3fc extends Vector3f{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Vector3fc(){
		super();
	}
	
	public Vector3fc(float x, float y, float z){
		super(x,y,z);
	}
	
	public Point getPoint(){
		return new Point(x, y, z);
	}
	
	public boolean equals(Vector3fc v2){
		if(x == v2.x && y == v2.y && z == v2.z)
			return true;
		return false;
	}
	
	public Vector3fc getAdd(Vector3fc v2){
		return new Vector3fc(x+v2.x,y+v2.y,z+v2.z);
	}
	
	public Vector3fc getNegate(){
		return new Vector3fc(-x,-y,-z);
	}
	
	public Vector3fc getMultiply(float m){
		return new Vector3fc(x*m, y*m, z*m);
	}
	
	public Vector3fc copy(){
		return new Vector3fc(x, y, z);
	}
	
	public static Vector3fc[] getMinMaxVectors(Vector3fc...vectors){
		Vector3fc max = vectors[0].copy();
		Vector3fc min = vectors[0].copy();
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
		return new Vector3fc[]{min, max};
		
	}
	
	public static Vector3fc rotateVector(Vector3fc vectorToCenter, float pitch, float yaw, float roll){
		pitch = (float)Math.toRadians(pitch);
		yaw = (float)Math.toRadians(yaw);
		roll = (float)Math.toRadians(roll);
		Vector4f vecPosMod = new Vector4f(vectorToCenter.x, vectorToCenter.y, vectorToCenter.z, 1.0f);
		
		//rotation around x axis
		Matrix4f transMat = new Matrix4f();
		transMat.rotate(pitch, new Vector3f(1.0f, 0.0f, 0.0f));
		Matrix4f.transform(transMat,  vecPosMod,  vecPosMod);
		
		//rotation around y axis
		transMat = new Matrix4f();
		transMat.rotate(yaw, new Vector3f(0.0f, 1.0f, 0.0f));
		Matrix4f.transform(transMat,  vecPosMod,  vecPosMod);
		
		//rotation around z axis
		transMat = new Matrix4f();
		transMat.rotate(roll, new Vector3f(0.0f, 0.0f, 1.0f));
		Matrix4f.transform(transMat,  vecPosMod,  vecPosMod);
		
		return new Vector3fc(vecPosMod.x, vecPosMod.y, vecPosMod.z);
	}


}
