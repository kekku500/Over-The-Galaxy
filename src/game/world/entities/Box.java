package game.world.entities;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class Box extends AbstractEntity{
	
	private float w, h, d;

		/**
		 * @param pos Position is set at the most negative spot (coordinates) of the box.
		 * @param w Size in x coordinates. (width)
		 * @param h Size in y coordinates. (height)
		 * @param d Size in z coordinates. (depth)
		 */
	public Box(Vector3f pos, float w, float h, float d){
		super(pos);

		this.w = w;
		this.h = h;
		this.d = d;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(3 * 6 * 4); //(x,y,z)*(4 vertices on a side)*(6 sides)
		vertices.put(new float[]
				{0,0,0,	w,0,0,	w,h,0,	0,h,0,
				0,0,d,	w,0,d,	w,h,d,	0,h,d,
				0,0,0,	0,0,d,	0,h,d,	0,h,0,
				w,0,0,	w,0,d,	w,h,d,	w,h,0,
				0,0,0,	w,0,0,	w,0,d,	0,0,d,
				0,h,0,	w,h,0,	w,h,d,	0,h,d,
				});
		vertices.rewind();
	}
	
	@Override
	public void render2(){
		glDrawArrays(GL_QUADS, 0, 6 * 4);
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
	
}
