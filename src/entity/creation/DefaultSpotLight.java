package entity.creation;

import main.state.RenderState;
import math.Matrix4f;
import math.Vector3f;
import math.Vector4f;
import entity.blueprint.AbstractVisualPhysicsEntity;
import entity.sheet.SpotLighting;
import entitymanager.EntityManager;

public class DefaultSpotLight extends DefaultLight implements SpotLighting{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DefaultSpotLight(EntityManager world) {
		super(world);
	}

	private final Vector4f initialDirection = new Vector4f(0,0,1, 0);
	private float spotCutoff = (float)Math.cos(Math.toRadians(30)); //GL_SPOT_CUTOFF
	private Vector3f spotlightDirection = new Vector3f(-1,-2, -1); //GL_SPOT_DIRECTION 
	private float spotExponent = 0.1f; //GL_SPOT_EXPONENT
	
	@Override
	public void update(float dt){
		super.update(dt);
		//spotCutoff = (float)Math.cos(Math.toRadians(30));
		//spotExponent = .02f;
		if(getParent() != null){
			Vector4f changedDir = initialDirection.copy();
			if(getParent() instanceof AbstractVisualPhysicsEntity){
				AbstractVisualPhysicsEntity m = (AbstractVisualPhysicsEntity)getParent();
				Matrix4f rot = new Matrix4f();
				m.getTransform().updating().getMatrix(rot);
				rot.inv();
				changedDir.mul(rot);
				spotlightDirection.set(changedDir.x, changedDir.y, changedDir.z);
			}
			if(getParent() instanceof ModeledEntity){
				ModeledEntity m = (ModeledEntity)getParent();
				Matrix4f rot = m.rotationMatrix.copy().inv();
				changedDir.mul(rot);
				spotlightDirection.set(changedDir.x, changedDir.y, changedDir.z);
			}
		}		

	}
	
	@Override
	public float getSpotCutoff() {
		return spotCutoff;
	}
	
	@Override
	public void setSpotCutoff(float spotCutoff) {
		this.spotCutoff = spotCutoff;
	}
	
	@Override
	public Vector3f getSpotLightDirection() {
		return spotlightDirection;
	}
	
	@Override
	public void setSpotLightDirection(Vector3f spotLightDirection) {
		this.spotlightDirection = spotLightDirection;
	}
	
	@Override
	public float getSpotExponent() {
		return spotExponent;
	}
	
	@Override
	public void setSpotExponent(float spotExponent) {
		this.spotExponent = spotExponent;
	}


}
