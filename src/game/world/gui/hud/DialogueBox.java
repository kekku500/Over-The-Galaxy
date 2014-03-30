package game.world.gui.hud;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import game.threading.RenderThread;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import blender.model.Texture;

public class DialogueBox extends HudComponent{
	public DialogueBox(){
		position = new Vector2f(500,413);
		width = 300;
		height = 187;
		
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		vertices.put(new float[]{0,height, width,height, width,0, 0,0});
		vertices.rewind();
		
		isTextured = true;
	}

	private void setTexture(){
		texVertices = BufferUtils.createFloatBuffer(2 * 4);
		texVertices.put(new float[]{
				RenderThread.spritesheet.getBottomLeftCoordNormal(81)[0],
				RenderThread.spritesheet.getBottomLeftCoordNormal(81)[1],
				RenderThread.spritesheet.getBottomRightCoordNormal(85)[0],
				RenderThread.spritesheet.getBottomRightCoordNormal(85)[1],
				RenderThread.spritesheet.getUpRightCoordNormal(65)[0],
				RenderThread.spritesheet.getUpRightCoordNormal(65)[1],
				RenderThread.spritesheet.getUpLeftCoordNormal(61)[0],
				RenderThread.spritesheet.getUpLeftCoordNormal(61)[1]
				});
		texVertices.rewind();
	}
	@Override
	public void renderInitStart() {
		setTexture();
		Texture tex =  RenderThread.spritesheet.getTex();
		texture = tex.id;
		vboTexVertexID = glGenBuffers();
			
        glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
        glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
		
	}

	@Override
	public void renderDraw() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDrawArrays(GL_QUADS, 0, 4);
		glDisable(GL_BLEND);
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
