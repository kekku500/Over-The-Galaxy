package test.OBJloader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import test.OBJloader.Face;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;


public class Model {
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texture = new ArrayList<Vector2f>();
	public Map<String, Material> materials = new HashMap<String, Material>();
	public List<ArrayList<Face>> faces = new ArrayList<ArrayList<Face>>();
	public boolean textured;
	public List<Texture> textures = new ArrayList<Texture>();
	
	private int total = 0;
	int vboIndeciesID;
	int vboVertexID;
	int vboTextureID;
	int vboNormalID;
	int vboColorID;
	ShaderProgram shaderProgram;
	List<Integer> koht = new ArrayList<>();
	
/*	public void render()
    {
        // Enable client states
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, faces.get(1).get(0).material.texture.getTextureID());

        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        // Bind the texture buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboTextureID);
        glTexCoordPointer(2, GL_FLOAT, 0, 0);

        // Bind the normal buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
        glNormalPointer(GL_FLOAT, 0, 0);

        // Bind the color buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboColorID);
        glColorPointer(3, GL_FLOAT, 0, 0);

        // Bind the vertex buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glVertexPointer(3, GL_FLOAT, 0, 0);

        // Draw all the faces as triangles
        glDrawArrays(GL_TRIANGLES, 0, 9 * total);

        // Disable client states
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    }*/
	
	public void render(){
	//	textured = false;
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		if(textured){
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		}
		shaderProgram.bind();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(3, GL_FLOAT,44,0);
		glNormalPointer(GL_FLOAT,44,12);
		glColorPointer(3, GL_FLOAT,44,24);
		
		if(textured){
			glEnable(GL_TEXTURE_2D);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndeciesID);
			glBindTexture(GL_TEXTURE_2D, faces.get(2).get(0).material.texture.id);
			glTexCoordPointer(2, GL_FLOAT,44,36);
			
			glDrawElements(GL_TRIANGLES,koht.get(0), GL_UNSIGNED_INT,0);
			
			
			
		}
		//glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndeciesID);
		//glDrawElements(GL_TRIANGLES,koht.get(0), GL_UNSIGNED_INT,0);
		
		//glDrawArrays(GL_TRIANGLES, 0, total);
		ShaderProgram.unbind();
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
		if(textured){
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		}
	}
	
	public void prepareVBO(){
		vboIndeciesID = glGenBuffers();
		vboVertexID = glGenBuffers();
		int x = 0;
		int e = 11;

		
		for(int i = 0; i < faces.size(); i++){
			total += faces.get(i).size();
		}
		float[] buffer = new float[33 * total];
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(33* total);
		IntBuffer indeciesBuffer = BufferUtils.createIntBuffer(3*total);
		
		for(int i = 2; i < faces.size(); i++){
			for(Face face : faces.get(i)){
				Material material = face.material;
				indeciesBuffer.put((int) (face.vertex.x - 1)).put((int) (face.vertex.y - 1)).put((int) (face.vertex.z - 1));
				
				
				x = (int) face.vertex.x - 1;
				Vector3f v1 = vertices.get((int) face.vertex.x - 1);
				Vector3f n1 = normals.get((int) face.normal.x - 1);
				Vector2f t1 = null;
				if(textured)
					t1 = texture.get((int) face.texture.x - 1);
				buffer[e*x] = v1.x;
				buffer[e*x+1] = v1.y;
				buffer[e*x+2] = v1.z;
				buffer[e*x+3] = n1.x;
				buffer[e*x+4] = n1.y;
				buffer[e*x+5] = n1.z;
				buffer[e*x+6] = material.getDiffuse().x;
				buffer[e*x+7] = material.getDiffuse().y;
				buffer[e*x+8] = material.getDiffuse().z;
				if(textured){
					buffer[e*x+9] = t1.x;
					buffer[e*x+10] = 1 - t1.y;
				}
				
				x = (int) face.vertex.y - 1;
				Vector3f v2 = vertices.get((int) face.vertex.y - 1);
				Vector3f n2 = normals.get((int) face.normal.y - 1);
				Vector2f t2 = null;
				if(textured)
					t2 = texture.get((int) face.texture.y - 1);
				buffer[e*x] = v2.x;
				buffer[e*x+1] = v2.y;
				buffer[e*x+2] = v2.z;
				buffer[e*x+3] = n2.x;
				buffer[e*x+4] = n2.y;
				buffer[e*x+5] = n2.z;
				buffer[e*x+6] = material.getDiffuse().x;
				buffer[e*x+7] = material.getDiffuse().y;
				buffer[e*x+8] = material.getDiffuse().z;
				if(textured){
					buffer[e*x+9] = t2.x;
					buffer[e*x+10] = 1 - t2.y;	
				}

				
				x = (int) face.vertex.z -1;
				Vector3f v3 = vertices.get((int) face.vertex.z - 1);
				Vector3f n3 = normals.get((int) face.normal.z - 1);
				Vector2f t3 = null;
				if(textured)
					t3 = texture.get((int) face.texture.z - 1);
				buffer[e*x] = v3.x;
				buffer[e*x+1] = v3.y;
				buffer[e*x+2] = v3.z;
				buffer[e*x+3] = n3.x;
				buffer[e*x+4] = n3.y;
				buffer[e*x+5] = n3.z;
				buffer[e*x+6] = material.getDiffuse().x;
				buffer[e*x+7] = material.getDiffuse().y;
				buffer[e*x+8] = material.getDiffuse().z;
				if(textured){
					buffer[e*x+9] = t3.x;
					buffer[e*x+10] = 1 - t3.y;
				}

			}			
			koht.add(indeciesBuffer.position());
		}
			vertexBuffer.put(buffer);
			
			indeciesBuffer.rewind();
			vertexBuffer.rewind();
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndeciesID);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indeciesBuffer, GL_STATIC_DRAW);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			
			glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
			glBufferData(GL_ARRAY_BUFFER, vertexBuffer , GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			shaderProgram = new ShaderProgram();
			shaderProgram.attachVertexShader("shader.vert");
			shaderProgram.attachFragmentShader("shader.frag");
			
			shaderProgram.link();
	}
	
	public void dispose(){
		glDeleteBuffers(vboVertexID);
	}
}
