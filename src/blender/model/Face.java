package blender.model;

import javax.vecmath.Vector3f;

import utils.math.Vector4f;

/**
 * A Face in a Model. Each face is a triangle.
 * 
 * @author Sri Harsha Chilakapati
 */
public class Face
{

    // Vertex indices
    private Vector4f vertex;
    // Normal indices
    private Vector4f normal;
    // TexCoord indices
    private Vector4f texCoords;
    
    private Material material;


    /**
     * Create a new Face with vertex indices, normal indices, texture indices
     * and material
     */
    public Face(Vector4f vertex, Vector4f normal, Vector4f texCoords, Material m){
        this.vertex = vertex;
        this.normal = normal;
        this.texCoords = texCoords;
        
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
    public Vector4f getVertex()
    {
        return vertex;
    }

    /**
     * @return The normal indices
     */
    public Vector4f getNormal()
    {
        return normal;
    }

    /**
     * @return The Texture Indices
     */
    public Vector4f getTexCoord()
    {
        return texCoords;
    }
    
    public Material getMaterial(){
    	return material;
    }
    
}
