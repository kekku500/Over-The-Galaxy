package game.world.entities;

import game.world.graphics.ShadowMapper;

import utils.math.Vector3f;
import utils.math.Vector4f;

public class LightSource extends AbstractEntity {
	
	//General light
	private Vector4f ambient, diffuse, specular;
	
	//Spotlight
	private float spotCutoff = (float)Math.cos(Math.toRadians(30)); //GL_SPOT_CUTOFF
	private Vector3f spotLightDirection = new Vector3f(-1,-2, -1); //GL_SPOT_DIRECTION 
	private float spotExponent = 0.1f; //GL_SPOT_EXPONENT
	
	public enum LightType{DIRECTIONAL, POINT, SPOT};
	private LightType lightType = LightType.SPOT;
	
	//Point fade
	private float constantAttenuation = 1.0f, linearAttenuation = .000f, quadricAttenuation = .000f;
	
	
	public ShadowMapper shadowMapper;
	


	public LightSource(boolean isStatic){
		ambient = new Vector4f(.25f, .25f, .25f, 1.0f);
		diffuse = new Vector4f(1.7f, 1.7f, 1.7f, 1.0f);
		specular = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);
		lightType = LightType.SPOT;
	}
	
	@Override
	public Entity getLinked(){
		return new LightSource(true).linkTo(this);
	}
	
	public ShadowMapper getShadowMapper() {
		return shadowMapper;
	}
	public void setShadowMapper(ShadowMapper shadowMapper) {
		this.shadowMapper = shadowMapper;
	}
	
	public float getConstantAttenuation() {
		return constantAttenuation;
	}

	public void setConstantAttenuation(float constantAttenuation) {
		this.constantAttenuation = constantAttenuation;
	}

	
	public int getLightType() {
		if(lightType == LightType.DIRECTIONAL)
			return 0;
		else if(lightType == LightType.POINT)
			return 1;
		else //SPOT
			return 2;
	}

	public void setLightType(LightType lightType) {
		this.lightType = lightType;
	}
	
	public boolean isShadowed(){
		if(shadowMapper == null)
			return false;
		return true;
	}
	
	@Override
	public void dispose(){
		if(isShadowed())
			shadowMapper.dispose();
	}

	public float getSpotExponent() {
		return spotExponent;
	}

	public void setSpotExponent(float spotExponent) {
		if(spotExponent >= 0 && spotExponent <= 128)
			this.spotExponent = spotExponent;
		else
			spotExponent = 0;
	}

	
	public void init(){
		if(isShadowed())
			shadowMapper.init();
	}
	
	public Vector3f getSpotLightDirection() {
		return spotLightDirection;
	}
	
	public void setSpotLightDirection(Vector3f spotLightDirection) {
		this.spotLightDirection = spotLightDirection;
	}

	public float getSpotCutoff() {
		return spotCutoff;
	}

	public void setSpotCutoff(float spotLight) {
		if(spotLight <= 90 && spotLight >= 0)
			this.spotCutoff = spotLight;
		else
			this.spotCutoff = 180;
	}
	
	public Vector4f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector4f ambient) {
		ambient.setPositive();
		this.ambient = ambient;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		ambient.setPositive();
		this.diffuse = diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}

	public void setSpecular(Vector4f specular) {
		ambient.setPositive();
		this.specular = specular;
	}

	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	public void setLinearAttenuation(float linearAttenuation) {
		if(linearAttenuation < 0)
			linearAttenuation = 0;
		this.linearAttenuation = linearAttenuation;
	}

	public float getQuadricAttenuation() {
		return quadricAttenuation;
	}

	public void setQuadricAttenuation(float quadricAttenuation) {
		if(quadricAttenuation < 0)
			quadricAttenuation = 0;
		this.quadricAttenuation = quadricAttenuation;
	}

}
