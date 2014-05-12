package world.entity.lighting;

import utils.math.Vector4f;
import world.EntityManager;
import world.entity.AbstractEntity;
import world.graphics.ShadowMapper;

public class DefaultLight extends AbstractEntity implements Lighting {
	
	private Vector4f ambient, diffuse, specular;
	private boolean enabled = true;
	
	private boolean shadow;
	
	public DefaultLight(EntityManager world){
		super(world);
		ambient = defaultAmbient.copy();
		diffuse = defaultDiffuse.copy();
		specular = defaultSpeclar.copy();
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

	@Override
	public boolean isShadowed() {
		return shadow;
	}

	@Override
	public void setShadowed(boolean m) {
		shadow = m;
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}
	
	/*@Override
	public void dispose(){

	}
	
	@Override
	public void openGLInitialization(){
	}*/


}
