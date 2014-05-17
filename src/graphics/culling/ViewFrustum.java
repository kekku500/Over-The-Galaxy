package graphics.culling;

import main.state.Copyable;
import math.Vector3f;
import utils.ArrayList;
import utils.Utils;
import entity.sheet.VisualEntity;

public class ViewFrustum implements Generalizable, Copyable<ViewFrustum>{
	
	public enum Frustum{
		OUTSIDE, INTERSECT, INSIDE;
	}
	
	public int id = 0;
	
	private float aspect, fov;
	private float zNear, zFar;
	private Vector3f X, Y, Z, pos;
	
	//Derived variables
	private float tang, sphereFactorY, sphereFactorX, Hfar, Wfar;
	
	private ArrayList<VisualEntity> insideFrustum = new ArrayList<VisualEntity>();
	
	private boolean failIntersectCheck = false;
	
	@Override
	public ViewFrustum copy() {
		ViewFrustum copy = new ViewFrustum();
		copy.setProjection(fov, aspect, zNear, zFar);
		return copy;
	}
	
	public void setIntersectCheckFail(boolean b){
		failIntersectCheck = b;
	}
	
	public void cullEntities(ArrayList<VisualEntity> set){
		insideFrustum.clear();
		for(VisualEntity e: set){

			Frustum test = inView(e);
			if(test != Frustum.OUTSIDE){
				insideFrustum.add(e);
			}
		}
	}
	 
	public ArrayList<VisualEntity> getInsideFrustumEntities(){
		return insideFrustum;
	}
	
	public void setProjection(float fov, float aspect, float zNear, float zFar){
		this.fov = fov;this.aspect = aspect;this.zNear = zNear;this.zFar = zFar;
		
		float angle = (float) Math.toRadians(this.fov/2);
		tang = (float) Math.tan(angle);
		
		sphereFactorY = 1/(float)Math.cos(angle);
		
		float anglex = (float) Math.atan(tang*aspect);
		sphereFactorX = 1/(float)Math.cos(anglex);

		Hfar = 2 * tang * zFar;
		Wfar = Hfar * aspect;
	}
	
	public void setView(Vector3f viewRay, Vector3f rightVector, Vector3f upVector){
		Z = viewRay; X = rightVector; Y = upVector;
	}
	
	public void setPos(Vector3f pos){
		this.pos = pos;
	}

	
	public Frustum inView(VisualEntity e){
		/*boolean check = false;
		if(e.getTag() == 1){
			check = true;
		}*/
		Frustum sphereResult = sphereLocation(e.getBoundingSphere());
		//System.out.println("sphere: " + sphereResult);
		/*if(check)
			System.out.println("SPEHRE " + sphereResult);*/
		if(sphereResult == Frustum.INTERSECT){ //Sphere is touching Frustum
			if(failIntersectCheck){
				return Frustum.OUTSIDE;
			}
			Frustum boxResult = boxLocation(e.getBoundingAxis());
			//System.out.println("box: " + boxResult);
			/*if(check)
				System.out.println("BOX " + boxResult);*/
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
		if(az > zFar+radius || az < zNear-radius){
			return Frustum.OUTSIDE; 
		}
		if(az > zFar - radius || az < zNear+radius)
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
		az *= aspect; //Frustum far plane width
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
		Vector3f fc = pos.copy().add(Z.copy().scl(zFar)); //temp variable
		//System.out.println("Front center is " + fc + " view ray " + Z);
		Vector3f ftl = fc.copy().add(Y.copy().scl(Hfar/2f)).add(X.copy().scl(Wfar/2f).negater()); //frustum to left corner
		Vector3f fbr = fc.copy().add(Y.copy().scl(Hfar/2f).negater()).add(X.copy().scl(Wfar/2f)); //frustum bottom right corner
		
		Vector3f ftr = fc.copy().add(Y.copy().scl(Hfar/2f)).add(X.copy().scl(Wfar/2f)); //frustum to left corner
		Vector3f fbl = fc.copy().add(Y.copy().scl(Hfar/2f).negater()).add(X.copy().scl(Wfar/2f).negater()); //frustum bottom right corner
		
		return Utils.getMinMaxVectors(ftl, fbr, pos, ftr, fbl);

	}

	@Override
	public BoundingAxis getBoundingAxis() {
		Vector3f[] fb = getFrustumBox();
		Vector3f fmin = fb[0];
		Vector3f fmax = fb[1];
		
		return new BoundingAxis(fmin, fmax);
	}

	@Override
	public BoundingSphere getBoundingSphere() {
		// TODO Auto-generated method stub
		return null;
	}


}
