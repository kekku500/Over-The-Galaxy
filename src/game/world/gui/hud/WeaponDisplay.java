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
import game.Game;
import game.world.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;



import blender.model.Texture;

public class WeaponDisplay extends HudComponent{
	String texPath;

	public WeaponDisplay(String texPath){
		
		position = new Vector2f(50,0);
		width = 100;
		height = 50;
		this.texPath = texPath;
		
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		vertices.put(new float[]{0,height, width,height, width,0, 0,0});
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

	@Override
	public void renderDraw() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDrawArrays(GL_QUADS, 0, 4);
		World.graphics2D.drawString(100, 50, "DEFAULT" + 50);
		glDisable(GL_BLEND);
		
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
