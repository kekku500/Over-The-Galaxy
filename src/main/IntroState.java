package main;

import game.State;

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
		Main.debugPrint("Init intro");
		
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

}
