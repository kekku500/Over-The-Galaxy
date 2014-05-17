package graphics;

import java.nio.FloatBuffer;

import utils.ArrayList;
import entity.creation.Camera;
import entity.sheet.Lighting;
import entity.sheet.VisualEntity;

public interface ShadowMapping {
	
	public void init();
	
	public void render(Camera camera, ArrayList<VisualEntity> entities, int fbo);
	
	public Lighting getParent();
	
	public void setParent(Lighting parent);
	
	public int getShadowMap();
	
	public FloatBuffer getLightTexture();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enable);
	
	public void dispose();
	
	

}
