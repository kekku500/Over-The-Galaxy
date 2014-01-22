package test.OBJloader;

import org.lwjgl.util.vector.Vector3f;

public class Face{
	public Vector3f vertex = new Vector3f();
	public Vector3f normal = new Vector3f();
	public Vector3f texture = new Vector3f();
	public Face(Vector3f vertex, Vector3f texture, Vector3f normal){
		this.vertex = vertex;
		this.texture = texture;
		this.normal = normal;
	}
	
}
