package world.entity.gui.hud;

import world.World;
import world.entity.Entity;
import world.entity.gui.AbstractComponent;

public abstract class HudComponent extends AbstractComponent{

	@Override
	public void setWorld(World world) {
	// TODO Auto-generated method stub
	
	}
	
	@Override
	public World getWorld() {
	// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void update(float dt) {
	// TODO Auto-generated method stub
	
	}
	
	@Override
	public boolean isInWorld() {
	// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Entity setLink(Entity t) {
	// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Entity getLinked() {
	// TODO Auto-generated method stub
		return null;
	}
	
	public void init(){
		
	}


}