package world.entity.create;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glMultMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex3f;

import javax.vecmath.Quat4f;

import org.lwjgl.opengl.Drawable;

import resources.model.Model;
import state.RenderState;
import state.StateVariable;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import world.EntityManager;
import world.culling.BoundingAxis;
import world.culling.BoundingSphere;
import world.entity.AbstractEntity;
import world.entity.VisualEntity;

public class ModeledEntity extends AbstractEntity implements VisualEntity{
	
	protected Model model;
	
	protected Matrix4f scaleRotationMatrix;
	
	public Matrix4f rotationMatrix;

	public float rotX, rotY, rotZ;
	
	protected StateVariable<BoundingAxis> boundingAxis;
	protected StateVariable<BoundingSphere> boundingSphere;
	
	public ModeledEntity(EntityManager world){
		super(world);
		scaleRotationMatrix = new Matrix4f();
		scaleRotationMatrix.idy();
		rotationMatrix = new Matrix4f();
		rotationMatrix.idy();
		boundingAxis = new StateVariable<BoundingAxis>(new BoundingAxis());
		boundingSphere = new StateVariable<BoundingSphere>(new BoundingSphere());
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
	
	public Vector3f drawLine1 = null;
	public Vector3f drawLine2 = null;

	@Override
	public void render() {
		if(getModel() == null)
			return;
		
		glPushMatrix(); 
		
		//Translate, Rotate
		Matrix4f m4 = new Matrix4f();
		getTransform(RenderState.getRenderingId()).getMatrix(m4);
		m4.transpose();
		glMultMatrix(m4.fb());
		
		glMultMatrix(rotationMatrix.fb());
		
		//Rotate, Scale
		if(scaleRotationMatrix != null){
			glMultMatrix(scaleRotationMatrix.fb()); 
		}
		
		
		
		getModel().render();
		
	    glPopMatrix(); 	
	    
	    if(drawLine1 != null && drawLine2 != null){
	        glLineWidth(5);
			glColor3f(1f, 1f, 1f);

			
			 glBegin(GL_LINES);
		        glVertex3f(drawLine1.x, 
		        		drawLine1.y,
		        		drawLine1.z);
		        glVertex3f(drawLine2.x, 
		        		drawLine2.y,
		        		drawLine2.z);
		     glEnd();

	    }
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
		if(scaleRotationMatrix == null)
			createScaleRotationMatrix();
		if(Float.isNaN(y) && Float.isNaN(z)){
			scaleRotationMatrix.scaleLeft(x);
		}else
			scaleRotationMatrix.scaleleft(x, y, z);	
	}

	@Override
	public void rotate(Transform t) {
		Quat4f rot = new Quat4f();
		t.getRotation(rot);
		rotate(rot);
	}

	@Override
	public void rotate(Quat4f q) {
		if(scaleRotationMatrix == null)
			createScaleRotationMatrix();
		scaleRotationMatrix.rotateLeft(q);
	}
	
	
	
	@Override
	public BoundingAxis getBoundingAxis() {
		return null;
	}
	
	@Override
	public BoundingSphere getBoundingSphere() {
		return null;
	}

	@Override
	public void update(float dt) {}

}
