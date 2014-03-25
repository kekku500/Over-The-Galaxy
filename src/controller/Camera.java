package controller;

import game.Game;
import game.world.World;
import game.world.culling.ViewFrustum;
import game.world.entities.AbstractEntity;
import game.world.entities.Entity;
import game.world.entities.VisualEntity;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

import utils.R;
import utils.Utils;
import utils.math.Vector3f;
import blender.model.Model;
import blender.model.custom.Sphere;

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

    //Camera config
    private float mouseSensitivity = 0.05f;
    private float movementSpeed = 50.0f; //move 50 units per second
    private float shiftBoost = 10f; //x times faster
    public float pitch, yaw;
    private enum CamType{FP, _6DOF, LOCK}
    private CamType type = CamType.FP;
    
    private R<Entity> following = new R<Entity>();
    private float viewRadius = 30;
    
    //Camera frustum
	public ViewFrustum cameraFrustum;
	
    private Vector3f viewRay = new Vector3f(0,0,1); //Vector which points at the direction your'e looking at
    private Vector3f upVector = new Vector3f(0,1,0); //Points up
    private Vector3f rightVector = new Vector3f(1,0,0); //Cross product of viewRay and upVector
    private Transform uniformTransform = new Transform(new Matrix4f(new Quat4f(0,0,0,1), new Vector3f(0,0,0), 1));
    

	@Override
	public void dispose() {}

	@Override
	public void openGLInitialization() {}

	@Override
	public Entity getLinked() {
		return new Camera().setLink(this);
	}
    
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof Camera){
			Camera ve = (Camera)t;
			uniformTransform = ve.uniformTransform;
			following = ve.following;
		}

		return this;
	}
	
	public void setFollowing(Entity e){
		following.set(e);
	}
	
	public R<Entity> getFollowingWrapper(){
		return following;
	}
    
    public Camera(){
		cameraFrustum = new ViewFrustum();
		cameraFrustum.setProjection(Game.fov, Game.width, Game.height, Game.zNear, Game.zFar);	
    }
   
    World world;
    public Camera(float x, float y, float z, World w){
    	uniformTransform.origin.set(x, y, z);
    	world = w;
    	
    	cameraFrustum = new ViewFrustum();
		cameraFrustum.setProjection(Game.fov, Game.width, Game.height, Game.zNear, Game.zFar);	
    }
    
    private void rotation(float dt){
		float dx = Mouse.getDX() * mouseSensitivity;
		float dy = Mouse.getDY() * mouseSensitivity;
		if(type == CamType._6DOF){
			Transform t = uniformTransform;
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3f(1,0,0), Utils.rads(dy));
			qRotatedy.mul(orientation);
			orientation = qRotatedy;
			
			Quat4f qRotatedx = new Quat4f();
			QuaternionUtil.setRotation(qRotatedx, new Vector3f(0,1,0), Utils.rads(dx));
			qRotatedx.mul(orientation); // vv
			orientation = qRotatedx; //  this makes it DOF6
			orientation.normalize();
			
			t.setRotation(orientation);
		}else if(type == CamType.FP){
			Transform t = uniformTransform;
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3f(1,0,0), Utils.rads(dy));
			qRotatedy.mul(orientation);
			orientation = qRotatedy;
			
			Quat4f qRotatedx = new Quat4f();
			QuaternionUtil.setRotation(qRotatedx, new Vector3f(0,1,0), Utils.rads(dx));
			orientation.mul(qRotatedx); //this line makes it fps
			orientation.normalize();
			
			t.setRotation(orientation);
		}else if(type == CamType.LOCK){
			if(following.get() != null){
				Transform t = following.get().getTransform();
				
				Quat4f orientation = new Quat4f();
				t.getRotation(orientation);
				
				orientation.conjugate();
				
				//Dont wanna look directly from behind
				Quat4f qRotatedy = new Quat4f();
				QuaternionUtil.setRotation(qRotatedy, new Vector3f(0,1,0), Utils.rads(180));
				qRotatedy.mul(orientation);
				orientation = qRotatedy;
				
				Quat4f qRotatedx = new Quat4f();
				QuaternionUtil.setRotation(qRotatedx, new Vector3f(1,0,0), Utils.rads(10));
				qRotatedx.mul(orientation);
				orientation = qRotatedx;
				orientation.normalize();
				
				Transform t2 = uniformTransform;
				t2.setRotation(orientation);
			}
		}
		
		Matrix4f viewMatrix = new Matrix4f();
		uniformTransform.getMatrix(viewMatrix);
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		viewRay = new Vector3f(ray[0], ray[1], ray[2]);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		upVector = new Vector3f(up[0], up[1], up[2]);
		float[] right = new float[4];
		viewMatrix.getRow(0, right);
		rightVector = new Vector3f(right[0], right[1],right[2]);
		
    }
    
    private void translation(float dt){
		float speed = movementSpeed*dt;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			speed *= shiftBoost;
		if(following.get() == null){
			Transform tr = uniformTransform;
			Matrix4f mr = new Matrix4f();
			tr.getMatrix(mr);
			Vector3f delta = new Vector3f();
			if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_S)){
				float[] ray = new float[4];
				mr.getRow(2, ray);
				Vector3f viewRay = new Vector3f(ray[0], ray[1], ray[2]);
				if(Keyboard.isKeyDown(Keyboard.KEY_W)){
					delta.add(viewRay);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_S)){
					delta.add(viewRay.getNegate());
				}
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_D)){
				float[] right = new float[4];
				mr.getRow(0, right);
				Vector3f rightVector = new Vector3f(right[0], right[1], right[2]);
				if(Keyboard.isKeyDown(Keyboard.KEY_A)){
					delta.add(rightVector);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_D)){
					delta.add(rightVector.getNegate());
				}

			}
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
				float[] up = new float[4];
				mr.getRow(1, up);
				Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
					delta.add(upVector);
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
					delta.add(upVector.getNegate());
				}
			}
			if(!delta.isZero()){ //moved
				delta.normalize();
				delta.scale(speed);
				tr.origin.add(delta);
			}
			
			setPosition(new Vector3f(uniformTransform.origin));

		}else{
			Transform tcam = uniformTransform;
			
			Transform tfollow = following.get().getTransform();
			
			Vector3f camPos = new Vector3f();
			viewRay.normalize();
			viewRay.scale(viewRadius);
			camPos.add(tfollow.origin, viewRay.getNegate());
			tcam.origin.set(camPos);
			
			setPosition(new Vector3f(tfollow.origin).copy().add(viewRay.copy().getNegate()));
		}

    }
    
    @Override
    public void update(float dt){
		rotation(dt);
		
		translation(dt);
		
		
		//Culling
		cameraFrustum.setView(getViewRay(), getRightVector(), getUpVector());
		cameraFrustum.setPos(getPosition());
		//frustum.update(); //Update variables required for culling check
		
		cameraFrustum.cullEntities(world.getVisualEntities());
    }
    
    public void lookAt(){
    	Vector3f position = getPosition();
    	//if(following == null)
    		GLU.gluLookAt(position.x, position.y, position.z, position.x+viewRay.x, position.y+viewRay.y, position.z+viewRay.z, upVector.x, upVector.y, upVector.z);	
    	//else
    	//	GLU.gluLookAt(position.x+viewRay.x, position.y+viewRay.y, position.z+viewRay.z, position.x, position.y, position.z, upVector.x, upVector.y, upVector.z);	
    	//if(modelShape.getVBOVertexID() == 0)
    	//	modelShape.prepareVBO();
    	//render();
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
    
}
