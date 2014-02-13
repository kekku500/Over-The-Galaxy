package game.vbo;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import game.world.World;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Action;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import utils.BoundingAxis;
import utils.BoundingSphere;

public abstract class ModelVBO{
	
	protected int vboVertexID;
	protected FloatBuffer vertices;
	protected Transform initialMotion;
	
	public void render(){
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(3, GL_FLOAT, 0, 0);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    glDraw();
	    glDisableClientState(GL_VERTEX_ARRAY);
	}
	
	protected abstract void glDraw();
	
	public void dispose(){
	    glDeleteBuffers(vboVertexID);
	}
	
	public void prepareVBO() {
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public int getVBOVertexID(){
		return vboVertexID;
	}
	
	public void setInitialMotion(Transform t){
		initialMotion = t;
	}

}
