package world.entity.lighting;

import utils.math.Vector4f;
import world.EntityManager;
import world.entity.create.ModeledEntity;
import world.graphics.ShadowMapper;

public abstract class AbstractVisualLight extends ModeledEntity implements Lighting{
	
	public AbstractVisualLight(EntityManager world) {
		super(world);
		lightExtension = new DefaultLight(world);
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
