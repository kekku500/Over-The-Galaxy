package game.world.entities;

import main.Main;

public class Player extends Box{
	
	private float movementSpeed = 80; //Pixels per second
	
	private boolean moveRight, moveLeft, moveUp, moveDown;

	public Player(float x, float y, float w, float h) {
		super(x, y, w, h);
	}
	
	public void requestMoveRight(){
		moveRight = true;
	}
	
	public boolean mustMoveRight(){
		if(moveRight){
			moveRight = false;
			return true;
		}
		return false;
	}
	
	public void requestMoveLeft(){
		moveLeft = true;
	}
	
	public boolean mustMoveLeft(){
		if(moveLeft){
			moveLeft = false;
			return true;
		}
		return false;
	}
	
	public void requestMoveDown(){
		moveDown = true;
	}
	
	public boolean mustMoveDown(){
		if(moveDown){
			moveDown = false;
			return true;
		}
		return false;
	}
	
	public void requestMoveUp(){
		moveUp = true;
	}
	
	public boolean mustMoveUp(){
		if(moveUp){
			moveUp = false;
			return true;
		}
		return false;
	}
	
	public float getMovementSpeed(){
		return movementSpeed;
	}

}
