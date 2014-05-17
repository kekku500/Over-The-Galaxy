package resources.model;

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
import graphics.Graphics3D;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import main.Config;
import math.Vector2f;
import math.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexShader;

public class SubModel {
	
	public List<Face> faces = new ArrayList<Face>();
	public List<Vector2f> texCoords = new ArrayList<Vector2f>();
    public boolean isTextured;
    public boolean isNormalMapped;
    
    public Material material = new Material();
	
	public int vboVertexID;
	public int vboNormalID;
	public int vboTexVertexID;
	public int vboAmbientID, vboDiffuseID, vboSpecularID, vboEmissionShininessID, vboShininessID;
	
	public int vertexCount, textureCount, normalCount;
	public String[] altasTextureData = null;
	
	public void setAltasTextureData(String[] data){
		altasTextureData = data;
	}
	
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
			glBindBuffer(GL_ARRAY_BUFFER, vboAmbientID);
			ARBVertexShader.glVertexAttribPointerARB(Graphics3D.preprocess.attribLocations[1], 3, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(Graphics3D.preprocess.attribLocations[1]);
			
			glBindBuffer(GL_ARRAY_BUFFER, vboSpecularID);
			ARBVertexShader.glVertexAttribPointerARB(Graphics3D.preprocess.attribLocations[2], 3, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(Graphics3D.preprocess.attribLocations[2]);
			
			glBindBuffer(GL_ARRAY_BUFFER, vboEmissionShininessID);
			ARBVertexShader.glVertexAttribPointerARB(Graphics3D.preprocess.attribLocations[3], 4, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(Graphics3D.preprocess.attribLocations[3]);
			
	    	glEnableClientState(GL_COLOR_ARRAY);
	        glBindBuffer(GL_ARRAY_BUFFER, vboDiffuseID);
	        glColorPointer(3, GL_FLOAT, 0, 0);
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
        	glUniform1i(Graphics3D.preprocess.uniformLocations[0], 1); //Tell shader that textures are coming
        	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, material.texture.getID());
        }else{
        	glUniform1i(Graphics3D.preprocess.uniformLocations[0], 0); //Not using textures
        }
        //if model is normalmapped and normalmapping is enabled
		if(isNormalMapped && Model.drawNormalMapping()){
			glEnableVertexAttribArray(Graphics3D.preprocess.attribLocations[0]);
			ARBVertexShader.glVertexAttribPointerARB(Graphics3D.preprocess.attribLocations[0], 3, GL_FLOAT, false, 0, 0);
			glUniform1i(Graphics3D.preprocess.uniformLocations[1], 1); //Inform shader of normalmapping
			glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, material.normalMap.getID()); //normalmap texture
		}else{
			glUniform1i(Graphics3D.preprocess.uniformLocations[1], 0); //not normalmapping
		}
		
		if(masterModel.isGodRays){
			glUniform1i(Graphics3D.preprocess.uniformLocations[2], 1); //Inform shader of god rays
		}
		
		/*int f = faces.size();
		if(totalFaceCount != 0)
			f = totalFaceCount;*/
		glDrawArrays(GL_TRIANGLES, 0, 9 * faces.size());  
		
		if(masterModel.isGodRays){
			glUniform1i(Graphics3D.preprocess.uniformLocations[2], 0); //Inform shader of god rays
		}

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableClientState(GL_VERTEX_ARRAY);

		if(Model.drawNormals()){ 
			 glDisableClientState(GL_NORMAL_ARRAY);
		} 
       
