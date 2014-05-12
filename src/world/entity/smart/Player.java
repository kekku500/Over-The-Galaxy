package world.entity.smart;

import input.InputReciever;
import input.InputConfig;

import javax.vecmath.Quat4f;

import org.lwjgl.input.Keyboard;

import resources.Resources;
import resources.model.Model;
import state.RenderState;
import utils.R;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import weapon.Laser;
import weapon.Weapon;
import world.EntityManager;
import world.entity.create.DynamicEntity;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.QuaternionUtil;

/**
 * This is temporary.
 */

public class Player extends AbstractMoveableEntity implements InputReciever{
	

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
		if(Keyboard.isKeyDown(InputConfig.playerAccelerate)){
			accelerate(1000);
		}
		if(Keyboard.isKeyDown(InputConfig.playerRotateRight)){
			rotateRight(2000);
		}else if(Keyboard.isKeyDown(InputConfig.playerRotateLeft)){
			rotateLeft(1000);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_I)){
			rotateUp(2000);
		}
		Vector3f cam = getEntityManager().getState().getCamera().getPosition(RenderState.getUpdatingId());
		Vector3f cameraPosition = new Vector3f(cam.x , cam.y, cam.z);
		Vector3f viewRay = getEntityManager().getState().getCamera().getViewRay(RenderState.updating());

		if(shootBoxes){
			float w = 2+(float)Math.random()*5, h = 2+(float)Math.random()*5, d = 2+(float)Math.random()*5;
			float impluseForce = 200;
			
			DynamicEntity box = new DynamicEntity(getEntityManager());
			box.setPosition(cameraPosition.copy().add(viewRay.copy().scl((float)Math.sqrt(w+h+d))));
			box.scale(w, h, d);
			try {
				box.createBody(Resources.getModel("common\\cuboid.obj"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			box.getBody().activate();
			box.getBody().applyCentralImpulse(viewRay.copy().scl(impluseForce));
			
			getEntityManager().addEntity(box);
			
			shootBoxes = false;
		}
		
		super.update(dt);
	}

	@Override
	public void checkKeyboardInput(int k) {
		switch(k){
		case Keyboard.KEY_E:
			shootBoxes = true; break;
		case Keyboard.KEY_P:
			referencedFuel.set(referencedFuel.get()-5); 
			break;
		}
	}

	@Override
	public void checkMouseInput(int m) {
		// TODO Auto-generated method stub
		
	}
	
	public float getFuel(){
		return referencedFuel.get();
	}
	
	public Weapon getWeapon(){
		return weapon.get();
	}
}
