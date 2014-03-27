package world.entity;

import world.World;
import world.sync.Linkable;

public interface Entity extends Linkable<Entity> {
	
	public void setID(int i);
	
	public int getID();
	
	public void setWorld(World world);
	
	public World getWorld();
	
	public void update(float dt);
	
	public boolean isInWorld();
	
}
