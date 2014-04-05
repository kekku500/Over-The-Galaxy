package world.entity;

import resources.model.Model;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import world.culling.BoundingAxis;
import world.culling.BoundingSphere;

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

	@Override
	public Vector3f getViewRay() {
		Matrix4f viewMatrix = getBodyMatrix();

		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		
		return new Vector3f(ray[0], ray[1], ray[2]);
	}

	@Override
	public Vector3f getRightVector() {
		Matrix4f viewMatrix = getBodyMatrix();

		float[] right = new float[4];
		viewMatrix.getRow(0, right);
		
		return new Vector3f(right[0], right[1], right[2]);
	}

	@Override
	public Vector3f getUpVector() {
		Matrix4f viewMatrix = getBodyMatrix();

		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		
		return new Vector3f(up[0], up[1], up[2]);
	}
	
	@Override
	public Matrix4f getBodyMatrix(){
		Transform bodyTransform = new Transform();
		getBody().getWorldTransform(bodyTransform);
		Matrix4f viewMatrix = new Matrix4f();
		bodyTransform.getMatrix(viewMatrix);
		viewMatrix.invert();
		return viewMatrix;
	}

}
