package world.entity;

import input.InputListener;
import utils.math.Transform;
import utils.math.Vector3f;
import world.World;
import world.culling.Generalizable;
import world.sync.Linkable;

public interface WorldEntity extends Entity, Generalizable{
	
	public void setPosition(Vector3f v);
	
	public void setPosition(float x, float y, float z);
	
	public Vector3f getPosition();
	
	public Transform getTransform();
	
}
