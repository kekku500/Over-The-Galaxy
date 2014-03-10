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
	boolean resetControlBall = false;
	boolean shootBoxes = false;
	boolean switchMotion = false;
	
	public void checkInput(int a){
		switch(a){
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
		Vector3f cam = getWorld().getCamera().getPos();
		Vector3f cameraPosition = new Vector3f(cam.x , cam.y, cam.z);
		Vector3f viewRay = getWorld().getCamera().getViewRay();
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
