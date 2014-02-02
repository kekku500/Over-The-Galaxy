package game.world.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

public class Rectangle extends AbstractComponent{
	
	public Rectangle(Vector2f pos, int w, int h){
		position = pos;
		width = w;
		height = h;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		vertices.put(new float[]{0,h, w,h, w,0, 0,0});
		vertices.rewind();
	}
	
	@Override
	public void renderDraw(){
		glDrawArrays(GL_QUADS, 0, 4);
	}

	@Override
	public void update() {}
	
}
