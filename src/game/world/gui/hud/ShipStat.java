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

public class ShipStat extends HudComponent {
	private int currentHP;
	private int currentFuel;
	private boolean textured = false;
	
	public ShipStat(int currentHP, int currentFuel){
		this.currentHP = currentHP;
		this.currentFuel = currentFuel;
		
		position = new Vector2f(0,0);
		width = 205;
		height = 113;
		
		vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		float[] vertex = {
			0,height, width,height, width,0, 0,0,						
		};
		vertices.put(vertex);
		vertices.rewind();
		
		isTextured = true;
		
	}
	
	public void setTexture(){
		texVertices = BufferUtils.createFloatBuffer(2 * 4);
		float[] texturea = {
			RenderThread.spritesheet.getBottomLeftCoordNormal(27)[0],
			RenderThread.spritesheet.getBottomLeftCoordNormal(27)[1],
			RenderThread.spritesheet.getBottomRightCoordNormal(30)[0],
			RenderThread.spritesheet.getBottomRightCoordNormal(30)[1],
			RenderThread.spritesheet.getUpRightCoordNormal(10)[0],
			RenderThread.spritesheet.getUpRightCoordNormal(10)[1],
			RenderThread.spritesheet.getUpLeftCoordNormal(7)[0],
			RenderThread.spritesheet.getUpLeftCoordNormal(7)[1],			
		};
		texVertices.put(texturea);
		texVertices.rewind();
		textured = true;
	}
	
	
	@Override
	public void renderInitStart() {
		if(!textured){
			setTexture();
		}
		Texture tex = RenderThread.spritesheet.getTex();
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
		glDrawArrays(GL_QUADS, 0, 8);
		glDisable(GL_BLEND);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
