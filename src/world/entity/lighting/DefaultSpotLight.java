package world.entity.lighting;

import utils.math.Vector3f;
import utils.math.Vector4f;
import world.entity.Entity;

public class DefaultSpotLight extends DefaultLight implements SpotLighting{
	
	private float spotCutoff = (float)Math.cos(Math.toRadians(30)); //GL_SPOT_CUTOFF
	private Vector3f spotlightDirection = new Vector3f(-1,-2, -1); //GL_SPOT_DIRECTION 
	private float spotExponent = 0.1f; //GL_SPOT_EXPONENT
	
	public Entity getLinked(){
		return new DefaultSpotLight().setLink(this);
	}
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof DefaultPointLight){
			DefaultSpotLight ve = (DefaultSpotLight)t;
			spotCutoff = ve.getSpotCutoff();
			spotlightDirection = ve.getSpotLightDirection();
			spotExponent = ve.getSpotExponent();
		}

		return this;
	}
	
	@Override
	public void update(float dt){
		super.update(dt);
		setDiffuse(new Vector4f(1.7f, 1.5f, 1.2f, 1.0f));
		spotCutoff = (float)Math.cos(Math.toRadians(30));
		spotExponent = .1f;
		setSpotLightDirection(new Vector3f(-1,-1,-1));
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
