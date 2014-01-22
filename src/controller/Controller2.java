package controller;



import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Controller2{
	public Vector3f position;
	public Vector3f rotation;
	private static int walkingSpeed = 10;
	private static int mouseSpeed = 2;
	private static final int maxLookUp = 85;
	private static final int maxLookDown = -85;
	
	public Controller2(Vector3f position, Vector3f rotation){
		this.position = position;
		this.rotation = rotation;
	}
	
	public void Control(boolean keyUp, boolean keyDown, boolean keyLeft, boolean keyRight, 
			boolean flyUp, boolean flyDown, boolean moveFaster, boolean moveSlower,float mouseDX, 
			float mouseDY, int delta){
		
		if (Mouse.isGrabbed()) {
	        float mouseDXx = mouseDX * mouseSpeed * 0.16f;
	        float mouseDYy = mouseDY * mouseSpeed * 0.16f;
	        if (rotation.y + mouseDXx >= 360) {
	            rotation.y = rotation.y + mouseDXx - 360;
	        } else if (rotation.y + mouseDXx < 0) {
	            rotation.y = 360 - rotation.y + mouseDXx;
	        } else {
	            rotation.y += mouseDXx;
	        }
	        if (rotation.x - mouseDYy >= maxLookDown && rotation.x - mouseDYy <= maxLookUp) {
	            rotation.x += -mouseDYy;
	        } else if (rotation.x - mouseDYy < maxLookDown) {
	            rotation.x = maxLookDown;
	        } else if (rotation.x - mouseDYy > maxLookUp) {
	            rotation.x = maxLookUp;
	        }
	    }
	    
	    if (moveFaster && !moveSlower) {
	        walkingSpeed *= 4f;
	    }
	    if (moveSlower && !moveFaster) {
	        walkingSpeed /= 10f;
	    }
	
	    if (keyUp && keyRight && !keyLeft && !keyDown) {
	        float angle = rotation.y + 45;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = (walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (keyUp && keyLeft && !keyRight && !keyDown) {
	        float angle = rotation.y - 45;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = (walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (keyUp && !keyLeft && !keyRight && !keyDown) {
	        float angle = rotation.y;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = (walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (keyDown && keyLeft && !keyRight && !keyUp) {
	        float angle = rotation.y - 135;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = (walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (keyDown && keyRight && !keyLeft && !keyUp) {
	        float angle = rotation.y + 135;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = (walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (keyDown && !keyUp && !keyLeft && !keyRight) {
	        float angle = rotation.y;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = -(walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (keyLeft && !keyRight && !keyUp && !keyDown) {
	        float angle = rotation.y - 90;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = (walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (keyRight && !keyLeft && !keyUp && !keyDown) {
	        float angle = rotation.y + 90;
	        Vector3f newPosition = new Vector3f(position);
	        float hypotenuse = (walkingSpeed * 0.0002f) * delta;
	        float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
	        float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
	        newPosition.z += adjacent;
	        newPosition.x -= opposite;
	        position.z = newPosition.z;
	        position.x = newPosition.x;
	    }
	    if (flyUp && !flyDown) {
	        double newPositionY = (walkingSpeed * 0.0002) * delta;
	        position.y -= newPositionY;
	    }
	    if (flyDown && !flyUp) {
	        double newPositionY = (walkingSpeed * 0.0002) * delta;
	        position.y += newPositionY;
	    }
	    if (moveFaster && !moveSlower) {
	        walkingSpeed /= 4f;
	    }
	    if (moveSlower && !moveFaster) {
	        walkingSpeed *= 10f;
	    }
	    while (Mouse.next()) {
	        if (Mouse.isButtonDown(0)) {
	            Mouse.setGrabbed(true);
	        }
	        if (Mouse.isButtonDown(1)) {
	            Mouse.setGrabbed(false);
	        }
	    }
	    while (Keyboard.next()) {
	        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
	            position = new Vector3f(0, 0, 0);
	            rotation = new Vector3f(0, 0, 0);
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
	            mouseSpeed += 1;
	            System.out.println("Mouse speed changed to " + mouseSpeed + ".");
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
	            if (mouseSpeed - 1 > 0) {
	                mouseSpeed -= 1;
	                System.out.println("Mouse speed changed to " + mouseSpeed + ".");
	            }
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
	            System.out.println("Walking speed changed to " + walkingSpeed + ".");
	            walkingSpeed += 1;
	        }
	        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
	            System.out.println("Walking speed changed to " + walkingSpeed + ".");
	            walkingSpeed -= 1;
	        }
	    }
	}
}
