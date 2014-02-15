package blender.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL13.*;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import test.OBJloader.ShaderProgram;
import utils.Utils;

public class SubModel {
	
	public List<Face> faces = new ArrayList<Face>();
    public boolean isTextured;
    
    private Material material = new Material();
	
	private int vboVertexID;
	private int vboNormalID;
	private int vboColorID;
	private int vboTexVertexID;
	
	private Model masterModel;
	
	ShaderProgram shaderProgram;
	
	public SubModel(Model master){
		masterModel = master;
	}
	
	public void render(){
		material.apply();
		shaderProgram.bind();
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        
        if (isTextured){
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        	glEnable(GL_TEXTURE_2D);   
            
            glBindTexture(GL_TEXTURE_2D, material.textureHandle);
            glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
            glTexCoordPointer(2, GL_FLOAT, 0, 0);
        }
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_COLOR_MATERIAL);
            
        glBindBuffer(GL_ARRAY_BUFFER, vboColorID);
        glColorPointer(3, GL_FLOAT, 0, 0);
        
        
        // Bind the normal buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
        glNormalPointer(GL_FLOAT, 0, 0);

        // Bind the vertex buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glVertexPointer(3, GL_FLOAT, 0, 0);

        // Draw all the faces as triangles
        glDrawArrays(GL_TRIANGLES, 0, 9 * faces.size());

        // Disable client states
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        
        glDisable(GL_COLOR_MATERIAL);

        if (isTextured){
            glDisable(GL_TEXTURE_2D);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        }
        glDisable(GL_COLOR_MATERIAL);
        glDisableClientState(GL_COLOR_ARRAY);
        
        Material.clear();
	}
	
	public void prepareVBO(){
		
		vboVertexID = glGenBuffers();
		vboNormalID = glGenBuffers();
		vboColorID = glGenBuffers();
		
	    if (isTextured){
			//Load texture first
	    	material.loadTexture();
	    	vboTexVertexID = glGenBuffers();
	    }
		
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(9 * faces.size());
		FloatBuffer normalBuffer  = BufferUtils.createFloatBuffer(9 * faces.size());
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(9 * faces.size());
        FloatBuffer textureBuffer = null;
        if (isTextured){
            textureBuffer = BufferUtils.createFloatBuffer(6 * faces.size());
        }
        
        List<Vector3f> vertices = masterModel.vertices;
        List<Vector3f> normals = masterModel.normals;
        List<Vector2f> texCoords = masterModel.texCoords;
		for(Face face: faces){        
			Material material = face.getMaterial();
            // Get the first vertex of the face
            Vector3f v1 = vertices.get((int) face.getVertex().x - 1);
            vertexBuffer.put(v1.x).put(v1.y).put(v1.z);
            // Get the color of the vertex
            colorBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));
            
            // Get the second vertex of the face
            Vector3f v2 = vertices.get((int) face.getVertex().y - 1);
            vertexBuffer.put(v2.x).put(v2.y).put(v2.z);
            // Get the color of the face
            colorBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));

            // Get the third vertex of the face
            Vector3f v3 = vertices.get((int) face.getVertex().z - 1);
            vertexBuffer.put(v3.x).put(v3.y).put(v3.z);
            // Get the color of the face
            colorBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));

            // Get the first normal of the face
            Vector3f n1 = normals.get((int) face.getNormal().x - 1);
            normalBuffer.put(n1.x).put(n1.y).put(n1.z);

            // Get the second normal of the face
            Vector3f n2 = normals.get((int) face.getNormal().y - 1);
            normalBuffer.put(n2.x).put(n2.y).put(n2.z);

            // Get the third normal of the face
            Vector3f n3 = normals.get((int) face.getNormal().z - 1);
            normalBuffer.put(n3.x).put(n3.y).put(n3.z);
            
            if (isTextured){
                // Get the first texCoords of the face
                Vector2f t1 = texCoords.get((int) face.getTexCoord().x - 1); 
                textureBuffer.put(t1.x).put(1 - t1.y);

                // Get the second texCoords of the face
                Vector2f t2 = texCoords.get((int) face.getTexCoord().y - 1);
                textureBuffer.put(t2.x).put(1 - t2.y);

                // Get the third texCoords of the face
                Vector2f t3 = texCoords.get((int) face.getTexCoord().z - 1);
                textureBuffer.put(t3.x).put(1 - t3.y);
            }
		    
		}
		
	    //Rewind the buffers
        vertexBuffer.rewind();
        normalBuffer.rewind();
        colorBuffer.rewind();
        if (isTextured){
            textureBuffer.rewind();
        }
        
        // Create the vertex VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Create the normal VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Create the color VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboColorID);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        if (isTextured){
            // Create the texture VBO
            glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        
		shaderProgram = new ShaderProgram();
		shaderProgram.attachVertexShader("shader.vert");
		shaderProgram.attachFragmentShader("shader.frag");
		
		shaderProgram.link();
	}
	
	public void setMaterial(Material m){
        material = m;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public void dispose(){
		glDeleteBuffers(vboVertexID);
		glDeleteBuffers(vboNormalID);
        glDeleteBuffers(vboColorID);
        if (isTextured){
            glDeleteBuffers(vboTexVertexID);
            glDeleteBuffers(material.textureHandle);
  
        }
	}

}
