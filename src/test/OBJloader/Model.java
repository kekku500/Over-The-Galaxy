package test.OBJloader;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import test.OBJloader.Face;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Model {
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texture = new ArrayList<Vector2f>();
	public Map<String, Material> materials = new HashMap<String, Material>();
	public List<Face> faces = new ArrayList<Face>();
	
	int vboVertexID;
	int vboNormalID;
	int vboColorID;
	
	public void Render(){
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
		glNormalPointer(GL_FLOAT,0,0);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboColorID);
		glColorPointer(3, GL_FLOAT,0,0);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(3, GL_FLOAT,0,0);
		
		glDrawArrays(GL_TRIANGLES, 0, 9 * faces.size());
		
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
	}
	
	public void prepareVBO(){
		vboNormalID = glGenBuffers();
		vboVertexID = glGenBuffers();
		vboColorID = glGenBuffers();
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(9 * faces.size());
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(9 * faces.size());
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(9 * faces.size());
		
		for(Face face : faces){
			Material material = face.material;
			
			Vector3f v1 = vertices.get((int) face.vertex.x - 1);
			vertexBuffer.put(v1.x).put(v1.y).put(v1.z);
			colorBuffer.put(material.diffuse.x)
					   .put(material.diffuse.y)
					   .put(material.diffuse.z);
			
			
			Vector3f v2 = vertices.get((int) face.vertex.y - 1);
			vertexBuffer.put(v2.x).put(v2.y).put(v2.z);
			colorBuffer.put(material.diffuse.x)
					   .put(material.diffuse.y)
					   .put(material.diffuse.z);
			Vector3f n2 = normals.get((int) face.normal.y -1);
			normalBuffer.put(n2.x).put(n2.y).put(n2.z);
			
			Vector3f v3 = vertices.get((int) face.vertex.z - 1);
			vertexBuffer.put(v3.x).put(v3.y).put(v3.z);
			colorBuffer.put(material.diffuse.x)
					   .put(material.diffuse.y)
					   .put(material.diffuse.z);
			Vector3f n3 = normals.get((int) face.normal.z -1);
			normalBuffer.put(n3.x).put(n3.y).put(n3.z);
			
			vertexBuffer.rewind();
			normalBuffer.rewind();
			colorBuffer.rewind();
			
			glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
			glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
			glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, vboColorID);
			glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void dispose(){
		glDeleteBuffers(vboVertexID);
		glDeleteBuffers(vboNormalID);
		glDeleteBuffers(vboColorID);
	}
}
