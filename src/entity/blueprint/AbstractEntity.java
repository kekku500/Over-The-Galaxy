package entity.blueprint;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Quat4f;

import main.state.RenderState;
import main.state.StateVariable;
import math.Matrix4f;
import math.Transform;
import math.Vector3f;
import entity.sheet.Entity;
import entitymanager.EntityManager;

public abstract class AbstractEntity implements Entity {
	
	protected Set<Entity> children = new HashSet<Entity>();
	
	protected Entity parent;
	
	private static final long serialVersionUID = 1L;
	private short priority;
	protected transient StateVariable<Transform> transform;
	
	public Matrix4f serializableMatrix;
	
	private transient EntityManager entityManager;
	private Object userData;
	
	protected Entity following;
	
	public AbstractEntity(EntityManager world){
		this.entityManager = world;
		
		Transform t = new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(0,0,0), 1));
		
		transform = new StateVariable<Transform>(t);
		if(world != null)
			world.addEntity(this);
	}
	
	@Override
	public Set<Entity> getChildren(){
		return children;
	}
	
	@Override
	public void addChild(Entity child){
		//configure child's (e) transform to parent transform
		child.setStateTransform(getTransform());
		child.setParent(this);
		children.add(child);
	}
	
	@Override
	public void setParent(Entity p){
		parent =  p;
	}
	
	@Override
	public Entity getParent(){
		return parent;
	}
	
	@Override
	public void requestRemoval(){
		for(Entity child: getChildren()){
			child.requestRemoval();
		}
		getEntityManager().removeEntity(this);
	}
	
	
	@Override
	public void setStateTransform(StateVariable<Transform> v){
		transform = v;
	}
	
	@Override
	public void save(){
		serializableMatrix = getTransformMatrix(RenderState.updating());
	}
	
	@Override
	public void load(){
		Transform t = new Transform(serializableMatrix);
		
		transform = new StateVariable<Transform>(t);
	}
	
	
	
	@Override
	public void setUpdatePriority(short priority){
		this.priority = priority;
	}
	
	@Override
	public short getUpdatePriority(){
		return priority;
	}
	
	@Override
	public int compareTo(Entity e){
		return (e.getUpdatePriority() > getUpdatePriority() ? 1 : (e.getUpdatePriority() == getUpdatePriority() ? 0 : -1));
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Override
	public void setEntityManager(EntityManager manager){
		entityManager = manager;
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
	public StateVariable<Transform> getTransform() {
		return transform;
	}
	
	@Override
	public Transform getTransform(int state){
		return transform.vars.get(state);
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
	public Vector3f getPosition(int state){
		return new Vector3f(getTransform().vars.get(state).origin);
	}
	
	public Matrix4f getTransformMatrix(int state){
		Matrix4f m4 = new Matrix4f();
		getTransform().vars.get(state).getMatrix(m4);
		
		return m4;
	}


}
