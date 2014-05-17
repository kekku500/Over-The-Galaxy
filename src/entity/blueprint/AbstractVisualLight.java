package entity.blueprint;

import math.Vector4f;
import entity.creation.DefaultLight;
import entity.creation.ModeledEntity;
import entity.sheet.Lighting;
import entitymanager.EntityManager;

public abstract class AbstractVisualLight extends ModeledEntity implements Lighting{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractVisualLight(EntityManager world) {
		super(world);
		lightExtension = new DefaultLight(null);
	}

	protected DefaultLight lightExtension;
	
	@Override
	public void update(float dt){
		lightExtension.update(dt);
	}

	@Override
	public void setAmbient(Vector4f ambient) {
		lightExtension.setAmbient(ambient);
	}

	@Override
	public Vector4f getAmbient() {
		return lightExtension.getAmbient();
	}

	@Override
	public void setDiffuse(Vector4f diffuse) {
		lightExtension.setDiffuse(diffuse);
		
	}

	@Override
	public Vector4f getDiffuse() {
		return lightExtension.getDiffuse();
	}

	@Override
	public void setSpecular(Vector4f specular) {
		lightExtension.setSpecular(specular);
		
	}

	@Override
	public Vector4f getSpecular() {
		return lightExtension.getSpecular();
	}

	@Override
	public void setEnabled(boolean enable) {
		lightExtension.setEnabled(enable);
		
	}

	@Override
	public boolean isEnabled() {
		return lightExtension.isEnabled();
	}	
	
	@Override
	public boolean isShadowed() {
		return lightExtension.isShadowed();
	}

	@Override
	public void setShadowed(boolean m) {
		lightExtension.setShadowed(m);
		
	}

}
