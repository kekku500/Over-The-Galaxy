package blender.model.custom;

import blender.model.Model;

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

	public void renderDraw() {
		sphere.draw(radius, detail1, detail2);
	}


	public void prepareVBO() {}


	public void dispose() {}
	
}
