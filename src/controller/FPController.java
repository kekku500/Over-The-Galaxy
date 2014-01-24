package controller;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class FPController {
	
    //3d vector to store the camera's position in
    private Vector3f    position    = null;
    //the rotation around the Y axis of the camera
    private float       yaw         = 0.0f;
    //the rotation around the X axis of the camera
    private float       pitch       = 0.0f;
    
    //the rotation around the Z axis of the camera
    private float       roll       = 0.0f;
    
    private float mouseSensitivity = 0.05f;
    private float movementSpeed = 50.0f; //move 50 units per second
    
    public FPController(float x, float y, float z){
        position = new Vector3f(x, y, z);
    }
    
    public void update(float dt){
        //controll camera yaw from x movement fromt the mouse
        yaw(Mouse.getDX() * mouseSensitivity);
        //controll camera pitch from y movement fromt the mouse
        pitch(-Mouse.getDY() * mouseSensitivity);
        
        //Speedo mode shift pro
        int boost = 1;
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
        	boost = 10;
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
        {
            walkForward(movementSpeed*dt*boost);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
        {
            walkBackwards(movementSpeed*dt*boost);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left
        {
        	strafeLeft(movementSpeed*dt*boost);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right
        {
            strafeRight(movementSpeed*dt*boost);
        }
    }
    
    //translates and rotate the matrix so that it looks through the camera
    //this dose basic what gluLookAt() does
    public void render(){
    	//GL11.glRotatef(roll, 0.0f,0.0f,1.0f);
        //roatate the pitch around the X axis
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //roatate the yaw around the Z axis
        //translate to the position vector's location
        GL11.glTranslatef(-position.x, -position.y, -position.z);
    }
    
    public Vector3f getPos(){
    	return position;
    }
    
    //increment the camera's current yaw rotation
    public void yaw(float amount){
        //increment the yaw by the amount param
        yaw += amount;
    }
     
    //increment the camera's current yaw rotation
    public void pitch(float amount){
        //increment the pitch by the amount param
        pitch += amount;
    }
    
    //increment the camera's current yaw rotation
    public void roll(float amount){
        //increment the pitch by the amount param
        roll += amount;
    }
    
    public float getPitch(){
    	return pitch;
    }
    
  //moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance){
        position.x += distance * (float)Math.sin(Math.toRadians(yaw));
        position.z -= distance * (float)Math.cos(Math.toRadians(yaw));
        position.y += distance * (float)Math.cos(Math.toRadians(90+pitch));
    }
     
    //moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance){
        position.x -= distance * (float)Math.sin(Math.toRadians(yaw));
        position.z += distance * (float)Math.cos(Math.toRadians(yaw));
        position.y -= distance * (float)Math.cos(Math.toRadians(90+pitch));
    }
     
    //strafes the camera left relitive to its current rotation (yaw)
    public void strafeLeft(float distance){
        position.x += distance * (float)Math.sin(Math.toRadians(yaw-90));
        position.z -= distance * (float)Math.cos(Math.toRadians(yaw-90)); 
    }
     
    //strafes the camera right relitive to its current rotation (yaw)
    public void strafeRight(float distance){
        position.x += distance * (float)Math.sin(Math.toRadians(yaw+90));
        position.z -= distance * (float)Math.cos(Math.toRadians(yaw+90));
    }
    


}
