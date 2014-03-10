package blender.model.custom;

import blender.model.Model;
import utils.math.Vector3f;

public class Plane extends Model{
	
	private float w, h ,d;
	private float radius;
	
	public Plane(float w, float h, float d){
		super("common\\plane.obj", false);
		this.w = w;
		this.h = h;
		this.d = d;
		
		if(w != 0 || h != 0 || d != 0){
			//change cuboid size
			vertices.add(new Vector3f(w/2,h,d/2));
			vertices.add(new Vector3f(-w/2,h,d/2));
			vertices.add(new Vector3f(w/2,h,-d/2));
			vertices.add(new Vector3f(-w/2,h,-d/2));
			
			Vector3f radiusVector = new Vector3f(w/2, h/2, d/2);
			radius = radiusVector.length();
		}else{
			radius = (float)Math.sqrt(3);
		}
		loadModel();
	}
	
	public float getWidth(){
		return w;
	}
	
	public float getHeight(){
		return h;
	}
	
	public float getDepth(){
		return d;
	}
	
	public float getRadius(){
		return radius;
	}

}
