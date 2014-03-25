package game.world.entities.lighting;

import game.world.entities.AbstractEntity;
import game.world.entities.Entity;
import game.world.graphics.ShadowMapper;
import utils.math.Vector4f;

public class DefaultLight extends AbstractEntity implements Lighting {
	
	private Vector4f ambient, diffuse, specular;
	private boolean enabled = true;
	
	private ShadowMapper shadowMapper;
	
	public DefaultLight(){
		ambient = defaultAmbient.copy();
		diffuse = defaultDiffuse.copy();
		specular = defaultSpeclar.copy();
	}
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof Lighting){
			Lighting ve = (Lighting)t;
			
			ambient = ve.getAmbient().copy();
			diffuse = ve.getDiffuse().copy();
			specular = ve.getSpecular().copy();
			enabled = ve.isEnabled();
			
			shadowMapper = ve.getShadowMapper();
		}

		return this;
	}

	public Vector4f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector4f ambient) {
		this.ambient = ambient;
	}


	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}

	public void setSpecular(Vector4f specular) {
		this.specular = specular;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ShadowMapper getShadowMapper() {
		return shadowMapper;
	}

	public void setShadowMapper(ShadowMapper shadowMapper) {
		this.shadowMapper = shadowMapper;
	}

	@Override
	public boolean isShadowed() {
		if(shadowMapper == null)
			return false;
		return true;
	}


	@Override
	public void update(float dt){
		//if(isShadowed())
		//	shadowMapper.setPos(getPosition());
	}
	
	@Override
	public void dispose(){
		if(isShadowed())
			shadowMapper.dispose();
	}
	
	@Override
	public void openGLInitialization(){
		if(isShadowed())
			shadowMapper.init();
	}


	@Override
	public Entity getLinked() {
		return new DefaultLight().setLink(this);
	}

}
