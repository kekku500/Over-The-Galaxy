package world.entity.smart;

import utils.math.Matrix4f;
import utils.math.Vector3f;
import world.entity.PhysicalEntity;

public class Thruster {
	
	private Vector3f applyPoint;
	private Vector3f direction;
	private float power;
	
	public Thruster(){}
	
	public Thruster(Vector3f applyPoint, Vector3f direction, float power) {
		super();
		this.applyPoint = applyPoint;
		this.direction = direction;
		this.power = power;
	}
	
	public Vector3f getApplyPoint() {
		return applyPoint;
	}
	
	public void setApplyPoint(Vector3f applyPoint) {
		this.applyPoint = applyPoint;
	}
	
	public Vector3f getDirection() {
		return direction;
	}
	
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}
	
	public float getPower() {
		return power;
	}
	
	public void setPower(float power) {
		this.power = power;
	}
	
	public void apply(PhysicalEntity entity){
		Matrix4f m4 = entity.getBodyMatrix();
		
		Vector3f applyPoint = getApplyPoint().copy().mulTra(m4);
		
		Vector3f forceStrength = getDirection().copy().mulTra(m4).scl(getPower());
		
		entity.getBody().applyForce(forceStrength, applyPoint);
	}


}
