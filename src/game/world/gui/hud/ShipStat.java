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
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import game.threading.RenderThread;
import game.world.entities.Player;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import blender.model.Texture;

public class ShipStat extends HudComponent {
	private boolean textured = false;
	private float x;
	private float y;
	private int hull;
	private int fuel;
	private Player player;
	FloatBuffer Vertices = BufferUtils.createFloatBuffer(2 * 8);
	
	public ShipStat(Player player){
		this.player = player;
		
		position = new Vector2f(0,487);
		width = 205;
		height = 113;
		x = 0.304F;
		y = 0.195F;
		hull = 121;
		fuel = 121;
		
		
		vertices = BufferUtils.createFloatBuffer(2 * 12); //(x,y)*(4 vertices on a rectangle)
		float[] vertex = {
			0,height, width,height, width,0, 0,0,
			width*x,height*y+5,width*x+hull,height*y+5,width*x+hull,height*y,width*x,height*y,
			width*x,height*y+22,width*x+fuel,height*y+22,width*x+fuel,height*y+17,width*x,height*y+17
			
		};
		vertices.put(vertex);
		vertices.rewind();
		
		isTextured = true;
	}
	
	public void setTexture(){
		texVertices = BufferUtils.createFloatBuffer(2 * 12);
		float[] texturea = {
			RenderThread.spritesheet.getBottomLeftCoordNormal(27)[0],
			RenderThread.spritesheet.getBottomLeftCoordNormal(27)[1],
			RenderThread.spritesheet.getBottomRightCoordNormal(30)[0],
			RenderThread.spritesheet.getBottomRightCoordNormal(30)[1],
			RenderThread.spritesheet.getUpRightCoordNormal(10)[0],
			RenderThread.spritesheet.getUpRightCoordNormal(10)[1],
			RenderThread.spritesheet.getUpLeftCoordNormal(7)[0],
			RenderThread.spritesheet.getUpLeftCoordNormal(7)[1],	
			
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[1],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[0],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[1],
			
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[1],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[0],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[1]
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
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferSubData(GL_ARRAY_BUFFER,32,Vertices);//1 v‰‰rtus = 4 bitti. 
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDrawArrays(GL_QUADS, 0, 24);
		glDisable(GL_BLEND);
	}

	@Override
	public void update() {
		float[] vertex = {
			width*x,height*y+5,width*x+hull*(player.getFuel()/100F),height*y+5,width*x+hull*(player.getFuel()/100F),height*y,width*x,height*y,
			width*x,height*y+22,width*x+fuel*(player.getFuel()/100F),height*y+22,width*x+fuel*(player.getFuel()/100F),height*y+17,width*x,height*y+17
			
		};
		Vertices.put(vertex);
		Vertices.rewind();
		
	}
}
