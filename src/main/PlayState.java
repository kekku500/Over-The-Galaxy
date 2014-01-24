package main;

import game.State;
import game.world.World;
import game.world.entities.Box;
import game.world.entities.Player;
import game.world.entities.Point;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import controller.FPController;

public class PlayState extends State{
	
	private int stateId;

	private World world;
	private Player player;
	
	public PlayState(int stateId){
		this.stateId = stateId;
	}
	
	FPController camera = new FPController(0,30,0);
	
	public FPController getCamera(){
		return camera;
	}

	@Override
	public void init() {
		Main.debugPrint("PlayState init");
		world = new World(this);
		
		player = new Player(100,0,100);
		world.addEntity(player);
		
		world.addEntity(new Box(new Vector3f(0,0,0), 10, 10, 10));
		
		//Add random points
		int totalPoints = 2000;
		
		int width = 1000; //pixels
		
		int starty = 100;
		int height = 200; //pixels (from y=starty)
		
		int depth = 1000; //pixels
		Random rand = new Random();
		for(int i=0;i<totalPoints;i++){
			world.addEntity(new Point(new Vector3f((rand.nextFloat() - 0.5f)*width,starty+(rand.nextFloat())*height,rand.nextInt(depth) - (int)(depth/2))));
		}
	}

	@Override
	public void update(float dt){
		camera.update(dt);
	    world.update(dt);
	}
	
	@Override
	public void render(){
		camera.render();
	    world.render();
	}

	@Override
	public void dispose(){
		world.dispose();
	}
	
	@Override
	public int getId() {
		return stateId;
	}

}
