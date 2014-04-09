package world.entity.gui.hud;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import threading.RenderThread;
import world.entity.smart.Player;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import Weapon.Weapon;
import resources.texture.Texture;

public class WeaponDisplay extends HudComponent{
	private Weapon weapon;
	private Player player;
	private float x;
	private float y;
	private float z;
	private int id;
	private int durability;
	FloatBuffer Vertices = BufferUtils.createFloatBuffer(2 * 4);
	FloatBuffer Texture = BufferUtils.createFloatBuffer(2 * 4);

	public WeaponDisplay(Player player){
	this.player = player;
	this.weapon = player.getWeapon();	
	width = 205;
	height = 113;
	x = 0.15F;
	y = 0.475F;
	z = 0.769F;
	durability = 86;
	position = new Vector2f(0,0);

	//Create Vertex Buffer
	vertices = BufferUtils.createFloatBuffer(2 * 32); //(x,y)*(4 vertices on a rectangle)
	float[] vertex = {
	0,height, width*x,height, width*x,0, 0,0,	
	width*x,height/3, width, height/3, width,0, width*x,0,	
	width*x ,2*height/3, width,2*height/3, width,height/3, width*x,height/3,	
	width*x,height, width,height, width, 2*height/3, width*x,2*height/3,
	width*y, height*z+4, width*y+durability,height*z+4, width*y+durability,height*z, width*y,height*z
	};
	vertices.put(vertex);
	vertices.rewind();
	
	isTextured = true;
	}
	
	private void setTexture(){
	texVertices = BufferUtils.createFloatBuffer(2 * 32);
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
	}
	
	@Override
	public void renderInitStart() {	
	setTexture();
	Texture tex = RenderThread.spritesheet.getTex();
	texture = tex.getID();
	vboTexVertexID = glGenBuffers();
	
	        glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
	        glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STREAM_DRAW);
	        glBindBuffer(GL_ARRAY_BUFFER, 0);
	
	}
	
	@Override
	public void renderDraw() {
	//Uuendatud Durability
	glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
	glBufferSubData(GL_ARRAY_BUFFER,224,Vertices);//1 v‰‰rtus = 4 bitti.
	glBindBuffer(GL_ARRAY_BUFFER, 0);
	        //Uuendatud relva logo
	glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
	        glBufferSubData(GL_ARRAY_BUFFER,224,Texture);
	        glBindBuffer(GL_ARRAY_BUFFER, 0);
	
	        glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glDrawArrays(GL_QUADS, 0, 32);
	RenderThread.graphics2D.drawString(88, 40, weapon.getClipAmount() + "/" + weapon.getMaxClips());
	RenderThread.graphics2D.drawString(70, 55, weapon.getAmmo() + "/" + weapon.getMaxAmmo());
	glDisable(GL_BLEND);
	
	
	}
	
	@Override
	public void update() {
	float[] vertex = {
	width*y, height*z+4, width*y+durability,height*z+4, width*y+durability,height*z, width*y,height*z
	};
	Vertices.put(vertex);
	Vertices.rewind();
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
	Texture.put(texture);
	Texture.rewind();
	}
	}

}