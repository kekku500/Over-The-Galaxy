package game;

public abstract class State {
	
	public abstract void init();
	
	public abstract void update(float dt);
	
	public abstract void render();
	
	public abstract void dispose();
	
	public abstract int getId();

}
