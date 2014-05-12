package resources.model;

import utils.math.Vector3f;

/**
 * A Face in a Model. Each face is a triangle.
 * @author Sri Harsha Chilakapati
 */
public class Face{

    // Vertex indices
	private Vector3f vertexIndices = new Vector3f();
    //private Vector4f vertex;
    // Normal indices
	private Vector3f normalIndices = new Vector3f();
    //private Vector4f normal;
    // TexCoord indices
	private Vector3f textureIndices = new Vector3f();
    //private Vector4f texCoords;
    
    private Material material;
    
    public Face(){
    	
    }
    /**
     * Create a new Face with vertex indices, normal indices, texture indices
     * and material
     */
    public Face(Vector3f vertexIndices, Vector3f normalIndices, Vector3f texIndices, Material m){
        this.vertexIndices = vertexIndices;
        this.normalIndices = normalIndices;
        this.textureIndices = texIndices;
        
        if(m != null)
        	material = m;
        else{
        	material = new Material();
        	material.setDefaults();
        }

    }

    /**
     * @return The vertex indices
     */
    public Vector3f getVertexIndices(){
        return vertexIndices;
    }

    /**
     * @return The normal indices
     */
    public Vector3f getNormalIndices(){
        return normalIndices;
    }

    /**
     * @return The Texture Indices
     */
    public Vector3f getTexIndices(){
        return textureIndices;
    }
    
    public void setMaterial(Material m){
    	material = m;
    }

    public Material getMaterial(){
    	return material;
    }
    
}
