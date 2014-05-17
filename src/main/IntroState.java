package main;

import main.state.State;

/**
 * A simple example of a state.
 */

public class IntroState extends State{
	
	private int stateId;
	
	public IntroState(int stateId){
		this.stateId = stateId;
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
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resized(float width, float height) {
		// TODO Auto-generated method stub
		
	}

}
