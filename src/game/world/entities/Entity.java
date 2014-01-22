package game.world.entities;

import game.world.World;

public interface Entity {
	
	public void render();
	
	public void dispose();
	
	public void generateVBO();
	
	public void setWorld(World world);
	
	public void setId(int id);
	
	public World getWorld();
	
	public int getId();
	
	public int getVboID();
	
	public EntityVariables getVariables(int i);
	
	public float getWidth();
	
	public float getHeight();

}
