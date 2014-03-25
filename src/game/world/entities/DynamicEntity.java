package game.world.entities;

import game.world.culling.BoundingAxis;
import game.world.culling.BoundingSphere;
import utils.math.Vector3f;
import blender.model.Model;
import blender.model.ModelUtils;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;

public class DynamicEntity extends AbstractVisualPhysicalEntity {

	@Override
	public void createBody(Model m) {
		createBody(m, null);
		
	}
	
	@Override
	public Entity getLinked(){
		return new DynamicEntity().setLink(this);
	}

	@Override
	public void createBody(Model m, RigidBodyConstructionInfo rbci) {
		model = m;
		
		if(rbci == null){
			CollisionShape shape = ModelUtils.getConvexHull(m, scaleRotationMatrix);
			
			DefaultMotionState defaultMotionState = new DefaultMotionState(getTransform());
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
		boundingAxis = new BoundingAxis(min, max);
		
		//Bounding sphere
		float[] f = new float[1];
		Vector3f v = new Vector3f();
		rbci.collisionShape.getBoundingSphere(v, f);
		
		boundingSphere = new BoundingSphere(getPosition(), f[0]);
	}
	
	@Override
	public void setModel(Model m){}

}
