package world.entity;

import javax.vecmath.Quat4f;

import resources.model.Model;
import state.RenderState;
import state.StateVariable;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import world.EntityManager;
import world.culling.BoundingAxis;
import world.culling.BoundingSphere;
import world.entity.create.ModeledEntity;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

public abstract class ModeledBodyEntity extends ModeledEntity implements PhysicalEntity{

	protected RigidBody body;
	
	private Transform previousTransform;
	
	

	
	public ModeledBodyEntity(EntityManager world){
		super(world);
		previousTransform = new Transform();
	}
	
	protected void addBodyToDynamicsWorld() {
		getEntityManager().getState().getDynamicsWorld().addRigidBody(getBody());
	}

	@Override
	public RigidBody getBody() {
		return body;
	}
	
	@Override
	public void update(float dt){
		if(body != null){
			if(getBody().isActive()){
				//interpoliate between previous and current using alpha
				getBody().getMotionState().getWorldTransform(getTransform(RenderState.getUpdatingId()));
				
				Vector3f min = new Vector3f();
				Vector3f max = new Vector3f();
				getBody().getAabb(min, max);
				getBoundingAxis().set(min, max);
				getBoundingSphere().pos = new Vector3f(getTransform(RenderState.getUpdatingId()).origin);
			}
		}
	}
	
	public void interpolate(float alpha){
		Transform previous = previousTransform;
		Transform current = getTransform(RenderState.getUpdatingId());
		
		Transform result = new Transform();
		result.set(current);
		
		//pos
		result.origin.interpolate(previous.origin, current.origin, alpha);

		//rot
		Quat4f prevRot = new Quat4f();
		previous.getRotation(prevRot);
		Quat4f curRot = new Quat4f();
		current.getRotation(curRot);
		prevRot.interpolate(curRot, alpha);
		
		result.setRotation(prevRot);

		getTransform(RenderState.getUpdatingId()).set(result);
	}
	
	public void storePreviousTransform(){
		getBody().getMotionState().getWorldTransform(previousTransform);
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
	
	public Transform getBodyTransform(){
		Transform t = new Transform();
		body.getWorldTransform(t);
		return t;
	}
	
	@Override
	public void setPosition(float x, float y, float z){
		if(body != null){
			Transform t = getBodyTransform();
			t.origin.set(x, y, z);
			getBody().setWorldTransform(t);
		}else
			super.setPosition(x, y, z);

	}
	
	
	@Override
	public BoundingAxis getBoundingAxis() {
		return boundingAxis.vars.get(RenderState.getUpdatingId());
	}
	
	@Override
	public BoundingSphere getBoundingSphere() {
		return boundingSphere.vars.get(RenderState.getUpdatingId());
	}


}
