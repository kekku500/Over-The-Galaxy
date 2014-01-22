package controller;

import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import controller.Controller2;

public class Controller {
	
	private static Vector3f position = new Vector3f(0,0,0);
	private static Vector3f rotation = new Vector3f(0,0,0);
	private static long lastFrame;
	private Controller2 control;
	
	private static long getTime(){
		return(Sys.getTime()* 1000) / Sys.getTimerResolution();
	}
	
	private static int getDelta(){
		long currentTime = getTime();
		int delta = (int)(currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}
	public Controller(){
		
		try{
			Display.setDisplayMode(new DisplayMode(640,480));
			Display.setTitle("Controller");
			Display.create();
		} catch(LWJGLException e){
			e.printStackTrace();
		}
		
		control = new Controller2(position, rotation);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective((float) 30, 640f / 480f, 0.001f, 100);
		glMatrixMode(GL_MODELVIEW);
		
		Point[] points = new Point[10000];
		Random random = new Random();
		
		for(int i = 0; i < points.length; i++){
			points[i] = new Point((random.nextFloat() - 0.5f)* 100f,
								  (random.nextFloat() - 0.5f)* 100f,
								  random.nextInt(200) - 200);
		}
		
		
		while(!Display.isCloseRequested()){
			int delta = getDelta();
			
			boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
			boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
			boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
			boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
			boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		    boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		    boolean moveFaster = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
		    boolean moveSlower = Keyboard.isKeyDown(Keyboard.KEY_TAB);
		    float mouseDX = Mouse.getDX();
		    float mouseDY = Mouse.getDY();
			
		    
		    control.Control(keyUp, keyDown, keyLeft, keyRight, flyUp, flyDown, moveFaster, moveSlower,
		    		mouseDX, mouseDY, delta);
			position = control.position;
			rotation = control.rotation;				
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glRotatef(rotation.x,1,0,0);
			glRotatef(rotation.y,0,1,0);
			glRotatef(rotation.z,0,0,1);
			glTranslatef(position.x,position.y,position.z);
			
			glBegin(GL_POINTS);
			for(Point p: points){
				glVertex3f(p.x,p.y,p.z);
			}
			glEnd();
									
			Display.update();
			Display.sync(60);			
		}
		Display.destroy();
		System.exit(0);
	}
	
	private static class Point{
		float x, y, z;
		
		public Point(float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	public static void main(String[] args){
		new Controller();
	}
	
}

