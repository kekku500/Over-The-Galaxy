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

import test.OBJloader.Face;
import test.OBJloader.Model;
import test.OBJloader.OBJLoader;
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
		
		int objectDisplayList = glGenLists(1);
		glNewList(objectDisplayList, GL_COMPILE);
		{
			Model m = null;
			try{
				m = OBJLoader.loadModel(new File("C:/Users/Marko/Documents/GitHub/Over-The-Galaxy/src/mees.obj"));
			}catch(FileNotFoundException e){
				e.printStackTrace();
				Display.destroy();
				System.exit(1);
			}catch(IOException e){
				e.printStackTrace();
				Display.destroy();
				System.exit(1);
			}
			glBegin(GL_TRIANGLES);
			for(Face face : m.faces){
				System.out.println(face.normal.y);
				Vector3f n1 = m.normals.get((int) face.normal.x -1);
				glNormal3f(n1.x,n1.y,n1.z);
				Vector3f v1 = m.vertices.get((int)face.vertex.x -1);
				glVertex3f(v1.x,v1.y,v1.z);
				Vector3f n2 = m.normals.get((int) face.normal.y -1);
				glNormal3f(n2.x,n2.y,n2.z);
				Vector3f v2 = m.vertices.get((int)face.vertex.y -1);
				glVertex3f(v2.x,v2.y,v2.z);
				Vector3f n3 = m.normals.get((int) face.normal.z -1);
				glNormal3f(n3.x,n3.y,n3.z);
				Vector3f v3 = m.vertices.get((int)face.vertex.z -1);
				glVertex3f(v3.x,v3.y,v3.z);
			}
			glEnd();
			
		}
		glEndList();
		
		while(!Display.isCloseRequested()){
			int delta = getDelta();			
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			
			glCallList(objectDisplayList);
			
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
	
	
	public static void main(String[] args){
		new Controller();
	}
	
}

