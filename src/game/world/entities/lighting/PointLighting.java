package game.world.entities.lighting;

public interface PointLighting extends Lighting{
	
	public void setConstantAttenuation(float c);
	
	public float getConstantAttenuation();
	
	public void setLinearAttenuation(float l);
	
	public float getLinearAttenuation();
	
	public void setQuadricAttenuation(float q);
	
	public float getQuadricAttenuation();

}
