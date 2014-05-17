package entity.creation;

import static org.lwjgl.opengl.GL11.glColor3f;
import math.Vector3f;
import math.Vector4f;
import resources.model.Model;
import resources.model.custom.Sphere;
import entity.blueprint.AbstractVisualLight;
import entity.sheet.DirectionalLighting;
import entitymanager.EntityManager;

public class SunLight extends AbstractVisualLight implements DirectionalLighting{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public SunLight(EntityManager world) {
		super(world);
	}

	private boolean lightScattering = true;
	private Vector3f localPosition = new Vector3f();
	private float radius;
	

	@Override
	public void update(float dt) {
		setShadowed(false);
		setAmbient(new Vector4f(.5f,.5f,.5f,1.0f));
		setDiffuse(new Vector4f(1.7f,1.7f,1.7f,1.0f));
		lightExtension.update(dt); //update shadow mapper position
		Controller cam = getEntityManager().getState().getCamera();
		if(cam != null){
			//Set sun position relative to camera position
			getTransform().updating().origin.set(localPosition.copy().add(new Vector3f(cam.getTransform().updating().origin)));
		}
		/*if(isShadowed()){
			getShadowMapper().setSceneOrigin(cam.getPos());
		}*/
		
	}
	
	@Override
	public void render(){
		Vector4f a = getAmbient();
		Vector4f d = getDiffuse();
		glColor3f(a.x+d.x, a.y+d.y, a.z+d.z);
		if(lightScattering)
			getModel().isGodRays = true;
		super.render();
		if(lightScattering)
			getModel().isGodRays = false;
		glColor3f(1,1,1);
	}
	
	public void setPosition(float x, float y, float z){
		localPosition.set(x, y, z);
	}
	
	@Override
	public void setModel(Model m){
		super.setModel(m);
		if(m instanceof Sphere){
			Sphere s = (Sphere)m;
			radius = s.getRadius();
		}
	}
	
	public float getRadius(){
		return radius;
	}

	public boolean isLightScattering() {
		return lightScattering;
	}

	public void setLightScattering(boolean enableLightScattering) {
		this.lightScattering = enableLightScattering;
	}


}
