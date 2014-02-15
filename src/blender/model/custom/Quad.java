package blender.model.custom;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import blender.model.Model;


public class Quad extends Model{
	
	protected int vboVertexID;
	protected FloatBuffer vertices;
	
	private float[] color = new float[4];
	
	public Quad(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4){
		color = new float[]{1,1,1,1};
		
		vertices = BufferUtils.createFloatBuffer(3 * 4);
		vertices.put(new float[]
				{p1.x, p1.y, p1.z,
				p2.x, p2.y, p2.z,
				p3.x, p3.y, p3.z,
				p4.x, p4.y, p4.z,});
		vertices.rewind();
	}

	public void renderDraw() {
		glColor4f(color[0],color[1],color[2],color[3]);
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(3, GL_FLOAT, 0, 0);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    
		glDrawArrays(GL_QUADS, 0, 4);
		
	    glDisableClientState(GL_VERTEX_ARRAY);
		glColor4f(1,1,1,1);
	}


	public void prepareVBO() {
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);	
	}


	public void dispose() {
		glDeleteBuffers(vboVertexID);
	}
	
	public void setColor(float[] f){
		color = f;
	}

}
