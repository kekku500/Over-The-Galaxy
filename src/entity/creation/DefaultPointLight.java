package entity.creation;

import entity.sheet.PointLighting;
import entitymanager.EntityManager;

public class DefaultPointLight extends DefaultLight implements PointLighting{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		
		/*constantAttenuation = .02f;
		linearAttenuation = 0.001f;
		quadricAttenuation = 0.00001f;*/
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
