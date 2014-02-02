package controller;

import math.Vector3fc;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Camera{

    //Camera config
    private float mouseSensitivity = 0.05f;
    private float movementSpeed = 50.0f; //move 50 units per second
    private float shiftBoost = 10f; //x times faster
	
    private Vector3fc pos = new Vector3fc();
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private float roll = 0.0f;
    
    private Vector3fc viewRay = new Vector3fc(0,0,-1); //Vector which points at the direction your'e looking at
    private Vector3fc upVector = new Vector3fc(0,1,0); //Points up
    private Vector3fc rightVector = new Vector3fc(1,0,0); //Cross product of viewRay and upVector
    
    //Vector pointing at the direction of axis
    private Vector3fc xAxis = new Vector3fc(1,0,0);
    private Vector3fc yAxis = new Vector3fc(0,1,0);
    private Vector3fc zAxis = new Vector3fc(0,0,1);
    
    public Camera copy(){
    	Camera cam = new Camera();
    	cam.setPos(pos.copy());
    	cam.setPitch(pitch);
    	cam.setYaw(yaw);
    	cam.setRoll(roll);
    	cam.setViewRay(viewRay);
    	cam.setUpVector(upVector);
    	cam.setRightVector(rightVector);
    	return cam;
    }
    
    public void setViewRay(Vector3fc v){
    	viewRay = v;
    }
    
    public void setUpVector(Vector3fc v){
    	upVector = v;
    }
    
    public void setRightVector(Vector3fc v){
    	rightVector = v;
    }
    
    public void setPos(Vector3fc v){
    	pos = v;
    }
    
    public Camera(){}
   
    
    public Camera(float x, float y, float z){
    	pos = new Vector3fc(x, y, z);
    }
    
    public void update(float dt){
    	boolean cameraRotated = checkInput(dt);
    	if(cameraRotated)
    		updateCamVectors();
    }
    
    private void updateCamVectors(){
		Vector4f vecPosMod = new Vector4f(zAxis.x, zAxis.y, -zAxis.z, 1.0f); //set view ray to z axis
		//rotation around x axis
		Matrix4f transMat = new Matrix4f();
		transMat.rotate((float)(Math.toRadians(-pitch)), xAxis);
		Matrix4f.transform(transMat,  vecPosMod,  vecPosMod);
		//rotation around y axis
		transMat = new Matrix4f();
		transMat.rotate((float)(Math.toRadians(-yaw)), yAxis);
		Matrix4f.transform(transMat,  vecPosMod,  vecPosMod);
		viewRay = new Vector3fc(vecPosMod.x, vecPosMod.y, vecPosMod.z);
		
		vecPosMod = new Vector4f(yAxis.x, yAxis.y, yAxis.z, 1.0f); //set up vector y axis
		//rotation around x axis
		transMat = new Matrix4f();
		transMat.rotate((float)Math.toRadians(-pitch), xAxis);
		Matrix4f.transform(transMat,  vecPosMod,  vecPosMod);
		//rotation around y axis
		transMat = new Matrix4f();
		transMat.rotate((float)Math.toRadians(-yaw), yAxis);
		Matrix4f.transform(transMat,  vecPosMod,  vecPosMod);
		upVector = new Vector3fc(vecPosMod.x, vecPosMod.y, vecPosMod.z);

        Vector3f.cross(viewRay, upVector, rightVector);
    }
    
    private boolean checkInput(float dt){
    	boolean cameraRotated = false;
    	float dx = Mouse.getDX();
    	float dy = Mouse.getDY();
    	if(dx != 0 || dy != 0){
    		cameraRotated = true;
            if(Math.cos(Math.toRadians(pitch)) < 0)
            	dx = -1*dx;
            //Mouse controls
            yaw += dx * mouseSensitivity;
            pitch += -dy * mouseSensitivity;
    	}
    	 //Keyboard controls
    	float boost = 1;
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
        	boost = shiftBoost;
        if (Keyboard.isKeyDown(Keyboard.KEY_W))
            walkForward(movementSpeed*dt*boost);
        if (Keyboard.isKeyDown(Keyboard.KEY_S))
            walkBackwards(movementSpeed*dt*boost);
        if (Keyboard.isKeyDown(Keyboard.KEY_A))
        	strafeLeft(movementSpeed*dt*boost);
        if (Keyboard.isKeyDown(Keyboard.KEY_D))
            strafeRight(movementSpeed*dt*boost);
        return cameraRotated;
    }
    
    public void lookAt(){
    	//Position; Point the camera is looking at looking at; Vector pointing upwards
    	GLU.gluLookAt(pos.x, pos.y, pos.z, pos.x+viewRay.x, pos.y+viewRay.y, pos.z+viewRay.z, upVector.x, upVector.y, upVector.z);
    }
   
    public void walkForward(float distance){
    	//walk in the direction of viewray
    	Vector3f.add(pos, viewRay.getMultiply(distance), pos);
    }
     
    public void walkBackwards(float distance){
    	Vector3f.add(pos, viewRay.getMultiply(-distance), pos);
    }
     
    public void strafeLeft(float distance){
    	Vector3f.add(pos, rightVector.getMultiply(-distance), pos);
    }
     
    public void strafeRight(float distance){
    	Vector3f.add(pos, rightVector.getMultiply(distance), pos);
    }
    
    //GET
    public Vector3fc getPos(){
    	return pos;
    }
    
    public Vector3fc getUpVector(){
    	return upVector;
    }
    
    public Vector3fc getRightVector(){
    	return rightVector;
    }
    
    public Vector3fc getViewRay(){
    	return viewRay;
    }
    
    public float getPitch(){
    	return pitch;
    }
    
    public float getYaw(){
    	return yaw;
    }
    
    public float getRoll(){
    	return roll;
    }
    
    public void setPitch(float p){
    	pitch = p;
    }
    
    public void setYaw(float p){
    	yaw = p;
    }
    
    public void setRoll(float p){
    	roll = p;
    }
    
}
