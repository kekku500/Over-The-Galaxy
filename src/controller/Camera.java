package controller;

import game.Game;
import game.world.World;
import game.world.entities.DefaultEntity;
import game.world.entities.Entity;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

import utils.Utils;
import utils.math.Vector3;
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

public class Camera extends DefaultEntity{

    //Camera config
    private float mouseSensitivity = 0.05f;
    private float movementSpeed = 50.0f; //move 50 units per second
    private float shiftBoost = 10f; //x times faster
    public float pitch, yaw;
    private enum CamType{FP, _6DOF, LOCK}
    private CamType type = CamType.FP;
    
    private Entity following;
    private float viewRadius = 30;
    
	
    private Vector3 viewRay = new Vector3(0,0,1); //Vector which points at the direction your'e looking at
    private Vector3 upVector = new Vector3(0,1,0); //Points up
    private Vector3 rightVector = new Vector3(1,0,0); //Cross product of viewRay and upVector
    private Vector3 position = new Vector3();
    
	public Entity copy(){
		Camera cam = new Camera();
		
		return copy2(cam);
	}
	
	public void setFollowing(Entity e){
		following = e;
	}
    
    public Camera(){}
   
    World world;
    public Camera(float x, float y, float z, World w){
    	position = new Vector3(x, y, z);
    	world = w;
    }
    
