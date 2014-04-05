package world.entity;

import resources.model.Model;
import utils.math.Matrix4f;
import utils.math.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

public interface PhysicalEntity extends WorldEntity{
	
	final float defaultInteria = 5f;
	final float defaultRestitution = .1f;
	final float defaultFriction = .95f;
	
	public RigidBody getBody();
	
	public boolean isPhysical();
	
	public void createBody(Model m);
	
	public void createBody(Model m, RigidBodyConstructionInfo rbci);
	
	public Vector3f getViewRay();
	
	public Vector3f getRightVector();
	
	public Vector3f getUpVector();
	
	public Matrix4f getBodyMatrix();

}
