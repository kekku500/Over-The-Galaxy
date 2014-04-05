package controller;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

import resources.model.Model;
import resources.model.custom.Sphere;
import state.Game;
import utils.R;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import world.World;
import world.culling.ViewFrustum;
import world.entity.AbstractEntity;
import world.entity.Entity;
import world.entity.VisualEntity;
import world.entity.WorldEntity;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

public class Camera extends AbstractEntity{

    private float fov, width, height, zNear, zFar;
	private Matrix4f projection = new Matrix4f();
	private Matrix4f view = new Matrix4f();

    private Vector3f viewRay = new Vector3f(0,0,1); //Vector which points at the direction your'e looking at
    private Vector3f upVector = new Vector3f(0,1,0); //Points up
    private Vector3f rightVector = new Vector3f(1,0,0); //Cross product of viewRay and upVector
    
	public ViewFrustum cameraFrustum = new ViewFrustum();
    
    public Camera(){
		setProjectionParameters(Game.fov, Game.width, Game.height, Game.zNear, Game.zFar);	
    }
    
    public Camera(float x, float y, float z){
    	setPosition(x, y, z);
    	
		setProjectionParameters(Game.fov, Game.width, Game.height, Game.zNear, Game.zFar);	
    }
    
	@Override
	public Entity getLinked() {
		return new Camera().setLink(this);
	}
    
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof Camera){
			Camera ve = (Camera)t;
		}

		return this;
	}
    

     
    @Override
    public void update(float dt){
		cameraFrustum.setView(getViewRay(), getRightVector(), getUpVector());
		cameraFrustum.setPos(getPosition());
		cameraFrustum.cullEntities(getWorld().getVisualEntities());
    }
    
    public void setProjectionParameters(float fov, float width, float height, float zNear, float zFar){
    	this.fov = fov;
    	this.width = width;
    	this.height = height;
    	this.zNear = zNear;
    	this.zFar = zFar;
    	cameraFrustum.setProjection(fov, width, height, zNear, zFar);
    }
    
    public void lookAt(){
    	Vector3f position = getPosition();
    	GLU.gluLookAt(position.x, position.y, position.z, position.x+viewRay.x, position.y+viewRay.y, position.z+viewRay.z, upVector.x, upVector.y, upVector.z);	
    }
    
    public void storeViewMatrixFromOpenGL(){
    	FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        glLoadIdentity();
    	Vector3f position = getPosition();
        GLU.gluLookAt(position.x, position.y, position.z, position.x+viewRay.x, position.y+viewRay.y, position.z+viewRay.z, upVector.x, upVector.y, upVector.z);	
        glGetFloat(GL_MODELVIEW_MATRIX, fb);
        view.set(fb);
    }
    
    public void storeProjectionMatrixFromOpenGL(){
    	FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        glLoadIdentity();
        gluPerspective(Game.fov, (float) width / (float) height, Game.zNear, Game.zFar);
        glGetFloat(GL_MODELVIEW_MATRIX, fb);
        projection.set(fb);
    }
    
    public void updateViewMatrix(FloatBuffer fb){
    	fb.rewind();
        glLoadIdentity();
        lookAt();
        glGetFloat(GL_MODELVIEW_MATRIX, fb);
    }
    
    public Vector3f getUpVector(){
    	return upVector;
    }
    
    public Vector3f getRightVector(){
    	return rightVector;
    }
    
    public Vector3f getViewRay(){
    	return viewRay;
    }
    
    public Matrix4f getProjectionMatrix(){
    	return projection;
    }
    
    public Matrix4f getViewMatrix(){
    	return view;
    }
    
}
