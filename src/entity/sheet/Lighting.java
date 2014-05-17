package entity.sheet;

import math.Vector4f;

public interface Lighting extends Entity{
	
	public final Vector4f defaultDiffuse = new Vector4f(.75f, .75f, .75f, 1.0f);
	public final Vector4f defaultAmbient = new Vector4f(.25f, .25f, .25f, 1.0f);
	public final Vector4f defaultSpeclar = new Vector4f(.1f, .1f, .1f, 1.0f);
	
	/**
	 * @param ambient Light in the shadows.
	 */
	public void setAmbient(Vector4f ambient);
	
	public Vector4f getAmbient();
	
	/**
	 * @param diffuse Exposed to the light.
	 */
	public void setDiffuse(Vector4f diffuse);
	
	public Vector4f getDiffuse();
	
	/**
	 * @param specular Reflection light.
	 */
	public void setSpecular(Vector4f specular);
	
	public Vector4f getSpecular();

	public void setEnabled(boolean enable);
	
	public boolean isEnabled();
	
	public boolean isShadowed();
	
	public void setShadowed(boolean m);

}
