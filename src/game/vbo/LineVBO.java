package game.vbo;

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

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

import utils.BoundingAxis;
import utils.BoundingSphere;

public class LineVBO extends ModelVBO{
	
	private Vector3f arrow;
	private float radius;
	private int lineWidth;
	
	public LineVBO(Vector3f arrow){
		this.arrow = arrow;
		
		radius = arrow.length()/2f;
		
		vertices = BufferUtils.createFloatBuffer(3 * 2);
		vertices.put(new float[]
				{0, 0, 0,
				arrow.x, arrow.y, arrow.z});
		vertices.rewind();
	}
	
	public Vector3f getArrow(){
		return arrow;
	}

	@Override
	protected void glDraw() {
		GL11.glLineWidth(lineWidth);
		glDrawArrays(GL_LINES, 0, 2);
		GL11.glLineWidth(1);
	}
	
	public void setLineWidth(int i){
		lineWidth = i;
	}
	
	public float getRadius(){
		return radius;
	}

	
}
