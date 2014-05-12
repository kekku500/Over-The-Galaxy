package world.entity.lighting;

import state.RenderState;
import world.EntityManager;

public class DefaultPointLight extends DefaultLight implements PointLighting{
	
	public DefaultPointLight(EntityManager world) {
		super(world);
	}

	private float constantAttenuation = 1.0f;
	private float linearAttenuation = 0.0001f;
	private float quadricAttenuation = 0.0001f;
	
	@Override
	public void update(float dt){
		super.update(dt);
		
		//setPosition(0,200,getPosition(RenderState.getUpdatingId()).z-10f*dt);
		
		/*constantAttenuation = .19f;
		linearAttenuation = 0.00001f;
		quadricAttenuation = 0.000001f;*/
	}

	@Override
	public void setConstantAttenuation(float c) {
		constantAttenuation = c;
		
	}

	@Override
	public float getConstantAttenuation() {
		return constantAttenuation;
	}

	@Override
	public void setLinearAttenuation(float l) {
		linearAttenuation = l;
		
	}

	@Override
	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	@Override
	public void setQuadricAttenuation(float q) {
		quadricAttenuation = q;
		
	}

	@Override
	public float getQuadricAttenuation() {
		return quadricAttenuation;
	}

}
