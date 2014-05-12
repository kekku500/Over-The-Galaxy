package world.entity.create;

import resources.model.Model;
import resources.model.ModelUtils;
import state.RenderState;
import state.State;
import utils.math.Transform;
import utils.math.Vector3f;
import world.EntityManager;
import world.culling.BoundingAxis;
import world.culling.BoundingSphere;
import world.entity.Entity;
import world.entity.ModeledBodyEntity;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;

public class DynamicEntity extends ModeledBodyEntity {
	


	public DynamicEntity(EntityManager world) {
		super(world);
	}

	@Override
	public void createBody(Model m) {
		createBody(m, null);
		
	}

	@Override
	public void createBody(Model m, RigidBodyConstructionInfo rbci) {
		model = m;
		
		if(rbci == null){
			CollisionShape shape = ModelUtils.getConvexHull(m, scaleRotationMatrix);
			
			DefaultMotionState defaultMotionState = new DefaultMotionState(getTransform(RenderState.getUpdatingId()));
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
		
		super.addBodyToDynamicsWorld();
	}




}
