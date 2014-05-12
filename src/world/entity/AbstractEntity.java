package world.entity;

import javax.vecmath.Quat4f;

import state.Copyable;
import state.RenderState;
import state.State;
import state.StateVariable;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import world.EntityManager;
import world.culling.BoundingAxis;
import world.culling.BoundingSphere;

public abstract class AbstractEntity implements Entity {
	
	
	protected StateVariable<Transform> transform;
	
	private EntityManager entityManager;
	private Object userData;
	
	public AbstractEntity(EntityManager world){
		this.entityManager = world;
		
		
		
		Transform t = new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(0,0,0), 1));
		
		transform = new StateVariable<Transform>(t);
		
		world.addEntity(this);
	}


	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Object getUserData() {
		return userData;
	}

	@Override
	public void setUserData(Object o) {
		userData = o;
	}


	@Override
	public Transform getTransform(int id) {
		return transform.vars.get(id);
	}
	
	@Override
	public void setPosition(float x,float y,float z){
		for(Transform t: transform.vars)
			t.origin.set(x, y, z);
	}
	
	@Override
	public void setPosition(Vector3f v){
		setPosition(v.x, v.y, v.z);
	}
	
	@Override
	public Vector3f getPosition(int id){
		return new Vector3f(getTransform(id).origin);
	}
	
	public Matrix4f getTransformMatrix(int state){
		Matrix4f m4 = new Matrix4f();
		getTransform(state).getMatrix(m4);
		
		return m4;
	}


}
