package blender.model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1i;
import game.world.World;
import game.world.graphics.RenderEngine3D;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexShader;

import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

import utils.math.Vector2f;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class SubModel {
	
	public List<Face> faces = new ArrayList<Face>();
    public boolean isTextured;
    public boolean isNormalMapped;
    
    public Material material = new Material();
	
	public int vboVertexID;
	public int vboNormalID;
	public int vboColorID;
	public int vboTexVertexID;
	
	public int vertexCount, textureCount, normalCount;
	
	public boolean reverseCull = false;
	
	private Model masterModel;
	
	public SubModel(Model master){
		masterModel = master;
	}
	
	public SubModel(){}
	
	public IntBuffer getVertexIndices(){
		IntBuffer b = BufferUtils.createIntBuffer(faces.size()*3);
		for(Face f: faces){
			Vector3f v = f.getVertexIndices();
			b.put(((int)v.x)).put((int)v.y).put((int)v.z);
		}
		b.rewind();
		return b;
	}
	
	public void render(){
		if(!Model.drawVertices()) //no point trying to draw without vertices enabled
			return;
		if(Model.drawMaterial()){
	        glEnable(GL_COLOR_MATERIAL);
			material.apply();
			
		}
		if(Model.drawNormals()){ 
		    glEnableClientState(GL_NORMAL_ARRAY);
	        glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
	        glNormalPointer(GL_FLOAT, 0, 0);
		}
		
	    glEnableClientState(GL_VERTEX_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        
        //if model is textured and textures are enabled
        if(isTextured && Model.drawTextures()){
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
            glTexCoordPointer(2, GL_FLOAT, 0, 0);
        	glUniform1i(World.renderEngine.preprocess.uniformLocations[0], 1); //Tell shader that textures are coming
        	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, material.textureHandle);
        }else if(Model.drawColors()){
        	glEnableClientState(GL_COLOR_ARRAY);
	        glBindBuffer(GL_ARRAY_BUFFER, vboColorID);
	        glColorPointer(3, GL_FLOAT, 0, 0);
	        glUniform1i(World.renderEngine.preprocess.uniformLocations[0], 0); //Not using textures
        }
        //if model is normalmapped and normalmapping is enabled
		if(isNormalMapped && Model.drawNormalMapping()){
			glEnableVertexAttribArray(World.renderEngine.preprocess.attribLocations[0]);
			ARBVertexShader.glVertexAttribPointerARB(World.renderEngine.preprocess.attribLocations[0], 3, GL_FLOAT, false, 0, 0);
			glUniform1i(World.renderEngine.preprocess.uniformLocations[1], 1); //Inform shader of normalmapping
			glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, material.normalHandle); //normalmap texture
		}else{
			glUniform1i(World.renderEngine.preprocess.uniformLocations[1], 0); //not normalmapping
		}
		
		if(masterModel.isGodRays){
			glUniform1i(World.renderEngine.preprocess.uniformLocations[2], 1); //Inform shader of god rays
		}
		
		glDrawArrays(GL_TRIANGLES, 0, 9 * faces.size());  
		
		if(masterModel.isGodRays){
			glUniform1i(World.renderEngine.preprocess.uniformLocations[2], 0); //Inform shader of god rays
		}

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableClientState(GL_VERTEX_ARRAY);
		if(Model.drawNormals()){ 
			 glDisableClientState(GL_NORMAL_ARRAY);
		} 
       
        if(isNormalMapped && Model.drawNormalMapping()){
        	glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        	glDisableVertexAttribArray(World.renderEngine.preprocess.attribLocations[0]);
        }
        if(isTextured && Model.drawTextures()){
        	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        }else if(Model.drawColors()){
        	glDisableClientState(GL_COLOR_ARRAY);
        }
        if(Model.drawMaterial())
        	Material.clear();
        	glDisable(GL_COLOR_MATERIAL);
	}
	
	public void prepareVBOVertices(Vector3f[] vertices){
		vboVertexID = glGenBuffers();
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length * 3);
		for(Vector3f v: vertices){
			vertexBuffer.put(v.x*4).put(v.y*4).put(v.z*4);
		}
        vertexBuffer.rewind();
        
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void prepareVBO(){
		
		vboVertexID = glGenBuffers();
		vboNormalID = glGenBuffers();
		vboColorID = glGenBuffers();
		
	    if (isTextured){
	    	material.loadTexture();
	    	if(material.normalHandle != 0)
	    		isNormalMapped = true;
	    	vboTexVertexID = glGenBuffers();
	    }
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
		FloatBuffer normalBuffer  = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer textureBuffer = null;
        if (isTextured){
            textureBuffer = BufferUtils.createFloatBuffer(2 * 3 * faces.size());
        }
        
        List<Vector3f> vertices = masterModel.vertices;
        List<Vector3f> normals = masterModel.normals;
        List<Vector2f> texCoords = masterModel.texCoords;
		for(Face face: faces){
			//http://www.vrarchitect.net/anu/cg/surfaceModeling/polygonMesh.en.html
			Material material = face.getMaterial();
            
            // Get the first vertex of the face
            Vector3f v1 = vertices.get((int) face.getVertexIndices().x);
            vertexBuffer.put(v1.x).put(v1.y).put(v1.z);
            // Get the color of the vertex
            colorBuffer.put(material.diffuse.get(0))
                       .put(material.diffuse.get(1))
                       .put(material.diffuse.get(2));
            
            // Get the second vertex of the face
            Vector3f v2 = vertices.get((int) face.getVertexIndices().y);
            vertexBuffer.put(v2.x).put(v2.y).put(v2.z);
            // Get the color of the face
            colorBuffer.put(material.diffuse.get(0))
            .put(material.diffuse.get(1))
            .put(material.diffuse.get(2));

            // Get the third vertex of the face
            Vector3f v3 = vertices.get((int) face.getVertexIndices().z);
            vertexBuffer.put(v3.x).put(v3.y).put(v3.z);
            // Get the color of the face
            colorBuffer.put(material.diffuse.get(0))
            .put(material.diffuse.get(1))
            .put(material.diffuse.get(2));

            // Get the first normal of the face
            Vector3f n1 = normals.get((int) face.getNormalIndices().x);
            normalBuffer.put(n1.x).put(n1.y).put(n1.z);

            // Get the second normal of the face
            Vector3f n2 = normals.get((int) face.getNormalIndices().y);
            normalBuffer.put(n2.x).put(n2.y).put(n2.z);

            // Get the third normal of the face
            Vector3f n3 = normals.get((int) face.getNormalIndices().z);
            normalBuffer.put(n3.x).put(n3.y).put(n3.z);

            
           if (isTextured){
                // Get the first texCoords of the face
                Vector2f t1 = texCoords.get((int) face.getTexIndices().x); 
                textureBuffer.put(t1.x).put(1 - t1.y);

                // Get the second texCoords of the face
                Vector2f t2 = texCoords.get((int) face.getTexIndices().y);
                textureBuffer.put(t2.x).put(1 - t2.y);

                // Get the third texCoords of the face
                Vector2f t3 = texCoords.get((int) face.getTexIndices().z);
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
