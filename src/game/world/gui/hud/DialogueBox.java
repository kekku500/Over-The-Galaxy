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
	private String texture1 = "res/models/hud/Dialogue box.png";
	public DialogueBox(){
		position = new Vector2f(200,200);
		width = 250;
		height = 187;
		
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		vertices.put(new float[]{0,height, width,height, width,0, 0,0});
		vertices.rewind();
		
		isTextured = true;
		
		texVertices = BufferUtils.createFloatBuffer(2 * 4);
		texVertices.put(new float[]{0,1, 1,1, 1,0, 0,0});
		texVertices.rewind();
	}
	//Kirjutada meetod mis vtab vastu teate, töötleb seda ja siis paneb esitlus listi.
	@Override
	public void renderInitStart() {
		Texture tex = Texture.loadTexture(texture1);
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
