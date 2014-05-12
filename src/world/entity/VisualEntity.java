package world.entity;

import javax.vecmath.Quat4f;

import resources.model.Model;
import utils.math.Matrix4f;
import utils.math.Transform;
import world.culling.Generalizable;

public interface VisualEntity extends Entity, Generalizable{
	
	public void setModel(Model m);
	
	public Model getModel();
	
	//public void openGLInitialization();

	//public void dispose();
	
	public void scale(float s);
	
	public void scale(float x, float y, float z);
	
	public void rotate(Transform t);
	
	public void rotate(Quat4f q);
	
	public Matrix4f getScaleRotationMatrix();
}
