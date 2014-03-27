package world.entity.lighting;

import utils.math.Vector4f;
import world.entity.Entity;

public class DefaultPointLight extends DefaultLight implements PointLighting{
	
	private float constantAttenuation = 1.0f;
	private float linearAttenuation = 0.0001f;
	private float quadricAttenuation = 0.0001f;
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof DefaultPointLight){
			DefaultPointLight ve = (DefaultPointLight)t;
			
			constantAttenuation = ve.getConstantAttenuation();
			linearAttenuation = ve.getLinearAttenuation();
			quadricAttenuation = ve.getLinearAttenuation();
		}

		return this;
	}
	
	@Override
	public void update(float dt){
		super.update(dt);
		if(getID() == 48){
			setEnabled(true);
		}else if(getID() == 46){
			setEnabled(true);
			/*setSpecular(new Vector4f(0.0f,0.0f,0.0f,1.0f));
			setDiffuse(new Vector4f(0.0f,0.0f,0.0f,1.0f));
			setAmbient(new Vector4f(0.0f,0.0f,0.0f,1.0f));*/
		}
		/*if(getID() == 46)
			setEnabled(true);
		setDiffuse(new Vector4f(0.1f,0.5f,0.1f,1.0f));
		//	private float constantAttenuation = 1.0f, linearAttenuation = .000f, quadricAttenuation = .0001f;
		setEnabled(true);*/
		constantAttenuation = 0.005f;
		linearAttenuation = 0.001f;
		quadricAttenuation = 0.0001f;
		//setEnabled(true);
	}
	
	public Entity getLinked(){
		return new DefaultPointLight().setLink(this);
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
