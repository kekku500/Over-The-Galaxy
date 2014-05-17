package entity.creation;

import math.Vector3f;
import resources.model.Model;
import resources.model.ModelUtils;

import com.bulletphysics.collision.shapes.BvhSubtreeInfo;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;

import entity.blueprint.AbstractVisualPhysicsEntity;
import entitymanager.EntityManager;
import graphics.culling.BoundingAxis;
import graphics.culling.BoundingSphere;

public class DynamicEntity extends AbstractVisualPhysicsEntity {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Vector3f angularVelocity = new Vector3f();
	private Vector3f linearVelocity = new Vector3f();
	private float mass;
	//private 

	public DynamicEntity(EntityManager world) {
		super(world);
	}

	@Override
	public void createBody(Model m) {
		createBody(m, null);
		
	}
	
	@Override
	public void save(){
		super.save();
		body.getAngularVelocity(angularVelocity);
		body.getLinearVelocity(linearVelocity);
	//	body.
		
		
	}

	@Override
	public void createBody(Model m, RigidBodyConstructionInfo rbci) {
		setModel(m);
		
		if(rbci == null){
			CollisionShape shape = ModelUtils.getConvexHull(m, scaleRotationMatrix);
			
			DefaultMotionState defaultMotionState = new DefaultMotionState(getTransform().updating());
			Vector3f intertia = new Vector3f();
			shape.calculateLocalInertia(defaultInteria,  intertia);
			rbci = new RigidBodyConstructionInfo(5f, defaultMotionState, shape, intertia);
			rbci.restitution = defaultRestitution;
			rbci.friction = defaultFriction;
		}
		
		body = new RigidBody(rbci);
		
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
		
		for(BoundingSphere bs: boundingSphere.vars){
			bs.radius = f[0];
		}
		
		

		
		body.getMotionState().setWorldTransform(getTransform().updating());
		body.setLinearVelocity(linearVelocity);
		body.setAngularVelocity(angularVelocity);
		
		
		//super.addBodyToDynamicsWorld();
	}




}
