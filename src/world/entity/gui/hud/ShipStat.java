package world.entity.gui.hud;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import state.Game;
import threading.RenderThread;
import world.entity.Entity;
import world.entity.gui.AbstractComponent;
import world.entity.gui.HudExample;
import world.entity.smart.Player;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import resources.texture.Texture;

public class ShipStat extends AbstractComponent {
	private static float x;
	private static float y;
	private static int hull;
	private static int fuel;
	private static Player player;
	private static int vboVertexID;
	private static int vboTexVertexID;
	private static int width;
	private static int height;
	private static FloatBuffer Vertices = BufferUtils.createFloatBuffer(2 * 8);
	
	public ShipStat(Player player){
		this.player = player;
		
		width = 250;
		height = 138;
		x = 0.304F;
		y = 0.195F;
		hull = 148;
		fuel = 148;
		setPosition(0,Game.height-height);
		
	}
		
	@Override
	public void render() {
		glPushMatrix(); //save current transformations
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Vector2f pos = getPosition();		
		glTranslatef(pos.x, pos.y, 0);
		
        glEnableClientState(GL_VERTEX_ARRAY);
//		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferSubData(GL_ARRAY_BUFFER,32,Vertices);//1 v‰‰rtus = 4 bitti.
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(2, GL_FLOAT, 0, 0);
		
		glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
        glTexCoordPointer(2, GL_FLOAT, 0, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, RenderThread.spritesheet.getTex().getID());

		glDrawArrays(GL_QUADS, 0, 24);
		
		glDisableClientState(GL_VERTEX_ARRAY);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, 0);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		
		glPopMatrix(); //reset transformations
	}
	
	@Override
	public void update(float dt) {
		float[] vertex = {
				width*x,height*y+6,width*x+hull*(player.getFuel()/100F),height*y+6,width*x+hull*(player.getFuel()/100F),height*y,width*x,height*y,
				width*x,height*y+27,width*x+fuel*(player.getFuel()/100F),height*y+27,width*x+fuel*(player.getFuel()/100F),height*y+21,width*x,height*y+21
		
		};
		Vertices.put(vertex);
		Vertices.rewind();
	
	}
	
	public static void dispose(){
	    glDeleteBuffers(vboVertexID);
	}

	public static void init() {
		FloatBuffer vb = BufferUtils.createFloatBuffer(2 * 12);
		vb.put(new float[]{	
				0,0,0,height, width,height, width,0,
				width*x,height*y,width*x,height*y+6,width*x+hull,height*y+6,width*x+hull,height*y,
				width*x,height*y+21,width*x,height*y+27,width*x+fuel,height*y+27,width*x+fuel,height*y+21}); //clockwise, front face
		vb.rewind();
		
		FloatBuffer texVertices = BufferUtils.createFloatBuffer(2 * 12);
		float[] texturea = {
		RenderThread.spritesheet.getUpLeftCoordNormal(7)[0],
		RenderThread.spritesheet.getUpLeftCoordNormal(7)[1],
		RenderThread.spritesheet.getBottomLeftCoordNormal(27)[0],
		RenderThread.spritesheet.getBottomLeftCoordNormal(27)[1],
		RenderThread.spritesheet.getBottomRightCoordNormal(30)[0],
		RenderThread.spritesheet.getBottomRightCoordNormal(30)[1],
		RenderThread.spritesheet.getUpRightCoordNormal(10)[0],
		RenderThread.spritesheet.getUpRightCoordNormal(10)[1],
	
		RenderThread.spritesheet.getUpLeftCoordNormal(51)[0],
		RenderThread.spritesheet.getUpLeftCoordNormal(51)[1],
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
		RenderThread.spritesheet.getUpRightCoordNormal(51)[1]

		};
		texVertices.put(texturea);
		texVertices.rewind();
		
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vb, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		vboTexVertexID = glGenBuffers();		
		glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
		glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
	}
	
	@Override
	public Entity setLink(Entity t) {
		if(t instanceof ShipStat){
			ShipStat wt = (ShipStat)t;			
		}
		return this;
	}
	

	@Override
	public Entity getLinked() {
	//	return new HudComponent(getPosition().x, getPosition().y).setLink(this);
		return new ShipStat(player).setLink(this);
	}

}
