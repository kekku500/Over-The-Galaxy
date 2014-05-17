package entity.creation;

import input.InputConfig;
import entity.sheet.Entity;
import entitymanager.EntityManager;

import javax.vecmath.Quat4f;

import main.state.RenderState;
import math.Matrix4f;
import math.Transform;
import math.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import utils.Utils;

import com.bulletphysics.linearmath.QuaternionUtil;

public class Controller extends Camera{
	
	private static final long serialVersionUID = 1L;
	//Camera config
    private float mouseSensitivity = 3;
    private float movementSpeed = 50.0f; //move 50 units per second
    private float shiftBoost = 10f; //x times faster
    public enum CamType{FP, _6DOF, LOCK}
    private CamType type = CamType.FP;
    
    private Entity following;
    
    private float viewRadius = 30;
    
    public Controller(EntityManager world,float x, float y, float z){
    	super(world, x, y, z);
    }
    
    @Override
    public void update(float dt){
		Transform updating = getTransform().updating();
		Transform uptodate = getTransform().uptodate();
		Matrix4f uptodatem4 = new Matrix4f();
		uptodate.getMatrix(uptodatem4);
		updating.set(uptodatem4);
		viewRadius = -6;
		
    	rotation(dt);
    	
    	translation(dt);
    	
    	super.update(dt);
    }
	
    private void rotation(float dt){
		float dx = 0;
		float dy = 0;
    	if(Mouse.isGrabbed()){
    		dx = getEntityManager().getState().getInput().getMouseDX() * mouseSensitivity * dt;
    		dy = getEntityManager().getState().getInput().getMouseDY() * mouseSensitivity * dt;
    	}
		float dz = 0;
		int wheel = getEntityManager().getState().getInput().getMouseDWheel(RenderState.updating());
		if(wheel > 0)
			dz += 200 * mouseSensitivity * dt;
		else if(wheel < 0)
			dz -= 200 * mouseSensitivity * dt; 
		if(type == CamType._6DOF){
			Transform t = getTransform().updating();//referencedTransform;
			
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
			Transform t = getTransform().updating();
			
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
				Transform t = getFollowing().getTransform().updating();
				
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
				
				Transform t2 = getTransform().updating();
				t2.setRotation(orientation);
			}
		}
    }
    
    private void translation(float dt){
		float speed = movementSpeed*dt;
		if(Keyboard.isKeyDown(InputConfig.translationBoost))
			speed *= shiftBoost;
		if(getFollowing() == null){
			Transform updating = getTransform().updating();
			Matrix4f uptodatem4 = new Matrix4f();
			updating.getMatrix(uptodatem4);
			updating.set(uptodatem4);
			
			Vector3f delta = new Vector3f();
			if(Keyboard.isKeyDown(InputConfig.translationForward) || Keyboard.isKeyDown(InputConfig.translationBackward)){
				float[] ray = new float[4];
				uptodatem4.getRow(2, ray);
				Vector3f viewRay = new Vector3f(ray[0], ray[1], ray[2]);
				if(Keyboard.isKeyDown(InputConfig.translationForward)){
					delta.add(viewRay);
				}
				if(Keyboard.isKeyDown(InputConfig.translationBackward)){
					delta.add(viewRay.negater());
				}
			}
			if(Keyboard.isKeyDown(InputConfig.translationRight) || Keyboard.isKeyDown(InputConfig.translationLeft)){
				float[] right = new float[4];
				uptodatem4.getRow(0, right);
				Vector3f rightVector = new Vector3f(right[0], right[1], right[2]);
				if(Keyboard.isKeyDown(InputConfig.translationLeft)){
					delta.add(rightVector);
				}
				if(Keyboard.isKeyDown(InputConfig.translationRight)){
					delta.add(rightVector.negater());
				}

			}
			if(Keyboard.isKeyDown(InputConfig.translationUp) || Keyboard.isKeyDown(InputConfig.translationDown)){
				float[] up = new float[4];
				uptodatem4.getRow(1, up);
				Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
				if(Keyboard.isKeyDown(InputConfig.translationUp)){
					delta.add(upVector);
				}
				if(Keyboard.isKeyDown(InputConfig.translationDown)){
					delta.add(upVector.negater());
				}
			}
			if(!delta.isZero()){ //moved
				delta.normalize();
				delta.scale(speed);
				updating.origin.add(delta);
			}
		}else{
			Transform tcam = getTransform().updating();
			
			Transform tfollow = getFollowing().getTransform().updating();
			
			Vector3f camPos = new Vector3f();
			Vector3f viewRay = getViewRay(RenderState.updating()).copy();
			viewRay.normalize();
			viewRay.scale(viewRadius);
			//viewRay.add(getUpVector(RenderState.updating()).copy().scl(-3));
			camPos.add(tfollow.origin, viewRay.negater());
			tcam.origin.set(camPos);
			
			//setPosition(new Vector3f(tfollow.origin).copy().add(viewRay.getNegate()));
		}

    }
    
    public Matrix4f getOpenGLView(int state){
    	return getTransform(state).getOpenGLViewMatrix();
    }
    
	public void setFollowing(Entity e){
		following = e;
	}
	
	public boolean isFollowing(){
		if(following != null)
			return true;
		return false;
	}
	
	public Entity getFollowing(){
		return following;
	}
	
	public void setType(CamType type){
		this.type = type;
	}

}
