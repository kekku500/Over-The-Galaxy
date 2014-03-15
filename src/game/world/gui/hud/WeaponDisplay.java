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

	public WeaponDisplay(Weapon weapon){
		this.weapon = weapon;		
		position = new Vector2f(50,0);
		width = 100;
		height = 50;
		
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		vertices.put(new float[]{0,height, width,height, width,0, 0,0});
		vertices.rewind();
		
		isTextured = true;
		
		texVertices = BufferUtils.createFloatBuffer(2 * 4);
		texVertices.put(new float[]{0,1, 1,1, 1,0, 0,0});
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
		glDrawArrays(GL_QUADS, 0, 4);
		RenderThread.graphics2D.drawString(38, 40, weapon.getClipAmount() + "/" + weapon.getMaxClips());
		RenderThread.graphics2D.drawString(20, 55, weapon.getAmmo() + "/" + weapon.getMaxAmmo());
		glDisable(GL_BLEND);
		
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
