package game.world.entities.lighting;

import static org.lwjgl.opengl.GL11.*;
import blender.model.Model;
import blender.model.custom.Sphere;
import utils.math.Vector3f;
import utils.math.Vector4f;
import controller.Camera;
import game.world.entities.Entity;

public class SunLight extends AbstractVisualLight implements DirectionalLighting{
	
	private boolean lightScattering = true;
	private Vector3f localPosition = new Vector3f();
	private float radius;
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof SunLight){
			SunLight ve = (SunLight)t;
			
			lightScattering = ve.isLightScattering();
			localPosition = ve.localPosition.copy();
			radius = ve.getRadius();
		}

		return this;
	}

	@Override
	public void update(float dt) {
		//setEnabled(true);
		//setAmbient(new Vector4f(.00f,.00f,.00f,1.0f));
		//setDiffuse(new Vector4f(0.50f,0.60f,0.00f,1.0f));
		lightExtension.update(dt); //update shadow mapper position
		Camera cam = getWorld().getCamera();
		if(cam != null){
			//Set sun position relative to camera position
			positionRotation.origin.set(localPosition.copy().add(cam.getPosition()));
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
	
	@Override
	public void setPosition(float x, float y, float z){
		localPosition.set(x, y, z);
	}

	@Override
	public Entity getLinked() {
		return new SunLight().setLink(this);
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
