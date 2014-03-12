package game.world.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import game.world.World;
import game.world.entities.Player;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;



import blender.model.Texture;

public class HeadsUpDisplay{
	public List<HudComponent> components = new ArrayList<HudComponent>();
	private Player player;
	private World world;
	String texPath = "res/models/HudWep.png";
	

	public HeadsUpDisplay(Player player, World world) {
		this.player = player;
		this.world = world;
		
		components.add(new HudComponent());
		world.addComponent(components.get(0));

	}



	public void renderInitStart() {

	}

	public void renderDraw() {

	}

	public void update() {
		for(HudComponent c: components){
			c.update();
		}
	}

}
