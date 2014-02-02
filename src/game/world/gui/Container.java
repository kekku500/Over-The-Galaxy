package game.world.gui;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;

public class Container extends AbstractComponent{
	
	private int componentCount;
	
	ArrayList<Component> components = new ArrayList<Component>();
	
	public Container(){
		position = new Vector2f();
	}
	
	public void addComponent(Component c){
		componentCount++;
		c.setId(componentCount);
		c.setMaster(this);
		components.add(c);
	}

	@Override
	public void update() {
		for(Component c: components){
			c.update();
		}
	}

	@Override
	public void render() {
		for(Component c: components){
			c.render();
		}
	}

	@Override
	public void dispose() {
		for(Component c: components){
			c.dispose();
		}
	}

	@Override
	public void renderDraw() {}

}
