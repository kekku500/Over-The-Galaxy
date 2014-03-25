package main;

import game.Game;
import game.State;
import game.world.graphics.Graphics2D;

/**
 * A simple example of a state.
 */

public class IntroState extends State{
	
	private int stateId;
	
	public IntroState(int stateId){
		this.stateId = stateId;
	}

	@Override
	public void init() {
		Game.print("Init intro");
		
	}

	@Override
	public void update(float dt) {
		
	}

	@Override
	public void render() {
	}

	@Override
	public void dispose() {
		//Dispose all objects created in this state.
		
	}
	
	@Override
	public int getId() {
		return stateId;
	}

	@Override
	public void renderInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postRenderInit() {
		// TODO Auto-generated method stub
		
	}

}
