package main;

import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_ESCAPE;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;
import static org.lwjgl.input.Keyboard.isKeyDown;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.PriorityQueue;

import org.lwjgl.opengl.Display;

import game.State;
import game.world.RenderState;
import game.world.World;
import game.world.entities.Box;
import game.world.entities.Entity;
import game.world.entities.Player;

public class PlayState extends State{
	
	private int stateId;
	
	private float x, y;
	
	private World world;
	private Player player;
	
	public PlayState(int stateId){
		this.stateId = stateId;
	}

	@Override
	public void init() {
		Main.debugPrint("PlayState init");
		world = new World();
		
		player = new Player(300, 100, 50, 50);
		
		world.addEntity(new Box(100,100,100,150));
		world.addEntity(new Box(300,300,100,50));
		world.addEntity(player);
	}

	@Override
	public void update(float dt) {
		checkInput(dt);
	    world.update(dt);
	}
	
	private void checkInput(float dt){
	    if (isKeyDown(KEY_LEFT))
	    	player.requestMoveLeft();
	    else if (isKeyDown(KEY_RIGHT)){
	        player.requestMoveRight();
	    }
	    if (isKeyDown(KEY_UP))
	    	player.requestMoveUp();
	    else if (isKeyDown(KEY_DOWN))
	    	player.requestMoveDown();
	}

	@Override
	public void render(){
	    world.render();
	}

	@Override
	public int getId() {
		return stateId;
	}
	
	@Override
	public void dispose(){
		for(Entity e: world.getEntities()){
			e.dispose();
		}
	}

}
