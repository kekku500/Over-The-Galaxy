package entity.sheet;

import graphics.culling.Generalizable;

import javax.vecmath.Quat4f;

import math.Matrix4f;
import math.Transform;
import resources.model.Model;

public interface VisualEntity extends Entity, Generalizable{
	
	public void setModel(Model m);
	
	public Model getModel();
	
	public void scale(float s);
	
	public void scale(float x, float y, float z);
	
	public void rotate(Transform t);
	
	public void rotate(Quat4f q);
	
	public Matrix4f getScaleRotationMatrix();
	
	public void castShadow(boolean b);
	
	public boolean isShadowEnabled();
}