    public void setImportant(Camera fromHere){
    	rigidShape = fromHere.getRigidBody();
    	modelShape = fromHere.getModel();
    }
    
    
    private float camRadius;
    public void createCamera(){
    	//for helpz
    	Vector3 up = new Vector3(0,1,0);
    	Vector3 ray = new Vector3(0,0,-1);
    	Vector3 right = new Vector3(1,0,0);
    	
    	float zNear = Game.zNear;
    	float fov = Game.fov;
    	float ratio = Game.width/Game.height;
    	
    	float Hnear = 2 * (float)Math.tan(Math.toRadians(fov/2))*zNear;
    	float Wnear = Hnear * ratio;
    	
    	camRadius = zNear;
    	
    	//Frustum vertices
    	/*Vector3 tip = new Vector3(0,0,0);
    	Vector3 nc = tip.getAdd(ray.getMultiply(zNear));
    	Vector3 ntl = nc.getAdd(up.getMultiply(Hnear/2)).getAdd(right.getMultiply(Wnear/2).getNegate());
    	Vector3 ntr = nc.getAdd(up.getMultiply(Hnear/2)).getAdd(right.getMultiply(Wnear/2));
    	Vector3 nbl = nc.getAdd(up.getMultiply(Hnear/2).getNegate()).getAdd(right.getMultiply(Wnear/2).getNegate());
    	Vector3 nbr = nc.getAdd(up.getMultiply(Hnear/2).getNegate()).getAdd(right.getMultiply(Wnear/2));

    	ObjectArrayList<Vector3f> vertices = new ObjectArrayList<Vector3f>();
    	vertices.add(tip);
    	vertices.add(ntl);
    	vertices.add(ntr);
    	vertices.add(nbr);
    	vertices.add(nbl);
    	CollisionShape shape = new ConvexHullShape(vertices);*/
		//AbstractVBO testModel = new CuboidVBO(5,5,15);
    	Model testModel = new Sphere(camRadius, 30, 30);
		setModel(testModel);
    	//ConvexShape shape = new SphereShape(camRadius);
    	ConvexShape shape = new SphereShape(camRadius);
		//CollisionShape shape = new BoxShape(new Vector3f(5/2, 5/2, 15/2));

		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				position, 1)));
		Vector3f intertia = new Vector3f();
		shape.calculateLocalInertia(0f,  intertia);
		RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0f, motionState, shape, intertia);
		constructionInfo.restitution = 1f;
		constructionInfo.angularDamping = 1f;
		constructionInfo.linearDamping = .999f;
		constructionInfo.mass = 0.0001f;
		RigidBody body = new RigidBody(constructionInfo);
		body.setAngularFactor(0);
		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		setRigidBody(body);
		
		body.setCollisionFlags(CollisionFlags.NO_CONTACT_RESPONSE);
		
		world.getDynamicsWorld().addRigidBody(body);
		body.setGravity(new Vector3f(0,0,0));
    }
    
    private void rotation(float dt){
		float dx = Mouse.getDX() * mouseSensitivity;
		float dy = Mouse.getDY() * mouseSensitivity;
		if(type == CamType._6DOF){
			Transform t = new Transform();
			rigidShape.getWorldTransform(t);
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3(1,0,0), Utils.rads(dy));
			qRotatedy.mul(orientation);
			orientation = qRotatedy;
			
			Quat4f qRotatedx = new Quat4f();
			QuaternionUtil.setRotation(qRotatedx, new Vector3(0,1,0), Utils.rads(dx));
			qRotatedx.mul(orientation); // vv
			orientation = qRotatedx; //  this makes it DOF6
			orientation.normalize();
			
			t.setRotation(orientation);
			rigidShape.setWorldTransform(t);
		}else if(type == CamType.FP){
			Transform t = new Transform();
			rigidShape.getWorldTransform(t);
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3(1,0,0), Utils.rads(dy));
			qRotatedy.mul(orientation);
			orientation = qRotatedy;
			
			Quat4f qRotatedx = new Quat4f();
			QuaternionUtil.setRotation(qRotatedx, new Vector3(0,1,0), Utils.rads(dx));
			orientation.mul(qRotatedx); //this line makes it fps
			orientation.normalize();
			
			t.setRotation(orientation);
			rigidShape.setWorldTransform(t);
		}else if(type == CamType.LOCK){
			if(following != null){
				Transform t = new Transform();
				following.getRigidBody().getWorldTransform(t);
				
				Quat4f orientation = new Quat4f();
				t.getRotation(orientation);
				
				orientation.conjugate();
				
				//Dont wanna look directly from behind
				Quat4f qRotatedy = new Quat4f();
				QuaternionUtil.setRotation(qRotatedy, new Vector3(1,0,0), Utils.rads(-25));
				qRotatedy.mul(orientation);
				orientation = qRotatedy;
				
				Transform t2 = new Transform();
				rigidShape.getWorldTransform(t2);
				t2.setRotation(orientation);
				rigidShape.setWorldTransform(t2);
			}
		}

		rigidShape.getMotionState().getWorldTransform(motionState); //update position
		Matrix4f viewMatrix = new Matrix4f();
		motionState.getMatrix(viewMatrix);
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		viewRay = new Vector3(ray[0], ray[1], ray[2]);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		upVector = new Vector3(up[0], up[1], up[2]);
		float[] right = new float[4];
		viewMatrix.getRow(0, right);
		rightVector = new Vector3(right[0], right[1],right[2]);
    }
    
    private void translation(float dt){
		float speed = movementSpeed*dt;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			speed *= shiftBoost;
		if(following == null){
			Transform tr = new Transform();
			rigidShape.getWorldTransform(tr);
			Matrix4f mr = new Matrix4f();
			tr.getMatrix(mr);
			Vector3 delta = new Vector3();
			if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_S)){
				float[] ray = new float[4];
				mr.getRow(2, ray);
				Vector3 viewRay = new Vector3(ray[0], ray[1], ray[2]);
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
				Vector3 rightVector = new Vector3(right[0], right[1], right[2]);
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
				Vector3 upVector = new Vector3(up[0], up[1], up[2]);
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
				rigidShape.setWorldTransform(tr);
				rigidShape.getWorldTransform(motionState);
			}
			
			position = new Vector3(motionState.origin.x, motionState.origin.y, motionState.origin.z);
		}else{
			Transform tcam = new Transform();
			rigidShape.getWorldTransform(tcam);
			
			Transform tfollow = new Transform();
			following.getRigidBody().getWorldTransform(tfollow);
			
			Vector3 camPos = new Vector3();
			viewRay.normalize();
			viewRay.scale(viewRadius);
			camPos.add(tfollow.origin, viewRay.getNegate());
			tcam.origin.set(camPos);
			
			rigidShape.setWorldTransform(tcam);
			
			position = new Vector3(tfollow.origin.x-viewRay.x, tfollow.origin.y-viewRay.y, tfollow.origin.z-viewRay.z);
		}

    }
    
    @Override
    public void update(float dt){
		rigidShape.activate();
		
		rotation(dt);
		
		translation(dt);
    }
    
    public Vector3 getPos(){
    	return position;
    }
    
    public void lookAt(){
    	//if(following == null)
    		GLU.gluLookAt(position.x, position.y, position.z, position.x+viewRay.x, position.y+viewRay.y, position.z+viewRay.z, upVector.x, upVector.y, upVector.z);	
    	//else
    	//	GLU.gluLookAt(position.x+viewRay.x, position.y+viewRay.y, position.z+viewRay.z, position.x, position.y, position.z, upVector.x, upVector.y, upVector.z);	
    	//if(modelShape.getVBOVertexID() == 0)
    	//	modelShape.prepareVBO();
    	//render();
    }
    
    public Vector3 getUpVector(){
    	return upVector;
    }
    
    public Vector3 getRightVector(){
    	return rightVector;
    }
    
    public Vector3 getViewRay(){
    	return viewRay;
    }
    
}
