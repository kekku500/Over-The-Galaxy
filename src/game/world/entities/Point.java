package game.world.entities;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import game.vbotemplates.CuboidVBO;
import game.vbotemplates.PointVBO;
import game.world.entities.AbstractEntity;
import game.world.entities.Entity;

import org.lwjgl.BufferUtils;

import utils.BoundingAxis;
import utils.BoundingSphere;

public class Point extends AbstractEntity{
	
	public Point(){}
	
	@Override
	public Entity copy(){
		Point newPoint = new Point();
		return copy2(newPoint);
	}

	public Point(Vector3f pos) {
		motionState.set(new Matrix4f(new Quat4f(0,0,0,1),pos, 1.0f));
		calcBoundingSphere();

	}
	
	@Override
	public void calcBoundingSphere(){
		boundingSphere = new BoundingSphere(motionState.origin, 0);
	}

	@Override
	public Vector3f getPosToMid() {
		// TODO Auto-generated method stub
		return null;
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
	public void lastUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calcBoundingAxis() {
		// TODO Auto-generated method stub
		
	}
	


}
