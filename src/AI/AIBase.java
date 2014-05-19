package AI;

import main.state.RenderState;
import math.Vector3f;
import entity.blueprint.AbstractMoveableEntity;
import entity.support.Thruster;
import entitymanager.EntityManager;

public class AIBase extends AbstractMoveableEntity {

	public AIBase(EntityManager world, float x, float y, float z) {
		super(world);
		setPosition(x, y, z);
	}
	
	private Thruster moveX = new Thruster(
			new Vector3f(0,0,0), new Vector3f(1,0,0), 1);
	
	private Thruster moveY = new Thruster(
			new Vector3f(0,0,0), new Vector3f(0,1,0), 1);
	
	private Thruster moveZ = new Thruster(
			new Vector3f(0,0,0), new Vector3f(0,0,1), 1);
	
	public void moveX(float amount) {
		moveX.setPower(amount);
		moveX.apply(this);
	}
	
	public void moveY(float amount) {
		moveY.setPower(amount);
		moveY.apply(this);
	}
	
	public void moveZ(float amount) {
		moveZ.setPower(amount);
		moveZ.apply(this);
	}
	
	public Vector3f enemyPosition() {
		System.out.println(RenderState.updating());
		return (Vector3f) getEntityManager().getState().getPlayer().getPosition(RenderState.updating());
	}
	
	public Vector3f enemyDistance() {
		return getPosition(RenderState.updating()).sub(this.enemyPosition()).abs();
	}
	
	public boolean enemyNotInSight(){
		if (enemyDistance().x < 1000 || enemyDistance().y < 1000 || enemyDistance().z < 1000) {
			return false;
		}
		return true;
	}
	
	public Vector3f getVector() {
		return getPosition(RenderState.updating()).sub(this.enemyPosition());
	}
	
	public void stabilize(){
		this.getBody().clearForces();
	}

	public void kamikaze() {
		this.moveX(getVector().x % 100);
		this.moveY(getVector().y % 100);
		this.moveZ(getVector().z % 100);
	}
	
	public void action(){
		while (true) {
			if (this.enemyNotInSight()){
				try {
					System.out.println("Kedagi pole");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				this.combatAction();
				this.stabilize();
			}
		}
	}
	
	public void combatAction(){	
		while (enemyNotInSight() == false) {
			System.out.println("Leidsin!!!");
			kamikaze();
		}
	}
	
}