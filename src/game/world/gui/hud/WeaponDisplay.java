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

import Weapon.Weapon;
import blender.model.Texture;

public class WeaponDisplay extends HudComponent{
	private Weapon weapon;
	private float x;
	private boolean textured = false;
	private int id;

	public WeaponDisplay(Weapon weapon){
		this.weapon = weapon;		
		position = new Vector2f(0,0);
		width = 205;
		height = 113;
		x = 0.15F;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 28); //(x,y)*(4 vertices on a rectangle)
		float[] vertex = {
			0,height, width*x,height, width*x,0, 0,0,						
			width*x,height/3,  width, height/3, width,0, width*x,0,			
			width*x ,2*height/3, width,2*height/3, width,height/3,  width*x,height/3,			
			width*x,height,  width,height,  width, 2*height/3, width*x,2*height/3
		};
		vertices.put(vertex);
		vertices.rewind();
		
		isTextured = true;
	}
	
	private void setTexture(){
		texVertices = BufferUtils.createFloatBuffer(2 * 28);
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
			
		};
		texVertices.put(texturea);
		texVertices.rewind();
		textured= true;
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
		glDrawArrays(GL_QUADS, 0, 28);
		RenderThread.graphics2D.drawString(88, 40, weapon.getClipAmount() + "/" + weapon.getMaxClips());
		RenderThread.graphics2D.drawString(70, 55, weapon.getAmmo() + "/" + weapon.getMaxAmmo());
		glDisable(GL_BLEND);
		
		
	}

	@Override
	public void update() {
		if(id != weapon.getID()){
			setTexture();
		}
		
	}

}
