package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

import test.OBJloader.HUD;
import test.OBJloader.Model;
import test.OBJloader.OBJLoader;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import controller.Controller2;

public class Controller {
	
	private static Vector3f position = new Vector3f(0,0,0);
	private static Vector3f rotation = new Vector3f(0,0,0);
	private static long lastFrame;
	private Controller2 control;
	private HUD hud;
	
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
		Model m = null;
		hud = new HUD();
		try{
			m = OBJLoader.loadModel("src/mees.obj");
		}catch(FileNotFoundException e){
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}catch(IOException e){
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
		make3D();

		while(!Display.isCloseRequested()){
			int delta = getDelta();			
			
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			m.Render();
			make2D();
			//hud.render();
			make3D();
			
			glLoadIdentity();
			glRotatef(rotation.x,1,0,0);
			glRotatef(rotation.y,0,1,0);
			glRotatef(rotation.z,0,0,1);
			glTranslatef(position.x,position.y,position.z);
			
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
			
			
									
			Display.update();
			Display.sync(60);			
		}
		Display.destroy();
		System.exit(0);
	}
	
	protected static void make2D(){
		glEnable(GL_BLEND);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 640,0, 480,-1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	protected static void make3D(){
		glDisable(GL_BLEND);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective((float) 30, 640f / 480f, 0.001f, 100);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	
	public static void main(String[] args){
		new Controller();
	}
	
}

