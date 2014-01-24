package game.world.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * This is temporary.
 */

public class Player extends Box{
	
	private float movementSpeed = 20; //Pixels per second

	public Player(float x, float y, float z) {
		super(new Vector3f(x,y,z), 10, 20, 10);
	}
	
	public float getMovementSpeed(){
		return movementSpeed;
	}

}
