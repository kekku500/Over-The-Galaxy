package game.world.entities;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import game.vbotemplates.AbstractVBO;
import game.vbotemplates.CuboidVBO;
import game.world.World;
import game.world.entities.AbstractEntity;
import game.world.entities.Entity;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import utils.BoundingAxis;
import utils.BoundingSphere;

import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Cuboid extends AbstractEntity{
	
	public Cuboid(){}
	
	public Cuboid(Vector3f pos, float w, float h, float d){
		modelShape = new CuboidVBO(w,h,d);
		motionState.set(new Matrix4f(
				new Quat4f(0,0,0,1), 
				pos, 1.0f));
		calcBoundingSphere();
	}
	
	@Override
	public void lastUpdate(float dt) {
	}
	
	@Override
	public void calcBoundingSphere(){
		Vector3f midPos = motionState.origin;
		midPos = new Vector3f(midPos.x, midPos.y, midPos.z);
		boundingSphere = new BoundingSphere(midPos, getRadius());
	}

	@Override
	public Entity copy(){
		Cuboid newCube = new Cuboid();
		newCube.boundingSphere = boundingSphere;
		newCube.boundingAxis = boundingAxis;
		
		return copy2(newCube);
	}
	
	public float getWidth(){
		CuboidVBO c = (CuboidVBO)modelShape;
		return c.getWidth();
	}
	
	public float getHeight(){
		CuboidVBO c = (CuboidVBO)modelShape;
		return c.getHeight();
	}
	
	public float getDepth(){
		CuboidVBO c = (CuboidVBO)modelShape;
		return c.getDepth();
	}
	
	public float getRadius(){
		CuboidVBO c = (CuboidVBO)modelShape;
		return c.getRadius();
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
	public Vector3f getPosToMid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void calcBoundingAxis() {
	}
	
}
