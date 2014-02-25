package game.world.entities;

import utils.math.Vector3f;


public class DefaultEntity extends AbstractEntity{

	@Override
	public Vector3f getPosToMid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity copy() {
		Entity e = new DefaultEntity();
		return copy2(e);
	}

	@Override
	public void firstUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRender() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endRender() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lastUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calcBoundingSphere() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calcBoundingAxis() {
		// TODO Auto-generated method stub
		
	}

}
