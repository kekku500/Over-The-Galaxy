package entity.sheet;

import math.Vector3f;

public interface SpotLighting extends Lighting{
	
	public void setSpotCutoff(float s);
	
	public float getSpotCutoff();
	
	public void setSpotLightDirection(Vector3f v);
	
	public Vector3f getSpotLightDirection();
	
	public void setSpotExponent(float e);
	
	public float getSpotExponent();

}
