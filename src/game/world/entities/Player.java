package game.world.entities;

public class Player extends Box{
	
	private float movementSpeed = 80; //Pixels per second

	public Player(float x, float y, float w, float h) {
		super(x, y, w, h);
	}
	
	public float getMovementSpeed(){
		return movementSpeed;
	}

}
