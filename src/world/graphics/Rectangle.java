package world.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import resources.texture.Texture;
import world.World;

public class Rectangle{
	
	private static int vboVertexID;
	private static int vboTexVertexID;
	
	public static void init(){
		FloatBuffer vb = BufferUtils.createFloatBuffer(2 * 4);
		vb.put(new float[]{0,0, 0,1, 1,1, 1,0}); //clockwise, front face
		vb.rewind();
		
		FloatBuffer tb = BufferUtils.createFloatBuffer(2 * 4);
		tb.put(new float[]{0,0, 0,1, 1,1, 1,0});
		tb.rewind();
		
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vb, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		vboTexVertexID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
        glBufferData(GL_ARRAY_BUFFER, tb, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public static void render(int...texturesID){
		Graphics2D.drawVBO(4, vboVertexID, vboTexVertexID, texturesID);
	}
	
	public static void dispose(){
	    glDeleteBuffers(vboVertexID);
		glDeleteBuffers(vboTexVertexID);
	}
	
}
