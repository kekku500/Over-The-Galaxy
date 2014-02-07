package game.vbotemplates;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

public class CuboidVBO extends AbstractVBO{
	
	public float w, h, d;
	public float radius;
	
	public CuboidVBO(float w, float h, float d){
		this.w = w;
		this.h = h;
		this.d = d;
		
		float wh = w/2;
		float hh = h/2;
		float dh = d/2;
		
		vertices = BufferUtils.createFloatBuffer(3 * 6 * 4); //(x,y,z)*(4 vertices on a side)*(6 sides)
		vertices.put(new float[]
				{
				-wh,-hh,-dh,	-wh,hh,-dh,	wh,hh,-dh,	wh,-hh,-dh,	
				wh,-hh,dh,		wh,hh,dh,	-wh,hh,dh,	-wh,-hh,dh,
				
				-wh,-hh,-dh,	-wh,-hh,dh,	-wh,hh,dh,	-wh,hh,-dh,
				wh,hh,-dh,	wh,hh,dh,	wh,-hh,dh,	wh,-hh,-dh,
				
				-wh,hh,-dh,	-wh,hh,dh,	wh,hh,dh,	wh,hh,-dh,
				wh,-hh,-dh,	wh,-hh,dh,	-wh,-hh,dh,	-wh,-hh,-dh,

				});
		vertices.rewind();
		
		Vector3f radiusVector = new Vector3f(w/2, h/2, d/2);
		radius = radiusVector.length();
	}
	
	@Override
	public void glDraw(){
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
	
	public float getRadius(){
		return radius;
	}

}
