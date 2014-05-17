package entity.creation;

import input.InputConfig;
import input.InputHandler;
import input.KbEvent;
import input.MsEvent;

import javax.vecmath.Quat4f;

import main.Config;
import main.state.RenderState;
import math.Matrix4f;
import math.Transform;
import math.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import resources.Resources;
import resources.model.Model;
import utils.R;
import utils.Utils;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.QuaternionUtil;

import entity.blueprint.AbstractMoveableEntity;
import entity.support.Laser;
import entity.support.Weapon;
import entitymanager.EntityManager;

/**
 * This is temporary.
 */

public class Player extends AbstractMoveableEntity implements InputHandler{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private R<Float> referencedFuel = new R<Float>(100f);
	private R<Weapon> weapon = new R<Weapon>();
	
	
	public Player(EntityManager world){
		super(world);
	}

	public Player(EntityManager world, float x, float y, float z) {
		super(world);
		setPosition(x, y, z);
		Model model2 = null;
		weapon.set(new Laser());
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
		
		
		createBody(model2);
		
		/*Model cuboid = null;
		try {
			cuboid = Resources.getModel("common\\cuboid.obj");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		setPosition(-100, 10, 0);
		scale(10,10,10);
		createBody(cuboid);*/

		getBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);
	}
	
	boolean shootBoxes = false;
	
	public void checkInput(int a){

	}
	
	@Override
	public void update(float dt){
		//System.out.println("updating " + this);
		int dx = getEntityManager().getState().getInput().getMouseDX();
		int dy = getEntityManager().getState().getInput().getMouseDY();
		if(!Mouse.isGrabbed()){
			dx = dy = 0;
		}
		getBody().activate();
		float mouseSen = 600;
		float accelSpeed = 20000;
		float rotateSpeed = 15000;
		float shiftBoost = 10;
		if(dy != 0)
			rotateUp(dy * dt * mouseSen);
		if(dx != 0)
			rotateRight(dx * dt * mouseSen);
		if(Keyboard.isKeyDown(InputConfig.playerAccelerate)){
			accelerate(dt * accelSpeed * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? shiftBoost : 1));
		}
		if(Keyboard.isKeyDown(InputConfig.playerMoveRight)){
			moveRight(accelSpeed * dt / 3 * 2 * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? shiftBoost : 1));
		}else if(Keyboard.isKeyDown(InputConfig.playerMoveLeft)){
			moveLeft(accelSpeed * dt / 3 * 2 * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? shiftBoost : 1));
		}
		if(Keyboard.isKeyDown(InputConfig.playerRollLeft)){
			rollLeft(rotateSpeed * dt);
		}else if(Keyboard.isKeyDown(InputConfig.playerRollRight)){
			rollRight(rotateSpeed * dt);
		}
		
		float ba = .95f;
		Vector3f angVel = new Vector3f();
		getBody().getAngularVelocity(angVel);
		getBody().setAngularVelocity(angVel.scl(ba));
		
		float bl = .97f;
		Vector3f linVel = new Vector3f();
		getBody().getLinearVelocity(linVel);
		getBody().setLinearVelocity(linVel.scl(bl));

		super.update(dt);
	}
	
	public float getFuel(){
		return referencedFuel.get();
	}
	
	public Weapon getWeapon(){
		return weapon.get();
	}

	@Override
	public void handleKey(KbEvent event) {
		if(event.state){
			int k = event.key;
			switch(k){
			//case Keyboard.KEY_E:
			//	shootBoxes = true; break;
			case Keyboard.KEY_P:
				referencedFuel.set(referencedFuel.get()-5); 
				break;
			}
		}

		
	}

	@Override
	public void handleMouse(MsEvent event) {
		// TODO Auto-generated method stub
		
	}
}
