package game.world.entities;

import game.world.World;

import javax.vecmath.Quat4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import blender.model.Model;
import blender.model.custom.Cuboid;
import blender.model.custom.Sphere;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

/**
 * This is temporary.
 */

public class Player extends DefaultEntity{
	
	private float movementSpeed = 20; //Pixels per second
	private float rotationSpeed = 90; //degrees per second
	
	
	public Player(){}

	public Player(float x, float y, float z) {
		//super(new Vector3f(x,y,z), 5, 5, 15);
		try {
			Model model2 = new Model("F-35_Lightning_II\\F-35_Lightning_II.obj");
			Quat4f quat = new Quat4f();
			QuaternionUtil.setRotation(quat, new Vector3f(1,0,0), Utils.rads(-90));
			Quat4f quat2 = new Quat4f();
			QuaternionUtil.setRotation(quat2, new Vector3f(0,0,1), Utils.rads(180));
			quat.mul(quat2); 
			quat.normalize();
			Transform t = new Transform(new Matrix4f(
					quat,
					new Vector3f(0,0,0), 1.0f));
			Matrix4f ne = new Matrix4f();
			t.getMatrix(ne);
			model2.setOffset(t);
			setModel(model2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//physics
		CollisionShape testShape = new BoxShape(new Vector3f(12/2, 3/2, 15/2));
		MotionState testMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(x,y,z), 1.0f)));
		Vector3f ballInertia = new Vector3f(0,0,0);
		testShape.calculateLocalInertia(2.5f, ballInertia);
		RigidBodyConstructionInfo testConstructionInfo = new RigidBodyConstructionInfo(2.5f, testMotionState, testShape, ballInertia);
		testConstructionInfo.restitution = 0.5f;
		testConstructionInfo.angularDamping = 0.95f;
		testConstructionInfo.friction = 0.95f;
		RigidBody testBody;
		testBody = new RigidBody(testConstructionInfo);
		testBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		//controlBall.setLinearVelocity(new Vector3f(0,0,0));
		//testBody.setCollisionFlags(CollisionFlags.KINEMATIC_OBJECT);
		setDynamic();
		rigidShape = testBody;	
	}
	
	@Override
	public Entity copy(){
		Player newCube = new Player();
		return copy2(newCube);
	}
	
	boolean applyForce = false;
	private enum Rotate{LEFT, RIGHT, NONE;}
	private enum Turn{UP, DOWN, NONE;}
	private Turn turn = Turn.NONE;
	private Rotate rotate = Rotate.NONE;
	boolean createNewShape = false;
	boolean resetControlBall = false;
	boolean shootBoxes = false;
	boolean switchMotion = false;
	
	public void checkInput(int a){
		switch(a){
		case Keyboard.KEY_G:
			createNewShape = true; break;
		case Keyboard.KEY_F:
			resetControlBall = true; break;
		case Keyboard.KEY_E:
			shootBoxes = true; break;
		case Keyboard.KEY_X:
			switchMotion = true; break;
		}
	}
	
	@Override
	public void firstUpdate(float dt){
		//rigidShape.setGravity(new Vector3f(0,0,0));
		if(Mouse.isButtonDown(0)){
			applyForce = true;
		}else{
			applyForce = false;
		}
		/*if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			rotate = Rotate.RIGHT;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			rotate = Rotate.LEFT;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			turn = Turn.UP;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			turn = Turn.DOWN;
		}*/
		Vector3f cam = getWorld().getCamera().getPos();
		Vector3f cameraPosition = new Vector3f(cam.x , cam.y, cam.z);
		Vector3f viewRay = getWorld().getCamera().getViewRay();
		/*float changeby = 40*dt;
		if(turn != Turn.NONE){
			Transform t = new Transform();
			rigidShape.getWorldTransform(t);
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			if(turn == Turn.UP)
				changeby *= -1;
			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3f(1,0,0), Utils.rads(changeby));
			orientation.mul(qRotatedy);
			
			t.setRotation(orientation);
			rigidShape.setWorldTransform(t);
			turn = Turn.NONE;
		}
		changeby = 40*dt;
		if(rotate != Rotate.NONE){
			Transform t = new Transform();
			rigidShape.getWorldTransform(t);
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			if(rotate == Rotate.RIGHT)
				changeby *= -1;

			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3f(0,1,0), Utils.rads(changeby));
			orientation.mul(qRotatedy);
			
			t.setRotation(orientation);
			rigidShape.setWorldTransform(t);
			rotate = Rotate.NONE;
		}
		if(applyForce){
			Transform t = new Transform();
			rigidShape.getMotionState().getWorldTransform(t);
			Matrix4f m = new Matrix4f();
			t.getMatrix(m);
			m.invert();
			float[] r3 = new float[4];
			m.getRow(2, r3);
			Vector3f aim = new Vector3f(r3[0], r3[1], r3[2]);
			Vector3f getGoodPos = new Vector3f();
			
			getGoodPos.sub(aim);
			getGoodPos.scale(7.5f);
			rigidShape.activate(true);
			//force strength
			aim.scale(50);
			rigidShape.applyForce(aim, getGoodPos);
		}*/
		if(createNewShape){
			Entity testObject = new DefaultEntity();
			//visual
			Model testModel = new Sphere(3.0f,30,30);
			testObject.setModel(testModel);
			
			CollisionShape shape = new SphereShape(3.0f);
			DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(
					new Quat4f(0,0,0,1),
					new Vector3f(cameraPosition.x,35,cameraPosition.z), 1)));
			Vector3f intertia = new Vector3f();
			shape.calculateLocalInertia(1.0f,  intertia);
			RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0f, motionState, shape, intertia);
			constructionInfo.restitution = 0.75f;
			constructionInfo.angularDamping = 0.95f;
			RigidBody body = new RigidBody(constructionInfo);
			setDynamic();
			testObject.setRigidBody(body);
			getWorld().addEntity(testObject);
			createNewShape = false;
		}
		if(shootBoxes){
			float w = 5, h = 5, d = 5;
			float I = 2f;
			float impluseForce = 20;
			Entity testObject = new DefaultEntity();
			//visual
			Model testModel = new Cuboid(w,h,d);
			testObject.setModel(testModel);
			
			CollisionShape shape = new BoxShape(new Vector3f(w/2, h/2, d/2));
			DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(
					new Quat4f(0,0,0,1),
					new Vector3f(cameraPosition.x+viewRay.x*10,cameraPosition.y+viewRay.y*10,cameraPosition.z+viewRay.z*10), 1)));
			Vector3f intertia = new Vector3f();
			shape.calculateLocalInertia(I,  intertia);
			RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(I, motionState, shape, intertia);
			constructionInfo.restitution = 0.1f;
			constructionInfo.friction = 0.95f;
			RigidBody body = new RigidBody(constructionInfo);
			setDynamic();
			body.activate();
			body.applyCentralImpulse(new Vector3f(viewRay.x*impluseForce, viewRay.y*impluseForce, viewRay.z*impluseForce));
			
			testObject.setRigidBody(body);
			getWorld().addEntity(testObject);
			shootBoxes = false;
		}
	}
	
	public float getMovementSpeed(){
		return movementSpeed;
	}
	
	public float getRotationSpeed(){
		return rotationSpeed;
	}

}
