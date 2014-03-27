package world.entity.gui;

import org.lwjgl.util.vector.Vector2f;

import world.entity.Entity;

public interface Component extends Entity{
	
	public void render();
	
	public void dispose();
	
	public void renderInit();
	
	//SET
	
	public void setMaster(Component m);
	
	public void setAngle(float a);
	
	//GET
	
	public Component getMaster();
	
	public Vector2f getPosition();
	
	public float getAngle();

}
