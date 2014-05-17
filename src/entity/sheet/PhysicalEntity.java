package entity.sheet;

import math.Matrix4f;
import math.Transform;
import math.Vector3f;
import resources.model.Model;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

public interface PhysicalEntity extends Entity{
	
	final float defaultInteria = 5f;
	final float defaultRestitution = .1f;
	final float defaultFriction = .95f;
	
	public RigidBody getBody();
	
	public void createBody(Model m);
	
	public void createBody(Model m, RigidBodyConstructionInfo rbci);
	
	public Vector3f getViewRay();
	
	public Vector3f getRightVector();
	
	public Vector3f getUpVector();
	
	public Matrix4f getBodyMatrix();
	
	public Transform getBodyTransform();

}
