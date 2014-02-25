package blender.model;

import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

public class SubModel {
	
	public List<Face> faces = new ArrayList<Face>();
    public boolean isTextured;
    
    public Material material = new Material();
	
	public int vboVertexID;
	public int vboNormalID;
	public int vboColorID;
	public int vboTexVertexID;
	
	private Model masterModel;
	
	public SubModel(Model master){
		masterModel = master;
	}
	
	public void render(boolean drawTexture){
		material.apply();
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
 
        if (drawTexture && isTextured){
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glActiveTexture(GL_TEXTURE0);  
            glBindTexture(GL_TEXTURE_2D, material.textureHandle);
            glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
            glTexCoordPointer(2, GL_FLOAT, 0, 0);
        }else{
        	//glActiveTexture(GL_TEXTURE0);
        	//glBindTexture(GL_TEXTURE_2D, 0);
        }

        // Bind the normal buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
        glNormalPointer(GL_FLOAT, 0, 0);

        // Bind the vertex buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        
        //color
        glBindBuffer(GL_ARRAY_BUFFER, vboColorID);
        glColorPointer(3, GL_FLOAT, 0, 0);

        // Draw all the faces as triangles
        glDrawArrays(GL_TRIANGLES, 0, 9 * faces.size());

        // Disable client states
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        if (drawTexture && isTextured){
        	//glActiveTexture(GL_TEXTURE);
        	glBindTexture(GL_TEXTURE_2D, 0);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        }else{
        	//glActiveTexture(GL_TEXTURE0);
        	//glBindTexture(GL_TEXTURE_2D, 0);
        }

        Material.clear();
        
	}
	
	public void prepareVBO(){
		
		vboVertexID = glGenBuffers();
		vboNormalID = glGenBuffers();
		vboColorID = glGenBuffers();
		
	    if (isTextured){
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
            // Retrieve the material of the face
            
            // Get the first vertex of the face
            Vector3f v1 = vertices.get((int) face.getVertex().x - 1);
            vertexBuffer.put(v1.x).put(v1.y).put(v1.z);
            // Get the color of the vertex
            colorBuffer.put(material.diffuse.get(0))
                       .put(material.diffuse.get(1))
                       .put(material.diffuse.get(2));
            
            // Get the second vertex of the face
            Vector3f v2 = vertices.get((int) face.getVertex().y - 1);
            vertexBuffer.put(v2.x).put(v2.y).put(v2.z);
            // Get the color of the face
            colorBuffer.put(material.diffuse.get(0))
            .put(material.diffuse.get(1))
            .put(material.diffuse.get(2));

            // Get the third vertex of the face
            Vector3f v3 = vertices.get((int) face.getVertex().z - 1);
            vertexBuffer.put(v3.x).put(v3.y).put(v3.z);
            // Get the color of the face
            colorBuffer.put(material.diffuse.get(0))
            .put(material.diffuse.get(1))
            .put(material.diffuse.get(2));

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
	}
	
	public void setMaterial(Material m){
        material = m;
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
