package resources.model.custom;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE_MODE;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.glUniform1i;
import resources.model.Model;
import world.graphics.Graphics3D;

public class Sphere extends Model{
	
	private org.lwjgl.util.glu.Sphere sphere;
	private float radius;
	private int detail1, detail2;
	
	public Sphere(float radius, int detail1, int detail2){
		sphere = new org.lwjgl.util.glu.Sphere();
		this.radius = radius;
		this.detail1 = detail1;
		this.detail2 = detail2;
	}

	public void renderSubModels() {
		glUniform1i(Graphics3D.preprocess.uniformLocations[0], 0); //tex
		glUniform1i(Graphics3D.preprocess.uniformLocations[1], 0); //bump
		int currentCull = glGetInteger(GL_CULL_FACE_MODE);
		if(isGodRays){
	        glCullFace(GL_BACK);
			glUniform1i(Graphics3D.preprocess.uniformLocations[2], 1); //Inform shader of god rays
		}
		sphere.draw(radius, detail1, detail2);
		if(isGodRays){
			glCullFace(currentCull);
			glUniform1i(Graphics3D.preprocess.uniformLocations[2], 0); //Inform shader of god rays
		}
	}
	
	public float getRadius(){
		return radius;
	}


	public void prepareVBO() {}


	public void dispose() {}
	
}
