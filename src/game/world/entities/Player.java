package game.world.entities;

import game.resources.Resources;
import game.world.World;
import game.world.input.Input;
import game.world.input.InputListener;

import javax.vecmath.Quat4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import utils.R;
import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import blender.model.Model;
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

/**
 * This is temporary.
 */

public class Player extends DynamicEntity implements Input{
	
	private float movementSpeed = 20; //Pixels per second
	private float rotationSpeed = 90; //degrees per second
	
	
	public Player(){}

	public Player(float x, float y, float z) {
		new InputListener(this); //to be able to register input
		//super(new Vector3f(x,y,z), 5, 5, 15);
		Model model2 = null;
		try {
			model2 = Resources.getModel("F-35_Lightning_II\\F-35_Lightning_II.obj");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Quat4f quat = new Quat4f();
		QuaternionUtil.setRotation(quat, new Vector3f(1,0,0), Utils.rads(-90));
		Quat4f quat2 = new Quat4f();
		QuaternionUtil.setRotation(quat2, new Vector3f(0,0,1), Utils.rads(180));
		quat.mul(quat2); 
		quat.normalize();
		Transform t = new Transform(new Matrix4f(
				quat,
				new Vector3f(0,0,0), 1.0f));
		
		this.rotate(t);
		
		//setDynamic();
		
		createBody(model2);
		/*CollisionShape testShape = model2.getConvexHull();
		
		//physics
		//CollisionShape testShape = new BoxShape(new Vector3f(12/2, 3/2, 15/2));
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
		//testBody = new RigidBody(testConstructionInfo);
		createRigidBody(testConstructionInfo);*/
		getBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		//controlBall.setLinearVelocity(new Vector3f(0,0,0));
		//testBody.setCollisionFlags(CollisionFlags.KINEMATIC_OBJECT);
		//setDynamic();
		//rigidShape = testBody;	
	}
	
	
	@Override
	public Entity getLinked(){
		return new Player().setLink(this);
	}
	
	@Override
	public Entity setLink(Entity t) {
		super.setLink(t);
		if(t instanceof Player){
			Player ve = (Player)t;
			
			shootBoxes = ve.shootBoxes;
		}

		return this;
	}
	
	boolean applyForce = false;
	private enum Rotate{LEFT, RIGHT, NONE;}
	private enum Turn{UP, DOWN, NONE;}
	private Turn turn = Turn.NONE;
	private Rotate rotate = Rotate.NONE;
	boolean createNewShape = false;
	boolean resetControlBall = false;
	R<Boolean> shootBoxes = new R<Boolean>(false);
	boolean switchMotion = false;
	
	public void checkInput(int a){

	}
	
	@Override
	public void update(float dt){
		//rigidShape.setGravity(new Vector3f(0,0,0));
		/*if(Mouse.isButtonDown(0)){
			applyForce = true;
		}else{
			applyForce = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			rotate = Rotate.RIGHT;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			rotate = Rotate.LEFT;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			turn = Turn.UP;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			turn = Turn.DOWN;
		}*/
		Vector3f cam = getWorld().getCamera().getPosition();
		Vector3f cameraPosition = new Vector3f(cam.x , cam.y, cam.z);
		Vector3f viewRay = getWorld().getCamera().getViewRay();
		float changeby = 40*dt;
		/*if(turn != Turn.NONE){
			Transform t = new Transform();
			getRigidBody().getWorldTransform(t);
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			if(turn == Turn.UP)
				changeby *= -1;
			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3f(1,0,0), Utils.rads(changeby));
			orientation.mul(qRotatedy);
			
			t.setRotation(orientation);
			getRigidBody().setWorldTransform(t);
			turn = Turn.NONE;
		}
		changeby = 40*dt;
		if(rotate != Rotate.NONE){
			Transform t = new Transform();
			getRigidBody().getWorldTransform(t);
			
			Quat4f orientation = new Quat4f();
			t.getRotation(orientation);
			if(rotate == Rotate.RIGHT)
				changeby *= -1;

			
			Quat4f qRotatedy = new Quat4f();
			QuaternionUtil.setRotation(qRotatedy, new Vector3f(0,1,0), Utils.rads(changeby));
			orientation.mul(qRotatedy);
			
			t.setRotation(orientation);
			getRigidBody().setWorldTransform(t);
			rotate = Rotate.NONE;
		}
		if(applyForce){
			Transform t = new Transform();
			getRigidBody().getMotionState().getWorldTransform(t);
			Matrix4f m = new Matrix4f();
			t.getMatrix(m);
			m.invert();
			float[] r3 = new float[4];
			m.getRow(2, r3);
			Vector3f aim = new Vector3f(r3[0], r3[1], r3[2]);
			Vector3f getGoodPos = new Vector3f();
			
			getGoodPos.sub(aim);
			getGoodPos.scale(7.5f);
			getRigidBody().activate(true);
			//force strength
			aim.scale(50);
			getRigidBody().applyForce(aim, getGoodPos);
		}*/
		if(createNewShape){
			/*OldEntity testObject = new OldDefaultEntity();
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
			//RigidBody body = new RigidBody(constructionInfo);
			//setDynamic();
			
			testObject.createBody(testModel, constructionInfo);

			//testObject.createRigidBody(constructionInfo);
			//testObject.setRigidBody(body);
			getWorld().addEntity(testObject);
			createNewShape = false;*/
		}
		if(shootBoxes.get()){
			float w = 2+(float)Math.random()*5, h = 2+(float)Math.random()*5, d = 2+(float)Math.random()*5;
			float impluseForce = 200;
			
			DynamicEntity box = new DynamicEntity();
			box.setPosition(cameraPosition.copy().add(viewRay.copy().mul((float)Math.sqrt(w+h+d))));
			box.scale(w, h, d);
			try {
				box.createBody(Resources.getModel("common\\cuboid.obj"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			box.getBody().activate();
			box.getBody().applyCentralImpulse(viewRay.copy().scaleGet(impluseForce));
			
			getWorld().addEntity(box);
			
			shootBoxes.set(false);
		}
		
		super.update(dt);
	}
	
	public float getMovementSpeed(){
		return movementSpeed;
	}
	
	public float getRotationSpeed(){
		return rotationSpeed;
	}

	@Override
	public void checkKeyboardInput(int k) {
		switch(k){
		case Keyboard.KEY_G:
			createNewShape = true; break;
		case Keyboard.KEY_F:
			resetControlBall = true; break;
		case Keyboard.KEY_E:
			shootBoxes.set(true); break;
		case Keyboard.KEY_X:
			switchMotion = true; break;
		}
	}

	@Override
	public void checkMouseInput(int m) {
		// TODO Auto-generated method stub
		
	}

}
