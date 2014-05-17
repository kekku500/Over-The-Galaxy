package graphics.gui;

import org.lwjgl.util.vector.Vector2f;

public abstract class AbstractComponent implements Component{
	
	private Vector2f position = new Vector2f();
	protected HUDManager hudManager;
	
	@Override
	public void setHUDManager(HUDManager manager){
		hudManager = manager;
	}
	
	@Override
	public HUDManager getHUDManager(){
		return hudManager;
	}
	
	@Override
	public void setPosition(Vector2f v) {
		setPosition(v.x, v.y);
		
	}

	@Override
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
		
	}

	@Override
	public Vector2f getPosition() {
		return position;
	}

	public abstract void init();
	public abstract void dispose();

}
