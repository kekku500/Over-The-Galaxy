package game.world;

import game.Game;
import game.world.entities.Entity;
import math.BoundingAxis;
import math.BoundingSphere;
import math.Vector3fc;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import controller.Camera;

public class FrustumCulling {
	
	public enum Frustum{
		OUTSIDE, INTERSECT, INSIDE;
	}
	
	private Camera camera;
	private Vector3fc X, Y, Z, camPos;
	
	private float tang;
	private float ratio;
	private float sphereFactorY;
	private float sphereFactorX;
	private float Hfar, Wfar;
	
	public FrustumCulling(Camera camera){
		this.camera = camera;
		
		ratio = (float)Game.width/Game.height;
		
		float angle = (float) Math.toRadians(Game.fov/2);
		tang = (float) Math.tan(angle);
		
		sphereFactorY = 1/(float)Math.cos(angle);
		
		float anglex = (float) Math.atan(tang*ratio);
		sphereFactorX = 1/(float)Math.cos(anglex);

		Hfar = 2 * tang * Game.zFar;
		Wfar = Hfar * ratio;
	}
	
	public void setCamera(Camera c){
		camera = c;
	}
	
	public void update(){
		Z = camera.getViewRay();
		X = camera.getRightVector();
		Y =  camera.getUpVector();
		camPos = camera.getPos();
	}
	
	//http://www.lighthouse3d.com/tutorials/view-frustum-culling/radar-approach-implementation-ii/
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
			return Frustum.INSIDE; //Just assume object is in view
		
		Vector3f vertex = sphere.pos; //Sphere midpoint
		float radius = sphere.radius;
		
		float d;
		float az, ax, ay;
		Frustum result = Frustum.INSIDE; //Assume sphere is in the view
		
		Vector3f v = new Vector3f(vertex.x-camPos.x, vertex.y-camPos.y, vertex.z-camPos.z); //Vector that points from pos to vertex
		
		//Compute and test the Z coordinate
		az = Vector3f.dot(v, Z);
		if(az > Game.zFar+radius || az < Game.zNear-radius){
			return Frustum.OUTSIDE; 
		}
		if(az > Game.zFar - radius || az < Game.zNear+radius)
			result = Frustum.INTERSECT;
		
		//Compute and test the Y coordinate
		ay = Vector3f.dot(v, Y);
		d = radius * sphereFactorY;
		az *= tang; //Frustum far plane height
		if(ay > az+d || ay < -az-d){
			return Frustum.OUTSIDE; 
		}
		if(ay > az-d || ay < -az+d)
			result = Frustum.INTERSECT;
		
		//Compute and test the X coordinate
		ax = Vector3f.dot(v, X);
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

		Vector3fc[] fb = getFrustumBox();
		Vector3fc fmin = fb[0];
		Vector3fc fmax = fb[1];
		
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

	public Vector3fc[] getFrustumBox(){
		Vector3fc fc = camPos.getAdd(Z.getMultiply(Game.zFar)); //temp variable
		Vector3fc ftl = fc.getAdd(Y.getMultiply(Hfar/2f)).getAdd(X.getMultiply(Wfar/2f).getNegate()); //frustum to left corner
		Vector3fc fbr = fc.getAdd(Y.getMultiply(Hfar/2f).getNegate()).getAdd(X.getMultiply(Wfar/2f)); //frustum bottom right corner
		
		return Vector3fc.getMinMaxVectors(ftl, fbr, camPos);
	}

}
