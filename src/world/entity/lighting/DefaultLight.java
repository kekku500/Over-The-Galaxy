package world.entity.lighting;

import utils.math.Vector4f;
import world.entity.AbstractEntity;
import world.entity.Entity;
import world.graphics.ShadowMapper;

public class DefaultLight extends AbstractEntity implements Lighting {
	
	private Vector4f ambient, diffuse, specular;
	private boolean enabled = true;
	
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


	@Override
	public void update(float dt){
		//if(isShadowed())
		//	shadowMapper.setPos(getPosition());
	}
	
	/*@Override
	public void dispose(){

	}
	
	@Override
	public void openGLInitialization(){
	}*/


	@Override
	public Entity getLinked() {
		return new DefaultLight().setLink(this);
	}

}
