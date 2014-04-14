package world.entity.gui;

import org.lwjgl.util.vector.Vector2f;

import world.World;
import world.entity.Entity;

public abstract class AbstractComponent implements Component{
	
	private Vector2f position = new Vector2f();
	private int id;
	private World world;
	
	@Override
	public Entity setLink(Entity t) {

		id = t.getID();
		world = t.getWorld();
		
		if(t instanceof Component){
			Component wt = (Component)t;
			
			setPosition(wt.getPosition());
			
		}

		return this;
	}
	
	@Override
	public void setID(int i) {
		if(!isInWorld())
			id = i;
	}
	
	@Override
	public int getID() {
		return id;
	}
	@Override
	public void setWorld(World world) {
		this.world = world;
		
	}
	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public boolean isInWorld() {
		if(world == null)
			return false;
		return true;
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
