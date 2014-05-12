package world.gui;

import org.lwjgl.util.vector.Vector2f;

public interface Component{
	
	public void setHUDManager(HUDManager manager);
	
	public HUDManager getHUDManager();
	
	public void render();
	
	public void setPosition(float x, float y);
	
	public void setPosition(Vector2f v);
	
	public Vector2f getPosition();
	
	public void update(float dt);


}
