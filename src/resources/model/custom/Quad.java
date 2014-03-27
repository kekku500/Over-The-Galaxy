package resources.model.custom;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import resources.model.Model;


public class Quad extends Model{
	
	protected int vboVertexID;
	private int vboNormalID;
	protected FloatBuffer vertices;
	protected FloatBuffer normals;
	
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
		normals = BufferUtils.createFloatBuffer(3 * 4);
		normals.put(new float[]
				{0, 1, 0,
				0,1, 0,
				0, 1, 0,
				0, 1, 0,});
		normals.rewind();
	}

	public void renderSubModels() {
		glColor4f(color[0],color[1],color[2],color[3]);
		
        // Bind the normal buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
        glNormalPointer(GL_FLOAT, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);

		glDrawArrays(GL_QUADS, 0, 4);
		
	    glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
		glColor4f(1,1,1,1);
	}


	public void prepareVBO() {
		vboVertexID = glGenBuffers();
		vboNormalID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);	
		
		glBindBuffer(GL_ARRAY_BUFFER, vboNormalID);
		glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);	
	}


	public void dispose() {
		glDeleteBuffers(vboVertexID);
	}
	
	public void setColor(float[] f){
		color = f;
	}

}
