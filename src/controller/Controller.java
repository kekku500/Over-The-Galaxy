package controller;

import input.InputConfig;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import utils.Utils;
import utils.math.Vector3f;
import world.entity.Entity;
import world.entity.WorldEntity;

import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

public class Controller extends Camera{
	
    //Camera config
    private float mouseSensitivity = 3;
    private float movementSpeed = 50.0f; //move 50 units per second
    private float shiftBoost = 10f; //x times faster
    public enum CamType{FP, _6DOF, LOCK}
    private CamType type = CamType.FP;
    
    private WorldEntity following;
    
    private float viewRadius = 30;
    
    private Transform referencedTransform = new Transform(new Matrix4f(new Quat4f(0,0,0,1), new Vector3f(0,0,0), 1));
    
    public Controller(){}
    
    public Controller(float x, float y, float z){
    	referencedTransform.origin.set(x, y, z);
    }
    
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof Camera){
			Controller ve = (Controller)t;
			following = ve.getFollowing();
			referencedTransform = ve.referencedTransform;
			type = ve.type;
		}

		return this;
	}
    
    @Override
    public Entity getLinked(){
    	return new Controller().setLink(this);
    }
    
    @Override
    public void update(float dt){
    	rotation(dt);
    	
    	translation(dt);
    	
    	super.update(dt);
    }
	
    private void rotation(float dt){
		float dx = 0;
		float dy = 0;
    	if(Mouse.isGrabbed()){
    		dx = InputConfig.getRotationX() * mouseSensitivity * dt;
    		dy = InputConfig.getRotationY() * mouseSensitivity * dt;
    	}
		float dz = 0;
		int wheel = InputConfig.getRotationZ();
		if(wheel > 0)
			dz += 200 * mouseSensitivity * dt;
		else if(wheel < 0)
			dz -= 200 * mouseSensitivity * dt; 
		if(type == CamType._6DOF){
			Transform t = referencedTransform;
			
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
			
			
			Quat4f qRotatedz = new Quat4f();
			QuaternionUtil.setRotation(qRotatedz, new Vector3f(0,0,1), Utils.rads(dz));
			qRotatedz.mul(orientation); // vv
			orientation = qRotatedz; //  this makes it DOF6
			
			orientation.normalize();
			
			t.setRotation(orientation);
		}else if(type == CamType.FP){
			Transform t = referencedTransform;
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3f(1,0,0), Utils.rads(dy));
			qRotatedy.mul(orientation);
			orientation = qRotatedy;
			
			Quat4f qRotatedx = new Quat4f();
			QuaternionUtil.setRotation(qRotatedx, new Vector3f(0,1,0), Utils.rads(dx));
			orientation.mul(qRotatedx); //this line makes it fps
			
			Quat4f qRotatedz = new Quat4f();
			QuaternionUtil.setRotation(qRotatedz, new Vector3f(0,0,1), Utils.rads(dz));
			orientation.mul(qRotatedz); //this line makes it fps
			orientation.normalize();
			
			
			t.setRotation(orientation);
		}else if(type == CamType.LOCK){
			if(getFollowing() != null){
				Transform t = getFollowing().getTransform();
				
				Quat4f orientation = new Quat4f();
				t.getRotation(orientation);
				
				orientation.conjugate();
				
				//Dont wanna look directly from behind
				/*Quat4f qRotatedy = new Quat4f();
				QuaternionUtil.setRotation(qRotatedy, new Vector3f(0,1,0), Utils.rads(180));
				qRotatedy.mul(orientation);
				orientation = qRotatedy;*/
				
				/*Quat4f qRotatedx = new Quat4f();
				QuaternionUtil.setRotation(qRotatedx, new Vector3f(1,0,0), Utils.rads(10));
				qRotatedx.mul(orientation);
				orientation = qRotatedx;
				orientation.normalize();*/
				
				Transform t2 = referencedTransform;
				t2.setRotation(orientation);
			}
		}
		
		Matrix4f viewMatrix = new Matrix4f();
		referencedTransform.getMatrix(viewMatrix);
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		getViewRay().set(ray[0], ray[1], ray[2]);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		getUpVector().set(up[0], up[1], up[2]);
		float[] right = new float[4];
		viewMatrix.getRow(0, right);
		getRightVector().set(right[0], right[1], right[2]);
		
    }
    
    private void translation(float dt){
		float speed = movementSpeed*dt;
		if(Keyboard.isKeyDown(InputConfig.translationBoost))
			speed *= shiftBoost;
		if(getFollowing() == null){
			Transform tr = referencedTransform;
			Matrix4f mr = new Matrix4f();
			tr.getMatrix(mr);
			Vector3f delta = new Vector3f();
			if(Keyboard.isKeyDown(InputConfig.translationForward) || Keyboard.isKeyDown(InputConfig.translationBackward)){
				float[] ray = new float[4];
				mr.getRow(2, ray);
				Vector3f viewRay = new Vector3f(ray[0], ray[1], ray[2]);
				if(Keyboard.isKeyDown(InputConfig.translationForward)){
					delta.add(viewRay);
				}
				if(Keyboard.isKeyDown(InputConfig.translationBackward)){
					delta.add(viewRay.getNegate());
				}
			}
			if(Keyboard.isKeyDown(InputConfig.translationRight) || Keyboard.isKeyDown(InputConfig.translationLeft)){
				float[] right = new float[4];
				mr.getRow(0, right);
				Vector3f rightVector = new Vector3f(right[0], right[1], right[2]);
				if(Keyboard.isKeyDown(InputConfig.translationLeft)){
					delta.add(rightVector);
				}
				if(Keyboard.isKeyDown(InputConfig.translationRight)){
					delta.add(rightVector.getNegate());
				}

			}
			if(Keyboard.isKeyDown(InputConfig.translationUp) || Keyboard.isKeyDown(InputConfig.translationDown)){
				float[] up = new float[4];
				mr.getRow(1, up);
				Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
				if(Keyboard.isKeyDown(InputConfig.translationUp)){
					delta.add(upVector);
				}
				if(Keyboard.isKeyDown(InputConfig.translationDown)){
					delta.add(upVector.getNegate());
				}
			}
			if(!delta.isZero()){ //moved
				delta.normalize();
				delta.scale(speed);
				tr.origin.add(delta);
			}
			
			setPosition(new Vector3f(referencedTransform.origin));

		}else{
			Transform tcam = referencedTransform;
			
			Transform tfollow = getFollowing().getTransform();
			
			Vector3f camPos = new Vector3f();
			Vector3f viewRay = getViewRay().copy();
			viewRay.normalize();
			viewRay.scale(viewRadius);
			camPos.add(tfollow.origin, viewRay.getNegate());
			tcam.origin.set(camPos);
			
			setPosition(new Vector3f(tfollow.origin).copy().add(viewRay.getNegate()));
		}

    }
    
	public void setFollowing(WorldEntity e){
		following = e;
	}
	
	public boolean isFollowing(){
		if(following != null)
			return true;
		return false;
	}
	
	public WorldEntity getFollowing(){
		return following;
	}
	
	public void setType(CamType type){
		this.type = type;
	}

}
