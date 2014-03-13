package game.world;

import game.Game;
import game.world.entities.Entity;
import utils.BoundingAxis;
import utils.BoundingSphere;
import utils.Utils;
import utils.math.Vector3f;

public class ViewFrustum {
	
	public enum Frustum{
		OUTSIDE, INTERSECT, INSIDE;
	}
	
	private int width, height, fov;
	private float zNear, zFar;
	private Vector3f X, Y, Z, pos;
	
	//Derived variables
	private float tang, ratio, sphereFactorY, sphereFactorX, Hfar, Wfar;
	
	public void setProjection(int fov, int width, int height, float zNear, float zFar){
		this.fov = fov;this.width = width;this.height = height;this.zNear = zNear;this.zFar = zFar;
		
		ratio = (float)width/height;
		
		float angle = (float) Math.toRadians(fov/2);
		tang = (float) Math.tan(angle);
		
		sphereFactorY = 1/(float)Math.cos(angle);
		
		float anglex = (float) Math.atan(tang*ratio);
		sphereFactorX = 1/(float)Math.cos(anglex);

		Hfar = 2 * tang * Game.zFar;
		Wfar = Hfar * ratio;
	}
	
	public void setView(Vector3f viewRay, Vector3f rightVector, Vector3f upVector){
		Z = viewRay; X = rightVector; Y = upVector;
	}
	
	public void setPos(Vector3f pos){
		this.pos = pos;
	}

	
	public Frustum inView(Entity e){
		Frustum sphereResult = sphereLocation(e.getBoundingSphere());
		if(sphereResult == Frustum.INTERSECT){ //Sphere is touching Frustum
			Frustum boxResult = boxLocation(e.getBoundingAxis());
			return boxResult; //box can be inside, intersect or outside
		}else{ //sphere is either outside or inside

			return sphereResult; 
		}
	}
	
	private Frustum sphereLocation(BoundingSphere sphere){
		if(sphere == null) //No sphere found
			return Frustum.INTERSECT; //Just assume object is in view
		
		Vector3f vertex = sphere.pos; //Sphere midpoint
		float radius = sphere.radius;
		
		float d;
		float az, ax, ay;
		Frustum result = Frustum.INSIDE; //Assume sphere is in the view
		
		Vector3f v = new Vector3f(vertex.x-pos.x, vertex.y-pos.y, vertex.z-pos.z); //Vector that points from pos to vertex
		
		//Compute and test the Z coordinate
		az = v.dot(Z);
		if(az > Game.zFar+radius || az < Game.zNear-radius){
			return Frustum.OUTSIDE; 
		}
		if(az > Game.zFar - radius || az < Game.zNear+radius)
			result = Frustum.INTERSECT;
		
		//Compute and test the Y coordinate
		ay =  v.dot(Y);
		d = radius * sphereFactorY;
		az *= tang; //Frustum far plane height
		if(ay > az+d || ay < -az-d){
			return Frustum.OUTSIDE; 
		}
		if(ay > az-d || ay < -az+d)
			result = Frustum.INTERSECT;
		
		//Compute and test the X coordinate
		ax =  v.dot(X);
		az *= ratio; //Frustum far plane width
		d = radius * sphereFactorX;
		if(ax > az+d || ax < -az-d){
			return Frustum.OUTSIDE; 
		}
		if(ax > az-d || ax < -az+d)
			result = Frustum.INTERSECT;
		
		return result;
	}
	
	public Frustum boxLocation(BoundingAxis ob){
		if(ob == null)
			return Frustum.INSIDE;

		Vector3f[] fb = getFrustumBox();
		Vector3f fmin = fb[0];
		Vector3f fmax = fb[1];
		
		if(fmin.x > ob.getMax().x || fmax.x < ob.getMin().x ||
				fmin.y > ob.getMax().y || fmax.y < ob.getMin().y ||
				fmin.z > ob.getMax().z || fmax.z < ob.getMin().z)
			return Frustum.OUTSIDE;
		else if(fmin.x < ob.getMin().x && fmax.x > ob.getMax().x &&
				fmin.y < ob.getMin().y && fmax.y > ob.getMax().y &&
				fmin.z < ob.getMin().z && fmax.z > ob.getMax().z)
			return Frustum.INSIDE;
		else{
			return Frustum.INTERSECT;
		}
	}

	public Vector3f[] getFrustumBox(){
		Vector3f fc = pos.copy().add(Z.copy().mul(Game.zFar)); //temp variable
		//System.out.println("Front center is " + fc + " view ray " + Z);
		Vector3f ftl = fc.copy().add(Y.copy().mul(Hfar/2f)).add(X.copy().mul(Wfar/2f).getNegate()); //frustum to left corner
		Vector3f fbr = fc.copy().add(Y.copy().mul(Hfar/2f).getNegate()).add(X.copy().mul(Wfar/2f)); //frustum bottom right corner
		
		Vector3f ftr = fc.copy().add(Y.copy().mul(Hfar/2f)).add(X.copy().mul(Wfar/2f)); //frustum to left corner
		Vector3f fbl = fc.copy().add(Y.copy().mul(Hfar/2f).getNegate()).add(X.copy().mul(Wfar/2f).getNegate()); //frustum bottom right corner
		
		return Utils.getMinMaxVectors(ftl, fbr, pos, ftr, fbl);

	}
}
