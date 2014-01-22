package game.world.entities;

import game.world.World;

public interface Entity {
	
	public void render();
	
	public Entity getCopy();
	
	public void dispose();
	
	public int getVboID();
	
	
	public int getId();
	
	public void setId(int id);
	
	public World getWorld();
	
	public void setWorld(World world);
	
	public EntityVariables getVariables(int i);
	
	public void setX(float x);
	
	public void setY(float y);
	
	public void generateVBO();

}
