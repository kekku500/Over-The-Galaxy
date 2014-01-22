package main;

import game.State;

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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getId() {
		return stateId;
	}

}
