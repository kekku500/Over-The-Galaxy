package game.world.entities;

import utils.math.Transform;
import utils.math.Vector3f;
import game.world.World;
import game.world.culling.Generalizable;
import game.world.sync.Synchronizable;

public interface Entity extends Generalizable, Synchronizable<Entity> {
	
	public void setPosition(Vector3f v);
	
	public void setPosition(float x, float y, float z);
	
	public Vector3f getPosition();
	
	public Transform getTransform();
	
	public void setID(int i);
	
	public int getID();
	
	public void setWorld(World world);
	
	public World getWorld();
	
	public void update(float dt);
	
	public boolean isInWorld();

	public void dispose();
	
	public void openGLInitialization();
}
