package game.world.gui;

import org.lwjgl.util.vector.Vector2f;

public interface Component {
	
	public void update();
	
	public void render();
	
	public void dispose();
	
	public void createVBO();
	
	//SET
	public void setId(int id);
	
	public void setMaster(Component m);
	
	public void setAngle(float a);
	
	//GET
	public int getId();
	
	public Component getMaster();
	
	public Vector2f getPosition();
	
	public float getAngle();

}
