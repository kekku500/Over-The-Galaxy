package game.world.entities.lighting;

import utils.math.Vector4f;
import game.world.entities.AbstractVisualEntity;
import game.world.entities.Entity;
import game.world.graphics.ShadowMapper;

public abstract class AbstractVisualLight extends AbstractVisualEntity implements Lighting{
	
	protected DefaultLight lightExtension = new DefaultLight();
	
	@Override
	public void update(float dt){
		lightExtension.update(dt);
	}
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof AbstractVisualLight){
			AbstractVisualLight ve = (AbstractVisualLight)t;
			
			lightExtension.setLink(ve.lightExtension);
		}

		return this;
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
	public ShadowMapper getShadowMapper() {
		return lightExtension.getShadowMapper();
	}

	@Override
	public void setShadowMapper(ShadowMapper sm) {
		lightExtension.setShadowMapper(sm);
	}

	@Override
	public boolean isShadowed() {
		return lightExtension.isShadowed();
	}	
	
	@Override
	public void openGLInitialization(){
		super.openGLInitialization();
		if(isShadowed()){
			getShadowMapper().init();
		}
	}

}
