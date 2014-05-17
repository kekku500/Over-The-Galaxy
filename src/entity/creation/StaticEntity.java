package entity.creation;

import math.Vector3f;
import resources.model.Model;
import resources.model.ModelUtils;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;

import entity.blueprint.AbstractVisualPhysicsEntity;
import entitymanager.EntityManager;
import graphics.culling.BoundingAxis;
import graphics.culling.BoundingSphere;

public class StaticEntity extends AbstractVisualPhysicsEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StaticEntity(EntityManager world) {
		super(world);
	}

	@Override
	public void createBody(Model m) {
		createBody(m, null);
		
	}
	
	@Override
	public void update(float dt){}

	@Override
	public void createBody(Model m, RigidBodyConstructionInfo rbci) {
		setModel(m);
		
		if(rbci == null){
			CollisionShape shape = ModelUtils.getStaticCollisionShape(m, scaleRotationMatrix);
			
			DefaultMotionState defaultMotionState = new DefaultMotionState(getTransform().updating());
			Vector3f intertia = new Vector3f();
			shape.calculateLocalInertia(defaultInteria,  intertia);
			rbci = new RigidBodyConstructionInfo(5f, defaultMotionState, shape, intertia);
			rbci.restitution = defaultRestitution;
			rbci.friction = defaultFriction;
		}
		
		body = new RigidBody(rbci);
		
		getBody().setMassProps(0f, new Vector3f(0,0,0)); //remove mass
		getBody().updateInertiaTensor();
		getBody().setLinearVelocity(new Vector3f(0,0,0)); //zero velocity
		getBody().setAngularVelocity(new Vector3f(0,0,0)); //zero angular velocity
		
		
		//Bounding aabb
		Vector3f min = new Vector3f();
		Vector3f max = new Vector3f();
		getBody().getAabb(min, max);
		for(BoundingAxis ba: boundingAxis.vars)
			ba.set(min, max);
		
		//Bounding sphere
		float[] f = new float[1];
		Vector3f v = new Vector3f();
		rbci.collisionShape.getBoundingSphere(v, f);
		int i = 0;
		for(BoundingSphere bs: boundingSphere.vars){
			bs.pos = getPosition(i);
			bs.radius = f[0];
			i++;
		}
	
		//super.addBodyToDynamicsWorld();
	}

}
