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
import game.threading.RenderThread;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import Weapon.Weapon;
import blender.model.Texture;

public class WeaponDisplay extends HudComponent{
	Weapon weapon;
	private float x;
	private float y;
	private float z;
	private float w;

	public WeaponDisplay(Weapon weapon){
		this.weapon = weapon;		
		position = new Vector2f(0,0);
		width = 205;
		height = 113;
		x = 0.15F;
		y = 0.226F;
		z = 0.904F;
		w = 0.052F;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 28); //(x,y)*(4 vertices on a rectangle)
		float[] vertex = {
			0,height, width*x,height, width*x,0, 0,0,			
			width*x, height/3,  width*y,height/3,  width*y,0, width*x, 0,			
			width*y,height*w, width*z,height*w, width*z,0, width*y,0,
			
			width*y,height/3, width*z,height/3, width*z,height*w, width*y, height*w,
			
			width*z, height/3,  width, height/3,  width,0,  width*z,0,			
			width*x ,2*height/3, width,2*height/3, width,height/3,  width*x,height/3,			
			width*x,height,  width,height,  width, 2*height/3, width*x,2*height/3
		};
		vertices.put(vertex);
		vertices.rewind();
		
		isTextured = true;
		
		texVertices = BufferUtils.createFloatBuffer(2 * 28);
		float[] texture = {
			0,1, 1*x,1, 1*x,0, 0,0,			
			1*x,1/3F, 1*y,1/3F, 1*y,0, 1*x,0,
			1*y,1*w, 1*z,1*w, 1*z,0, 1*y,0,
			
			//Relva pildi kordinaadid.
			1*y,1/3F, 1*z,1/3F, 1*z,1*w, 1*y,1*w,
			
			1*z,1/3F, 1,1/3F, 1,0,  1*z,0,		
			1*x,2/3F, 1,2/3F, 1,1/3F, 1*x,1/3F,
			1*x,1, 1,1, 1,2F/3F, 1*x,2F/3F
		};
		texVertices.put(texture);
		texVertices.rewind();
		
	}

	@Override
	public void renderInitStart() {
		Texture tex = Texture.loadTexture(weapon.getTexture());
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
		RenderThread.graphics2D.drawString(38, 40, weapon.getClipAmount() + "/" + weapon.getMaxClips());
		RenderThread.graphics2D.drawString(20, 55, weapon.getAmmo() + "/" + weapon.getMaxAmmo());
		glDisable(GL_BLEND);
		
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
