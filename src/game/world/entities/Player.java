package game.world.entities;

import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;
import static org.lwjgl.input.Keyboard.isKeyDown;
import game.RenderState;
import game.world.sync.Request.Action;
import game.world.sync.SyncManager;
import game.world.sync.UpdateRequest;
import math.Vector3fc;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

/**
 * This is temporary.
 */

public class Player extends Cuboid{
	
	private float movementSpeed = 20; //Pixels per second
	private float rotationSpeed = 90; //degrees per second
	
	public Player(){}

	public Player(float x, float y, float z) {
		super(new Vector3fc(x,y,z), 20, 35, 20);
		motion = Motion.PHYSICS;
	}
	
	@Override
	public Entity copy(){
		Player newCube = new Player();
		newCube.w = w;
		newCube.h = h;
		newCube.d = d;
		newCube.radius = radius;
		newCube.boundingSphere = boundingSphere;
		newCube.boundingAxis = boundingAxis;
		newCube.toCenter = toCenter;
		newCube.sleeping = sleeping;
		
		return copy2(newCube);
	}
	
	@Override
	public void firstUpdate(float dt){
		//TRANSLATION
		//System.out.println("here");
		boolean change = false;
		if(isKeyDown(KEY_RIGHT)){
			//System.out.println("MOVE DDAMN!!!! POS CHANGED IN somewhere");
			getPos().x -= movementSpeed*dt;
			change = true;
		}else if(isKeyDown(KEY_LEFT)){
			getPos().x += movementSpeed*dt;
			change = true;
		}
		if(isKeyDown(KEY_DOWN)){
			getPos().z -= movementSpeed*dt;
			change = true;
		}else if(isKeyDown(KEY_UP)){
			getPos().z += movementSpeed*dt;
			change = true;
		}
		//ROTATION
		if(isKeyDown(Keyboard.KEY_Y)){
			addYaw(getRotationSpeed()*dt);
			change = true;
		}else if(isKeyDown(Keyboard.KEY_H)){
			addYaw(-getRotationSpeed()*dt);
			change = true;
		}
		if(isKeyDown(Keyboard.KEY_U)){
			addPitch(getRotationSpeed()*dt);
			change = true;
		}else if(isKeyDown(Keyboard.KEY_J)){
			addPitch(-getRotationSpeed()*dt);
			change = true;
		}
		if(isKeyDown(Keyboard.KEY_I)){
			addRoll(getRotationSpeed()*dt);
			change = true;
		}else if(isKeyDown(Keyboard.KEY_K)){
			addRoll(-getRotationSpeed()*dt);
			change = true;
		}
		if(change){
			//System.out.println("Clicked");
			SyncManager sync = getWorld().getState().getSyncManager();
			sync.addCheck(new UpdateRequest(Action.UPDATEALL, this, 1));
		}
		//System.out.println("pos is" + pos + " at world " +  RenderState.updatingId);

		//System.out.println("done");
	}
	
	public float getMovementSpeed(){
		return movementSpeed;
	}
	
	public float getRotationSpeed(){
		return rotationSpeed;
	}

}
