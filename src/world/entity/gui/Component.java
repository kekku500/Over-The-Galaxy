package world.entity.gui;

import org.lwjgl.util.vector.Vector2f;

import world.entity.Entity;

public interface Component extends Entity{
	
	public void render();
	
	public void dispose();
	
	public void setPosition(float x, float y);
	
	public void setPosition(Vector2f v);
	
	public Vector2f getPosition();


}
