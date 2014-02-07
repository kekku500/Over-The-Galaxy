package test.OBJloader;

import org.lwjgl.util.vector.Vector3f;
import test.OBJloader.Texture;
//import org.newdawn.slick.opengl.Texture;

public class Material {
	public Vector3f diffuse = new Vector3f();
	public Vector3f ambient = new Vector3f();
	public Vector3f specularity = new Vector3f();
	public float refraction;
	public float shininess;
	public float alpha;
	public String name;
	public Texture texture;
	
	public void setName(String name){
		this.name = name;
	}
	
	public Vector3f getDiffuse(){
		return diffuse;
	}
	
	public void setRefraction(float refraction){
		this.refraction = refraction;
	}
	
	public void setDiffuse(Vector3f diffuse){
		this.diffuse = diffuse;
	}
	
	public void setAmbient(Vector3f ambient){
		this.ambient = ambient;
	}
	
	public void setSpecualrity(Vector3f specularity){
		this.specularity = specularity;
	}
	
	public void setShininess(float shininess){
		this.shininess = shininess;
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
	}
	
	public String getName(){
		return name;
	}
	
}
