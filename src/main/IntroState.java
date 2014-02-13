package main;

import controller.Camera;
import game.Game;
import game.State;
import game.world.gui.graphics.Graphics;

import org.lwjgl.util.vector.Vector2f;

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
	public void render(Graphics g) {
	}

	@Override
	public void dispose() {
		//Dispose all objects created in this state.
		
	}
	
	@Override
	public int getId() {
		return stateId;
	}

}
