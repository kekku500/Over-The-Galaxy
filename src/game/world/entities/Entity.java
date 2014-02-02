package game.world.entities;

import game.world.World;

import java.nio.FloatBuffer;

import math.BoundingAxis;
import math.BoundingSphere;
import math.Vector3fc;

import org.lwjgl.util.vector.Vector3f;

public interface Entity {
	
	public enum Motion {
		STATIC, DYNAMIC, PHYSICS;
	}
	
	public void update(float dt);
	
	public void render();
	
	public void dispose();
	
	public void createVBO();
	
	public void addRoll(float r);
	
	public void addPitch(float p);
	
	public void addYaw(float y);
	
	public void setMotion(Motion m);
	
	public Motion getMotion();
	
	public void setVBOVertexId(int i);
	
	public int getVBOVertexId();
	
	public void setPos(Vector3fc v);
	
	public Vector3fc getPos();
	
	public void setVisible(boolean b);
	
	public boolean isVisible();
	
	public void setId(int id);
	
	public int getId();
	
	public void setWorld(World world);
	
	public World getWorld();
	
	public void setPitch(float i);
	
	public float getPitch();
	
	public void setYaw(float i);
	
	public float getYaw();

	public void setRoll(float i);
	
	public float getRoll();
	
	public Vector3fc getPosToMid();

	public Vector3fc getToMidPoint();
	
	public BoundingSphere getBoundingSphere();
	
	public BoundingAxis getBoundingAxis();
	
	public boolean isSleeping();

	public Entity copy();

}
