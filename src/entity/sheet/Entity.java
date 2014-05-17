package entity.sheet;

import java.io.Serializable;
import java.util.Set;

import main.state.StateVariable;
import math.Transform;
import math.Vector3f;
import entitymanager.EntityManager;

public interface Entity extends Comparable<Entity>, Serializable{
	
	public Set<Entity> getChildren();
	
	public Entity getParent();
	
	public void setParent(Entity parent);
	
	public void addChild(Entity e);
	
	public Transform getTransform(int state);
	
	public StateVariable<Transform> getTransform();
	
	public void setPosition(float x, float y, float z);
	
	public void setPosition(Vector3f v);
	
	public Vector3f getPosition(int id);
	
	/**
	 * Prepares entity for serialization
	 */
	public void save();
	
	/**
	 * Restores entity proper state using preparations done in save() method
	 */
	public void load();
	
	/**
	 * This method is called in the update thread
	 * @param dt - time passed since the method was last called
	 */
	public void update(float dt);
	
	/**
	 * This method is called in the render thread
	 */
	public void render();
	
	/**
	 * World where object is
	 * @return
	 */
	public EntityManager getEntityManager();
	
	public void setEntityManager(EntityManager manager);
	
	public Object getUserData();
	
	public void setUserData(Object o);
	
	public void setStateTransform(StateVariable<Transform> v);
	
	/**
	 * Update method calling loop is sorted by priority. Higher priority means update method will be
	 * called before others. Lower priority corresponds to being updated later/last.
	 * @param priority
	 */
	public short getUpdatePriority();
	
	/**
	 * Update method calling loop is sorted by priority. Higher priority means update method will be
	 * called before others. Lower priority corresponds to being updated later/last.
	 * @param priority
	 */
	public void setUpdatePriority(short priority);
	
	public void requestRemoval();
	
	
}
