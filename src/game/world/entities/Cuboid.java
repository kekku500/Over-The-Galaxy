package game.world.entities;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import game.world.World;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import math.BoundingAxis;
import math.Point;
import math.BoundingSphere;
import math.Vector3fc;

public class Cuboid extends AbstractEntity{
	
	public float w, h, d;
	
	public Cuboid() {}
	
	/**
	* @param pos Position is set at the most negative spot (coordinates) of the box.
	* @param w Size in x coordinates. (width)
	* @param h Size in y coordinates. (height)
	* @param d Size in z coordinates. (depth)
	*/
	public Cuboid(Vector3fc pos, float w, float h, float d){
		this.pos = pos;
		this.w = w;
		this.h = h;
		this.d = d;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(3 * 6 * 4); //(x,y,z)*(4 vertices on a side)*(6 sides)
		vertices.put(new float[]
				{0,h,0,	w,h,0,	w,0,0,	0,0,0,	
				0,0,d,	w,0,d,	w,h,d,	0,h,d,
				0,0,0,	0,0,d,	0,h,d,	0,h,0,
				w,h,0,	w,h,d,	w,0,d,	w,0,0,
				0,0,0,	w,0,0,	w,0,d,	0,0,d,
				0,h,d,	w,h,d,	w,h,0,	0,h,0,		
				});
		vertices.rewind();
		
		toCenter = new Vector3fc(w/2, h/2, d/2);
		radius = toCenter.length();
	}

	public void midUpdate(float dt){

	}

	@Override
	public void renderDraw(){
		glDrawArrays(GL_QUADS, 0, 6 * 4);
	}
	
	@Override
	public BoundingAxis getBoundingAxis() {
		if(!sleeping){
			Vector3fc pos = getPos();
			Vector3fc vectorToCenter = toCenter;
			
			Vector3fc height = Vector3fc.rotateVector(new Vector3fc(0,h,0), pitch, yaw, roll);
			Vector3fc width = Vector3fc.rotateVector(new Vector3fc(w,0,0), pitch, yaw, roll);
			Vector3fc depth = Vector3fc.rotateVector(new Vector3fc(0,0,d), pitch, yaw, roll);

			Vector3fc rotatedToCenter = Vector3fc.rotateVector(vectorToCenter, pitch, yaw, roll);
			Vector3fc newPos = pos.getAdd(vectorToCenter).getAdd(rotatedToCenter.getNegate());
			Vector3fc newPos2 = pos.getAdd(vectorToCenter).getAdd(rotatedToCenter);
			
			boundingAxis = new BoundingAxis(newPos, newPos2, newPos.getAdd(width), newPos.getAdd(depth), newPos.getAdd(height), 
					newPos2.getAdd(width.getNegate()),newPos2.getAdd(depth.getNegate()), newPos2.getAdd(height.getNegate()));
		}
		return boundingAxis;
	}
	
	@Override
	public BoundingSphere getBoundingSphere() {
		if(!sleeping){
			boundingSphere = new BoundingSphere(pos.getAdd(toCenter), radius);
		}
		return boundingSphere;

	}

	public float getWidth(){
		return w;
	}
	
	public float getHeight(){
		return h;
	}
	
	public float getDepth(){
		return d;
	}
	
	@Override
	public Vector3fc getPosToMid() {
		return toCenter;
	}
	
	@Override
	public Entity copy(){
		Cuboid newCube = new Cuboid();
		newCube.w = w;
		newCube.h = h;
		newCube.d = d;
		newCube.radius = radius;
		newCube.boundingSphere = boundingSphere;
		newCube.boundingAxis = boundingAxis;
		newCube.toCenter = toCenter;
		newCube.sleeping = sleeping;
		
		return copy2(newCube);
	}

	@Override
	public void firstUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lastUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}
	
}
