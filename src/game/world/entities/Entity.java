package game.world.entities;

import org.lwjgl.util.vector.Vector3f;

import game.world.World;

public interface Entity {
	
	public void render();
	
	public void dispose();
	
	public void generateVBO();
	
	//SET
	public void setId(int id);
	
	public void setWorld(World world);
	
	//GET
	public int getId();
	
	public World getWorld();
	
	public int getVboID();
	
	public boolean isVBOGenerated();
	
	public Vector3f getPos();
	
	public EntityVariables getVariables(int i);

}
