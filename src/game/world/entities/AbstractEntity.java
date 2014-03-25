package game.world.entities;

import javax.vecmath.Quat4f;

import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import game.world.World;
import game.world.culling.BoundingAxis;
import game.world.culling.BoundingSphere;

public abstract class AbstractEntity implements Entity {
	
	private int id;
	private World world;
	
	protected Transform positionRotation;
	protected BoundingAxis boundingAxis;
	protected BoundingSphere boundingSphere;
	
	public AbstractEntity(){
		positionRotation = new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(0,0,0), 1));
	}
	
	@Override
	public Entity setLink(Entity t) {
		id = t.getID();
		world = t.getWorld();
		
		positionRotation.set(t.getTransform());
		boundingAxis = t.getBoundingAxis().copy();
		boundingSphere = t.getBoundingSphere().copy();

		return this;
	}
	
	@Override
	public BoundingAxis getBoundingAxis() {
		boundingAxis = new BoundingAxis(getPosition(), getPosition());
		return boundingAxis;
	}

	@Override
	public BoundingSphere getBoundingSphere() {
		boundingSphere = new BoundingSphere(getPosition(), 0);
		return boundingSphere;
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
	public void setPosition(Vector3f v) {
		setPosition(v.x, v.y, v.z);
		
	}

	@Override
	public void setPosition(float x, float y, float z) {
		positionRotation.origin.set(x, y, z);
		
	}

	@Override
	public Vector3f getPosition() {
		return new Vector3f(positionRotation.origin);
	}

	@Override
	public Transform getTransform() {
		return positionRotation;
	}




}
