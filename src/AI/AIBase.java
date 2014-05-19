package AI;

import main.state.RenderState;
import math.Vector3f;
import entity.blueprint.AbstractMoveableEntity;
import entitymanager.EntityManager;

public class AIBase extends AbstractMoveableEntity {

	public AIBase(EntityManager world) {
		super(world);
	}
	
	public Vector3f enemyPosition() {
		return (Vector3f) getEntityManager().getState().getCamera().getTransform().updating().origin;
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
	
	public void stabilize(){
		this.getBody().clearForces();
	}
	
	
	
	public void action(){
		while (true) {
			if (this.enemyNotInSight()){
				try {
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
	}
	
}
