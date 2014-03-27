package world.entity;

import static org.lwjgl.opengl.GL11.glMultMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import javax.vecmath.Quat4f;

import resources.model.Model;
import utils.math.Matrix4f;
import utils.math.Transform;

public abstract class AbstractVisualEntity extends AbstractEntity implements VisualEntity{
	
	protected Model model;
	
	protected Matrix4f scaleRotationMatrix;
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof VisualEntity){
			VisualEntity ve = (VisualEntity)t;
			
			model = ve.getModel();
			scaleRotationMatrix = ve.getScaleRotationMatrix();
		}

		return this;
	}
	
	@Override
	public Matrix4f getScaleRotationMatrix(){
		return scaleRotationMatrix;
	}

	@Override
	public void setModel(Model m) {
		model = m;	
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public void render() {
		if(!hasModel())
			return;
		
		glPushMatrix(); 
		
		//Translate, Rotate
		Matrix4f m4 = new Matrix4f();
		getTransform().getMatrix(m4);
		m4.transpose();
		glMultMatrix(m4.asFlippedFloatBuffer());
		
		//Rotate, Scale
		if(scaleRotationMatrix != null){
			glMultMatrix(scaleRotationMatrix.asFlippedFloatBuffer()); 
		}
		
		getModel().render();
	    
	    glPopMatrix(); 	
	}
	
	private void createScaleRotationMatrix(){
		scaleRotationMatrix = new Matrix4f();
		scaleRotationMatrix.setIdentity();
	}

	@Override
	public void scale(float s) {
		scale(s, Float.NaN, Float.NaN);
	}
	
	@Override
	public void scale(float x, float y, float z) {
		if(isInWorld())
			return;
		if(scaleRotationMatrix == null)
			createScaleRotationMatrix();
		if(Float.isNaN(y) && Float.isNaN(z)){
			scaleRotationMatrix.scaleRelative(x);
		}else
			scaleRotationMatrix.scaleRelative(x, y, z);	
	}

	@Override
	public void rotate(Transform t) {
		Quat4f rot = new Quat4f();
		t.getRotation(rot);
		rotate(rot);
	}

	@Override
	public void rotate(Quat4f q) {
		if(isInWorld())
			return;
		if(scaleRotationMatrix == null)
			createScaleRotationMatrix();
		scaleRotationMatrix.rotateRelative(q);
	}

	@Override
	public boolean hasModel() {
		if(model == null)
			return false;
		return true;
	}

}
