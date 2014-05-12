package world.gui;

import java.util.ArrayList;
import java.util.List;

import main.PlayState;
import state.State;

public class HUDManager {
	
	private List<Component> components = new ArrayList<Component>();
	
	PlayState state;
	
	public HUDManager(PlayState state){
		this.state = state;
	}
	

	
	public void add(Component c){
		c.setHUDManager(this);
		components.add(c);
	}
	
	public void update(float dt){
		for(Component c: components)
			c.update(dt);
	}
	
	public void render(){
		for(Component c: components)
			c.render();
	}
	
	public PlayState getState(){
		return state;
	}

}