        if(isNormalMapped && Model.drawNormalMapping()){
        	glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        	glDisableVertexAttribArray(Graphics3D.preprocess.attribLocations[0]);
        }
        if(isTextured && Model.drawTextures()){
        	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        }
        if(Model.drawMaterial()){
            glDisableClientState(GL_COLOR_ARRAY);
			glDisableVertexAttribArray(Graphics3D.preprocess.attribLocations[1]);
			glDisableVertexAttribArray(Graphics3D.preprocess.attribLocations[2]);
			glDisableVertexAttribArray(Graphics3D.preprocess.attribLocations[3]);
        }
	}
	
	public void prepareVBOVertices(Vector3f[] vertices){
		vboVertexID = glGenBuffers();
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length * 3);
		for(Vector3f v: vertices){
			vertexBuffer.put(v.x*(Config.Z_NEAR+1)).put(v.y*(Config.Z_NEAR+1)).put(v.z*(Config.Z_NEAR+1));
		}
        vertexBuffer.rewind();
        
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	
	public void prepareCombinedVBOSubModels(List<SubModel> combineTheseSubModels, boolean updateTexCoords){
		vboVertexID = glGenBuffers();
		vboNormalID = glGenBuffers();
		vboAmbientID = glGenBuffers();
		vboDiffuseID = glGenBuffers();
		vboSpecularID = glGenBuffers();
		vboEmissionShininessID = glGenBuffers();
		
		if(isTextured){
	    	material.loadTexture();
	    	vboTexVertexID = glGenBuffers();
		}
    	
    	//int faceCount = 0;
    	for(SubModel m: combineTheseSubModels){
    		//faceCount += m.faces.size();
    		faces.addAll(m.faces);
    	}
    	//totalFaceCount = faceCount;
    	
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
		FloatBuffer normalBuffer  = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer ambientBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer diffuseBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer specularBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer emissionShininessBuffer = BufferUtils.createFloatBuffer(4 * 3 * faces.size());
        
        FloatBuffer textureBuffer = null;
        
		if(isTextured){
			textureBuffer = BufferUtils.createFloatBuffer(2 * 3 * faces.size());
		}
        
        List<Vector3f> vertices = masterModel.vertices;
        List<Vector3f> normals = masterModel.normals;
        //List<Vector2f> texCoords = masterModel.texCoords;
        

        //fix texture coords according to atlas
        if(updateTexCoords){
            int neww = material.texture.getWidth();
            int newh = material.texture.getHeight();
            for(SubModel m: combineTheseSubModels){
            	String[] correctTexInfo = m.altasTextureData;
            	int x = Integer.parseInt(correctTexInfo[2]);
            	int y = Integer.parseInt(correctTexInfo[3]);
            	int w = Integer.parseInt(correctTexInfo[4]);
            	int h = Integer.parseInt(correctTexInfo[5]);
            	for(Vector2f texCoord: m.texCoords){
            		float oldtx = texCoord.x;
            		float oldty = texCoord.y;
            		
            		float newtx = ModelUtils.texCoordSwitch(oldtx, w, x, neww);//((oldtx*w+x)/neww);
            		float newty = ModelUtils.texCoordSwitch(oldty, h, y, newh);
            		texCoord.set(newtx, newty);
            	}
            	texCoords.addAll(m.texCoords); //fixed texture coords
            }
        }else{
            for(SubModel m: combineTheseSubModels){
            	texCoords.addAll(m.texCoords);
            }
        }

        
        //for(SubModel m: combineTheseSubModels){
    		for(Face face: faces){
    			Material material = face.getMaterial();
    			
                // Get the first vertex of the face
                Vector3f v1 = vertices.get((int) face.getVertexIndices().x);
                vertexBuffer.put(v1.x).put(v1.y).put(v1.z);
                
                // Get the second vertex of the face
                Vector3f v2 = vertices.get((int) face.getVertexIndices().y);
                vertexBuffer.put(v2.x).put(v2.y).put(v2.z);
                
                // Get the third vertex of the face
                Vector3f v3 = vertices.get((int) face.getVertexIndices().z);
                vertexBuffer.put(v3.x).put(v3.y).put(v3.z);
                
                // Get the color of the first vertex
                ambientBuffer.put(material.ambient.get(0)).put(material.ambient.get(1)).put(material.ambient.get(2));
                diffuseBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));
                specularBuffer.put(material.specular.get(0)).put(material.specular.get(1)).put(material.specular.get(2));
                emissionShininessBuffer.put(material.emission.get(0)).put(material.emission.get(1)).put(material.emission.get(2))
                .put(material.shininess.get(0));

                //  Get the color of the second vertex
                ambientBuffer.put(material.ambient.get(0)).put(material.ambient.get(1)).put(material.ambient.get(2));
                diffuseBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));
                specularBuffer.put(material.specular.get(0)).put(material.specular.get(1)).put(material.specular.get(2));
                emissionShininessBuffer.put(material.emission.get(0)).put(material.emission.get(1)).put(material.emission.get(2))
                .put(material.shininess.get(0));

                //  Get the color of the thrid vertex
                ambientBuffer.put(material.ambient.get(0)).put(material.ambient.get(1)).put(material.ambient.get(2));
                diffuseBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));
                specularBuffer.put(material.specular.get(0)).put(material.specular.get(1)).put(material.specular.get(2));
                emissionShininessBuffer.put(material.emission.get(0)).put(material.emission.get(1)).put(material.emission.get(2))
                .put(material.shininess.get(0));

                // Get the first normal of the face
                Vector3f n1 = normals.get((int) face.getNormalIndices().x);
                normalBuffer.put(n1.x).put(n1.y).put(n1.z);

                // Get the second normal of the face
                Vector3f n2 = normals.get((int) face.getNormalIndices().y);
                normalBuffer.put(n2.x).put(n2.y).put(n2.z);

                // Get the third normal of the face
                Vector3f n3 = normals.get((int) face.getNormalIndices().z);
                normalBuffer.put(n3.x).put(n3.y).put(n3.z);
                
                if(isTextured){
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
        //}
        
	    //Rewind the buffers
        vertexBuffer.rewind();
        normalBuffer.rewind();
        ambientBuffer.rewind();
        diffuseBuffer.rewind();
        specularBuffer.rewind();
        emissionShininessBuffer.rewind();
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
        glBindBuffer(GL_ARRAY_BUFFER, vboAmbientID);
        glBufferData(GL_ARRAY_BUFFER, ambientBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // Create the color VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboDiffuseID);
        glBufferData(GL_ARRAY_BUFFER, diffuseBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // Create the color VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboSpecularID);
        glBufferData(GL_ARRAY_BUFFER, specularBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // Create the color VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboEmissionShininessID);
        glBufferData(GL_ARRAY_BUFFER, emissionShininessBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        if (isTextured){
            // Create the texture VBO
            glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
	}
	
	public void prepareVBO(){
		
		vboVertexID = glGenBuffers();
		vboNormalID = glGenBuffers();
		vboAmbientID = glGenBuffers();
		vboDiffuseID = glGenBuffers();
		vboSpecularID = glGenBuffers();
		vboEmissionShininessID = glGenBuffers();
		
	    if (isTextured){
	    	material.loadTexture();
	    	if(material.normalMap != null)
	    		isNormalMapped = true;
	    	vboTexVertexID = glGenBuffers();
	    }
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
		FloatBuffer normalBuffer  = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        
        FloatBuffer ambientBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer diffuseBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer specularBuffer = BufferUtils.createFloatBuffer(3 * 3 * faces.size());
        FloatBuffer emissionShininessBuffer = BufferUtils.createFloatBuffer(4 * 3 * faces.size());
        
        FloatBuffer textureBuffer = null;
        if (isTextured){
            textureBuffer = BufferUtils.createFloatBuffer(2 * 3 * faces.size());
        }
        
        List<Vector3f> vertices = masterModel.vertices;
        List<Vector3f> normals = masterModel.normals;
        //List<Vector2f> texCoords = masterModel.texCoords;
		for(Face face: faces){
			Material material = face.getMaterial();
			
            // Get the first vertex of the face
            Vector3f v1 = vertices.get((int) face.getVertexIndices().x);
            vertexBuffer.put(v1.x).put(v1.y).put(v1.z);
            
            // Get the second vertex of the face
            Vector3f v2 = vertices.get((int) face.getVertexIndices().y);
            vertexBuffer.put(v2.x).put(v2.y).put(v2.z);
            
            // Get the third vertex of the face
            Vector3f v3 = vertices.get((int) face.getVertexIndices().z);
            vertexBuffer.put(v3.x).put(v3.y).put(v3.z);
            
            // Get the color of the first vertex
            ambientBuffer.put(material.ambient.get(0)).put(material.ambient.get(1)).put(material.ambient.get(2));
            diffuseBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));
            specularBuffer.put(material.specular.get(0)).put(material.specular.get(1)).put(material.specular.get(2));
            emissionShininessBuffer.put(material.emission.get(0)).put(material.emission.get(1)).put(material.emission.get(2))
            .put(material.shininess.get(0));

            //  Get the color of the second vertex
            ambientBuffer.put(material.ambient.get(0)).put(material.ambient.get(1)).put(material.ambient.get(2));
            diffuseBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));
            specularBuffer.put(material.specular.get(0)).put(material.specular.get(1)).put(material.specular.get(2));
            emissionShininessBuffer.put(material.emission.get(0)).put(material.emission.get(1)).put(material.emission.get(2))
            .put(material.shininess.get(0));

            //  Get the color of the thrid vertex
            ambientBuffer.put(material.ambient.get(0)).put(material.ambient.get(1)).put(material.ambient.get(2));
            diffuseBuffer.put(material.diffuse.get(0)).put(material.diffuse.get(1)).put(material.diffuse.get(2));
            specularBuffer.put(material.specular.get(0)).put(material.specular.get(1)).put(material.specular.get(2));
            emissionShininessBuffer.put(material.emission.get(0)).put(material.emission.get(1)).put(material.emission.get(2))
            .put(material.shininess.get(0));

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
        ambientBuffer.rewind();
        diffuseBuffer.rewind();
        specularBuffer.rewind();
        emissionShininessBuffer.rewind();
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
        glBindBuffer(GL_ARRAY_BUFFER, vboAmbientID);
        glBufferData(GL_ARRAY_BUFFER, ambientBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // Create the color VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboDiffuseID);
        glBufferData(GL_ARRAY_BUFFER, diffuseBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // Create the color VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboSpecularID);
        glBufferData(GL_ARRAY_BUFFER, specularBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // Create the color VBO
        glBindBuffer(GL_ARRAY_BUFFER, vboEmissionShininessID);
        glBufferData(GL_ARRAY_BUFFER, emissionShininessBuffer, GL_STATIC_DRAW);
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
        glDeleteBuffers(vboAmbientID);
        glDeleteBuffers(vboDiffuseID);
        glDeleteBuffers(vboSpecularID);
        glDeleteBuffers(vboEmissionShininessID);
        if (isTextured){
            glDeleteBuffers(vboTexVertexID);      
        }
	}

}
