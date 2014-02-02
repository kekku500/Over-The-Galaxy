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

import java.util.Arrays;

import math.BoundingAxis;
import math.BoundingSphere;
import math.Vector3fc;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Line extends AbstractEntity{
	
	public float length;
	
	public int lineWidth = 1;
	
	public Vector3fc offSet = new Vector3fc();
	

	
	public Line(){}

	public Line(Vector3fc startPos, Vector3fc endPos){
		pos = startPos;
		
		//calculate length
		Vector3f vec = new Vector3f(endPos.x-startPos.x, endPos.y-startPos.y, endPos.z-startPos.z);
		length = vec.length();
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(3 * 2);
		vertices.put(new float[]
				{0, 0, 0,
				endPos.x-startPos.x, endPos.y-startPos.y, endPos.z-startPos.z});
		vertices.rewind();
		offSet = new Vector3fc(endPos.x-startPos.x, endPos.y-startPos.y, endPos.z-startPos.z);

		toCenter = new Vector3fc((endPos.x-startPos.x)/2f, (endPos.y-startPos.y)/2f, (endPos.z-startPos.z)/2f);
		
		radius = toCenter.length();
	}
	
	@Override
	public BoundingAxis getBoundingAxis() {
		if(!sleeping){
			Vector3fc pos = getPos();
			Vector3fc vectorToCenter = toCenter;

			Vector3fc rotatedToCenter = Vector3fc.rotateVector(vectorToCenter, pitch, yaw, roll);
			Vector3fc newPos = pos.getAdd(vectorToCenter).getAdd(rotatedToCenter.getNegate());
			Vector3fc newPos2 = pos.getAdd(vectorToCenter).getAdd(rotatedToCenter);

			boundingAxis = new BoundingAxis(newPos, newPos2);
		}
		return boundingAxis;
	}

	@Override
	public BoundingSphere getBoundingSphere(){
		if(!sleeping){
			Vector3fc midPos = pos.getAdd(toCenter);
			boundingSphere =  new BoundingSphere(midPos, radius);
		}
		return boundingSphere;
	}
	
	@Override
	public void renderDraw(){
		GL11.glLineWidth(lineWidth);
		glDrawArrays(GL_LINES, 0, 2);
		GL11.glLineWidth(1);
	}
	
	public float length(){
		return length;
	}
	
	public void setLineWidth(int w){
		lineWidth = w;
	}

	@Override
	public Vector3fc getPosToMid() {
		return toCenter;
	}
	
	@Override
	public Entity copy(){
		Line l = new Line();
		l.setPos(pos.copy());
		l.length = length;
		l.lineWidth = lineWidth;
		l.offSet = offSet.copy();
		l.boundingSphere = boundingSphere;
		l.boundingAxis = boundingAxis;
		l.toCenter = toCenter;
		l.radius = radius;
		
		return copy2(l);
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
