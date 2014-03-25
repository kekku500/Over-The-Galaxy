package game.world.entities;

import javax.vecmath.Quat4f;

import utils.math.Matrix4f;
import utils.math.Transform;
import blender.model.Model;

public interface VisualEntity extends Entity{
	
	public void setModel(Model m);
	
	public Model getModel();
	
	public void openGLInitialization();
	
	public void render();

	public void dispose();
	
	public void scale(float s);
	
	public void scale(float x, float y, float z);
	
	public void rotate(Transform t);
	
	public void rotate(Quat4f q);
	
	public boolean hasModel();
	
	public Matrix4f getScaleRotationMatrix();
}
