package game.world.entities;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class Point extends AbstractEntity{

	public Point(Vector3f pos) {
		super(pos);
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(3 * 2);
		vertices.put(new float[]
				{0, 0, 0});
		vertices.rewind();
	}

	@Override
	public void render2() {
		glDrawArrays(GL_POINTS, 0, 1);
	}

}
