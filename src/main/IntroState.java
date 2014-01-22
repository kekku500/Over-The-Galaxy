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
		Main.debugPrint("Intro update");
		
	}

	@Override
	public void render() {
		Main.debugPrint("Intro render");
		
	}

	@Override
	public int getId() {
		return stateId;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
