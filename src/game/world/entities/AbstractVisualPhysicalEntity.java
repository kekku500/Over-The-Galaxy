package game.world.entities;

import game.world.culling.BoundingAxis;
import game.world.culling.BoundingSphere;
import utils.math.Vector3f;
import blender.model.Model;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;

public abstract class AbstractVisualPhysicalEntity extends AbstractVisualEntity implements PhysicalEntity{

	protected RigidBody body;
	
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof PhysicalEntity){
			PhysicalEntity ve = (PhysicalEntity)t;
			
			body = ve.getBody();
		}

		return this;
	}

	@Override
	public RigidBody getBody() {
		return body;
	}

	@Override
	public boolean isPhysical() {
		if(body == null)
			return false;
		return true;
	}
	
	@Override
	public void update(float dt){
		if(isPhysical()){
			if(getBody().isActive()){
				getBody().getMotionState().getWorldTransform(getTransform());
				Vector3f min = new Vector3f();
				Vector3f max = new Vector3f();
				getBody().getAabb(min, max);
				boundingAxis = new BoundingAxis(min, max);
				boundingSphere.pos = getPosition();
			}
		}
	}
	
	@Override
	public void setPosition(float x, float y, float z){
		if(isPhysical() && !isInWorld()){
			Transform t = new Transform();
			getBody().getWorldTransform(t);
			t.origin.set(x,y,z);
			getBody().setWorldTransform(t);
		}
		positionRotation.origin.set(x, y, z);
	}
	
	
	@Override
	public BoundingAxis getBoundingAxis(){
		return boundingAxis;
	}
	
	
	@Override
	public BoundingSphere getBoundingSphere(){
		return boundingSphere;
	}

}
