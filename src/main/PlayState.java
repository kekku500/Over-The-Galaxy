package main;

import game.RenderState;
import game.State;
import game.threading.RenderThread;
import game.world.World;
import game.world.entities.Cuboid;
import game.world.entities.Entity.Motion;
import game.world.entities.Line;
import game.world.entities.Player;
import game.world.entities.Point;
import game.world.gui.Container;
import game.world.gui.Rectangle;
import game.world.gui.graphics.Graphics;

import java.awt.Font;
import java.util.Random;

import math.Vector3fc;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import controller.Camera;
import controller.Camera;

public class PlayState extends State{
	
	private int stateId;
	
	private Player player;
	
	public PlayState(int stateId){
		this.stateId = stateId;
	}
	
	@Override
	public void init() {
		Main.debugPrint("PlayState init");
		World world = this.getUpToDateState().getWorld();

		player = new Player(10,0,10);

		world.addEntity(player);

		world.addComponent(new Rectangle(new Vector2f(100,100), 200, 50));

		//world.addEntity(new Cuboid(new Vector3fc(200,20,0), 20, 50, 200));
		
		//world.addEntity(new Line(new Vector3fc(200,20,0), new Vector3fc(200,20,200)));

		
		//Add random points
		/*int totalPoints = 40000;
		
		int width = 4000; //pixels
		
		int starty = 100;
		int height = 2000; //pixels (from y=starty)
		
		int depth = 4000; //pixels
		Random rand = new Random();
		for(int i=0;i<totalPoints;i++){
			world.addEntity(new Point(new Vector3fc((rand.nextFloat() - 0.5f)*width,starty+(rand.nextFloat())*height,rand.nextInt(depth) - (int)(depth/2))));
		}
		System.out.println("added");*/
		
		
	}

	@Override
	public void update(float dt){
		World world = getUpdatingState().getWorld();
	    world.update(dt);
	    //container.update();
	}
	boolean first = true;
	@Override
	public void render(Graphics g){
		World world = getRenderingState().getWorld();
	    world.render(g);
	     
	}

	@Override
	public void dispose(){
		World world = getUpToDateState().getWorld();
		world.dispose();
		//container.dispose();
	}
	
	@Override
	public int getId() {
		return stateId;
	}

}
