package game.world.entities;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import game.vbotemplates.CuboidVBO;
import game.vbotemplates.LineVBO;
import game.world.entities.AbstractEntity;
import game.world.entities.Entity;

import java.util.Arrays;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import utils.BoundingAxis;
import utils.BoundingSphere;

public class Line extends AbstractEntity{
	
	public float length;
	
	public Line(){}
	
	public Line(Vector3f startPos, Vector3f endPos){
		Vector3f center = new Vector3f();
		center.add(startPos, endPos);
		center.scale(.5f);
		Vector3f arrow = new Vector3f();
		arrow.add(endPos);
		arrow.sub(startPos);
		modelShape = new LineVBO(arrow);
		motionState.set(new Matrix4f(new Quat4f(0,0,0,1),center, 1.0f));
		calcBoundingSphere();
	}
	

	@Override
	public Vector3f getPosToMid() {
		return null;
	}

	@Override
	public Entity copy() {
		Line l = new Line();
		l.length = length;
		return copy2(l);
	}

	@Override
	public void firstUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRender() {
		// TODO Auto-generated method stub
	}

	@Override
	public void endRender() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void calcBoundingSphere() {
		Vector3f midPos = motionState.origin;
		midPos = new Vector3f(midPos.x, midPos.y, midPos.z);
		boundingSphere = new BoundingSphere(midPos, getRadius());
	}
	
	public void setLineWidth(int w){
		if(modelShape != null){
			if(modelShape instanceof LineVBO){
				LineVBO l = (LineVBO)modelShape;
				l.setLineWidth(w);
			}
		}

	}
	
	public float getRadius(){
		LineVBO c = (LineVBO)modelShape;
		return c.getRadius();
	}
	
	public void setLength(float l){
		length = l;
	}
	
	public float length(){
		return length;
	}


	@Override
	public void lastUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calcBoundingAxis() {
		// TODO Auto-generated method stub
		
	}
	
}
