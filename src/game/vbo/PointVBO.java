package game.vbo;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import utils.BoundingAxis;
import utils.BoundingSphere;

public class PointVBO extends ModelVBO{
	
	public PointVBO() {
		vertices = BufferUtils.createFloatBuffer(3);
		vertices.put(new float[]
				{0, 0, 0});
		vertices.rewind();
	}
	
	@Override
	public void glDraw() {
		glDrawArrays(GL_POINTS, 0, 1);
	}

}
