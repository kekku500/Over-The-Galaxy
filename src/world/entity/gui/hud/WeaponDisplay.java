package world.entity.gui.hud;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
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
import world.entity.smart.Player;
import world.graphics.BufferSubData;
import world.graphics.Graphics2D;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import weapon.Weapon;
import resources.texture.Texture;

public class WeaponDisplay extends AbstractComponent{
	private static int vboVertexID;
	private static int vboTexVertexID;
	
	private Weapon weapon;
	private Player player;
	
	private static float x = 0.15F;
	private static float y = 0.457F;
	private static float z = 0.769F;
	
	private int id;
	private static int durability = 86;
	
	private static int width = 205;
	private static int height = 113;
	BufferSubData verticesChange = new BufferSubData(BufferUtils.createFloatBuffer(2 * 4),0).setOffsetByFloat(56);
	BufferSubData textureChange =  new BufferSubData(BufferUtils.createFloatBuffer(2 * 4),0).setOffsetByFloat(56);

	public WeaponDisplay(Player player){
		this.player = player;
		this.weapon = player.getWeapon();	
		setPosition(0,0);
	}

	@Override
	public void render() {
		int textureid = RenderThread.spritesheet.getTex().getID();
		Vector2f pos = getPosition();
		
		glColor3f(1,1,1);
		
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Graphics2D.drawVBO(20, vboVertexID, verticesChange, vboTexVertexID, textureChange, textureid);
		Graphics2D.drawString(88, 40, weapon.getClipAmount() + "/" + weapon.getMaxClips());
		Graphics2D.drawString(70, 55, weapon.getAmmo() + "/" + weapon.getMaxAmmo());
		
		glDisable(GL_BLEND);
		
		glPopMatrix();
	}

	@Override
	public void update(float dt) {
		float[] vertex = {
				width*y, height*z+4, width*y+durability,height*z+4, width*y+durability,height*z, width*y,height*z
		};
		verticesChange.put(vertex);
		this.weapon = player.getWeapon();
		if(id != weapon.getID()){
			float[] texture = {
				RenderThread.spritesheet.getBottomLeftCoordNormal(weapon.getTexture().x)[0],
				RenderThread.spritesheet.getBottomLeftCoordNormal(weapon.getTexture().x)[1],
				RenderThread.spritesheet.getBottomRightCoordNormal(weapon.getTexture().y)[0],
				RenderThread.spritesheet.getBottomRightCoordNormal(weapon.getTexture().y)[1],
				RenderThread.spritesheet.getUpRightCoordNormal(weapon.getTexture().y)[0],
				RenderThread.spritesheet.getUpRightCoordNormal(weapon.getTexture().y)[1],
				RenderThread.spritesheet.getUpLeftCoordNormal(weapon.getTexture().x)[0],
				RenderThread.spritesheet.getUpLeftCoordNormal(weapon.getTexture().x)[1],	
			};
			textureChange.put(texture);
		}
	}
	
	@Override
	public Entity setLink(Entity t) {
		if(t instanceof WeaponDisplay){
			WeaponDisplay wt = (WeaponDisplay)t;			
		}
		return this;
	}
	
	@Override
	public Entity getLinked() {
		return new WeaponDisplay(player).setLink(this);
	}
	
	public void init(){
		FloatBuffer vertices = BufferUtils.createFloatBuffer(2 * 32); //(x,y)*(4 vertices on a rectangle)
		float[] vertex = {
			0,height, width*x,height, width*x,0, 0,0,	
			width*x,height/3, width, height/3, width,0, width*x,0,	
			width*x ,2*height/3, width,2*height/3, width,height/3, width*x,height/3,	
			width*x,height, width,height, width, 2*height/3, width*x,2*height/3,
			width*y, height*z+4, width*y+durability,height*z+4, width*y+durability,height*z, width*y,height*z
		};
		vertices.put(vertex);
		vertices.rewind();
		
		FloatBuffer texVertices = BufferUtils.createFloatBuffer(2 * 32);
		float[] texturea = {
		RenderThread.spritesheet.getBottomLeftCoordNormal(21)[0],
		RenderThread.spritesheet.getBottomLeftCoordNormal(21)[1],
		RenderThread.spritesheet.getBottomRightCoordNormal(21)[0],
		RenderThread.spritesheet.getBottomRightCoordNormal(21)[1],
		RenderThread.spritesheet.getUpRightCoordNormal(1)[0],
		RenderThread.spritesheet.getUpRightCoordNormal(1)[1],
		RenderThread.spritesheet.getUpLeftCoordNormal(1)[0],
		RenderThread.spritesheet.getUpLeftCoordNormal(1)[1],

		RenderThread.spritesheet.getBottomLeftCoordNormal(weapon.getTexture().x)[0],
		RenderThread.spritesheet.getBottomLeftCoordNormal(weapon.getTexture().x)[1],
		RenderThread.spritesheet.getBottomRightCoordNormal(weapon.getTexture().y)[0],
		RenderThread.spritesheet.getBottomRightCoordNormal(weapon.getTexture().y)[1],
		RenderThread.spritesheet.getUpRightCoordNormal(weapon.getTexture().y)[0],
		RenderThread.spritesheet.getUpRightCoordNormal(weapon.getTexture().y)[1],
		RenderThread.spritesheet.getUpLeftCoordNormal(weapon.getTexture().x)[0],
		RenderThread.spritesheet.getUpLeftCoordNormal(weapon.getTexture().x)[1],

		RenderThread.spritesheet.getBottomLeftCoordNormal(12)[0],
		RenderThread.spritesheet.getBottomLeftCoordNormal(12)[1],
		RenderThread.spritesheet.getBottomRightCoordNormal(16)[0],
		RenderThread.spritesheet.getBottomRightCoordNormal(16)[1],
		RenderThread.spritesheet.getUpRightCoordNormal(16)[0],
		RenderThread.spritesheet.getUpRightCoordNormal(16)[1],
		RenderThread.spritesheet.getUpLeftCoordNormal(12)[0],
		RenderThread.spritesheet.getUpLeftCoordNormal(12)[1],

		RenderThread.spritesheet.getBottomLeftCoordNormal(22)[0],
		RenderThread.spritesheet.getBottomLeftCoordNormal(22)[1],
		RenderThread.spritesheet.getBottomRightCoordNormal(26)[0],
		RenderThread.spritesheet.getBottomRightCoordNormal(26)[1],
		RenderThread.spritesheet.getUpRightCoordNormal(26)[0],
		RenderThread.spritesheet.getUpRightCoordNormal(26)[1],
		RenderThread.spritesheet.getUpLeftCoordNormal(22)[0],
		RenderThread.spritesheet.getUpLeftCoordNormal(22)[1],

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
		
		vboVertexID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
		vboTexVertexID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
        glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void dispose(){
	    glDeleteBuffers(vboVertexID);
		glDeleteBuffers(vboTexVertexID);
	}

}
