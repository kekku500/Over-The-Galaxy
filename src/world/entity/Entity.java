package world.entity;

import utils.math.Transform;
import utils.math.Vector3f;
import world.EntityManager;

public interface Entity{
	
	public Transform getTransform(int id);
	
	public void setPosition(float x, float y, float z);
	
	public void setPosition(Vector3f v);
	
	public Vector3f getPosition(int id);
	
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
	
	public Object getUserData();
	
	public void setUserData(Object o);
	
}
