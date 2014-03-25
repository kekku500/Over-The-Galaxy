package game.world.entities;

import blender.model.Model;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

public interface PhysicalEntity extends Entity{
	
	final float defaultInteria = 5f;
	final float defaultRestitution = .1f;
	final float defaultFriction = .95f;
	
	public RigidBody getBody();
	
	public boolean isPhysical();
	
	public void createBody(Model m);
	
	public void createBody(Model m, RigidBodyConstructionInfo rbci);

}