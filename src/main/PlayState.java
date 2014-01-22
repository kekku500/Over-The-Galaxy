package main;

import game.State;
import game.world.World;
import game.world.entities.Box;
import game.world.entities.Entity;
import game.world.entities.Player;

public class PlayState extends State{
	
	private int stateId;

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
	public void update(float dt){
	    world.update(dt);
	}
	
	@Override
	public void render(){
	    world.render();
	}

	@Override
	public void dispose(){
		for(Entity e: world.getEntities()){
			e.dispose();
		}
	}
	
	@Override
	public int getId() {
		return stateId;
	}

}
