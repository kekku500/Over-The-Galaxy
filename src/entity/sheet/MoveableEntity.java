package entity.sheet;


public interface MoveableEntity extends Entity{
	
	public void accelerate(float amount);
	
	public void decelerate(float amount);
	
	public void moveRight(float amount);
	
	public void moveLeft(float amount);
	
	public void rotateUp(float amount);
	
	public void rotateDown(float amount);
	
	public void rotateRight(float amount);
	
	public void rotateLeft(float amount);
	
	public void activatePrimaryWeapon();
	
	public void activateSecondaryWeapon();
	
	public void rollLeft(float amount);
	
	public void rollRight(float amount);
	
}
