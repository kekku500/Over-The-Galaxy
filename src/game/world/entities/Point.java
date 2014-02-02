package game.world.entities;

import static org.lwjgl.opengl.GL11.*;
import math.BoundingAxis;
import math.BoundingSphere;
import math.Vector3fc;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class Point extends AbstractEntity{
	
	public Point(){}
	
	@Override
	public Entity copy(){
		Point newPoint = new Point();
		newPoint.boundingSphere = boundingSphere;

		return copy2(newPoint);
	}

	public Point(Vector3fc pos) {
		this.pos = pos;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(3 * 2);
		vertices.put(new float[]
				{0, 0, 0});
		vertices.rewind();
	}
	
	@Override
	public Vector3fc getToMidPoint(){
		return new Vector3fc();
	}
	
	@Override
	public BoundingAxis getBoundingAxis() {
		return null;
	}
	
	@Override
	public BoundingSphere getBoundingSphere(){
		if(!sleeping){
			Vector3f pos = getPos();
			boundingSphere = new BoundingSphere(pos, 0);
		}
		return boundingSphere;

	}

	@Override
	public void renderDraw() {
		glDrawArrays(GL_POINTS, 0, 1);
	}

	@Override
	public Vector3fc getPosToMid() {
		return new Vector3fc();
	}

	@Override
	public void firstUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void midUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lastUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}
}
