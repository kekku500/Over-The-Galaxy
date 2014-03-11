package game.world.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import blender.model.Texture;

public class Rectangle extends AbstractComponent{
	
	String texPath = "res/models/superman/CHRNPCICOHER101_DIFFUSE.png";
	
	public Rectangle(Vector2f pos, int w, int h){
		position = pos;
		width = w;
		height = h;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		vertices.put(new float[]{0,h, w,h, w,0, 0,0});
		vertices.rewind();
		
		//testing texture
		isTextured = true;
		
		if(isTextured){
			texVertices = BufferUtils.createFloatBuffer(2 * 4);
			texVertices.put(new float[]{0,1, 1,1, 1,0, 0,0});
			texVertices.rewind();
		}
		
	}
	
	@Override
	public void renderDraw(){
		glDrawArrays(GL_QUADS, 0, 4);
	}

	@Override
	public void update() {}

	@Override
	public void renderInitStart() {
		if(isTextured){
			Texture tex = Texture.loadTexture(texPath);
			texture = tex.id;
			vboTexVertexID = glGenBuffers();
			
            glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
            glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		
	}
	
}
