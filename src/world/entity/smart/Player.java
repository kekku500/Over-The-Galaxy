package world.entity.smart;

import input.Input;
import input.InputConfig;
import input.InputListener;

import javax.vecmath.Quat4f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import resources.Resources;
import resources.model.Model;
import resources.model.custom.Sphere;
import utils.R;
import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import weapon.Weapon;
import world.World;
import world.entity.Entity;
import world.entity.dumb.DynamicEntity;

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

public class Player extends AbstractMoveableEntity implements Input{
	
	private static Weapon weapon;
	private static int fuel = 100;
	
	public Player(){}

	public Player(float x, float y, float z) {
		setPosition(x, y, z);
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
		
		
		createBody(model2);

		getBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);
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
			
			//shootBoxes = ve.shootBoxes;
		}

		return this;
	}
	
	boolean shootBoxes = false;
	
	public void checkInput(int a){

	}
	
	@Override
	public void update(float dt){
		if(Keyboard.isKeyDown(InputConfig.playerAccelerate)){
			accelerate(100);
		}
		if(Keyboard.isKeyDown(InputConfig.playerRotateRight)){
			rotateRight(200);
		}else if(Keyboard.isKeyDown(InputConfig.playerRotateLeft)){
			rotateLeft(100);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_I)){
			rotateUp(200);
		}
		Vector3f cam = getWorld().getController().getPosition();
		Vector3f cameraPosition = new Vector3f(cam.x , cam.y, cam.z);
		Vector3f viewRay = getWorld().getController().getViewRay();

		if(shootBoxes){
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
			
			shootBoxes = false;
		}
		
		super.update(dt);
	}

	@Override
	public void checkKeyboardInput(int k) {
		if(k == Keyboard.KEY_E)
			shootBoxes = true;
	}

	@Override
	public void checkMouseInput(int m) {
		// TODO Auto-generated method stub
		
	}
	
	public Weapon getWeapon(){
		return weapon;
	}

	public int getFuel(){
		return fuel;
	}

}
