package world.entity.smart;

import utils.math.Vector3f;
import world.EntityManager;
import world.entity.create.DynamicEntity;

public abstract class AbstractMoveableEntity extends DynamicEntity implements MoveableEntity {
	
	public AbstractMoveableEntity(EntityManager world) {
		super(world);
	}

	//accel, decel
	private Thruster accelThruster = new Thruster(
			new Vector3f(0,0,-1), new Vector3f(0,0,1), 1);
	//right, left
	private Thruster moveRightThruster = new Thruster(
			new Vector3f(0,0,0), new Vector3f(-1,0,0), 1);
	
	//right, left rotation
	private Thruster rotateThrusterBack = new Thruster(
			new Vector3f(0,0,-1), new Vector3f(1,0,0), 1);
	private Thruster rotateThrusterFront = new Thruster(
			new Vector3f(0,0,1), new Vector3f(-1,0,0), 1);
	
	//up, down rotation
	private Thruster rotateThrusterBackDown = new Thruster(
			new Vector3f(0,0,-1), new Vector3f(0,-1,0), 1);
	private Thruster rotateThrusterFrontUp = new Thruster(
			new Vector3f(0,0,1), new Vector3f(0,1,0), 1);
	
	@Override
	public void accelerate(float amount) {
		accelThruster.setPower(amount);
		accelThruster.apply(this);
	}

	@Override
	public void decelerate(float amount) {
		accelerate(-amount);
	}

	@Override
	public void moveRight(float amount) {
		moveRightThruster.setPower(amount);
		moveRightThruster.apply(this);
	}

	@Override
	public void moveLeft(float amount) {
		moveRight(-amount);
	}

	@Override
	public void rotateUp(float amount) {
		rotateThrusterBackDown.setPower(amount);
		rotateThrusterBackDown.apply(this);
		rotateThrusterFrontUp.setPower(amount);
		rotateThrusterFrontUp.apply(this);
	}

	@Override
	public void rotateDown(float amount) {
		rotateUp(-amount);
	}

	@Override
	public void rotateRight(float amount) {
		rotateThrusterBack.setPower(amount);
		rotateThrusterBack.apply(this);
		rotateThrusterFront.setPower(amount);
		rotateThrusterFront.apply(this);
	}

	@Override
	public void rotateLeft(float amount) {
		rotateRight(-amount);
		
	}

	@Override
	public void activatePrimaryWeapon() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activateSecondaryWeapon() {
		// TODO Auto-generated method stub
		
	}

}
