package game.world.entities;

import game.world.World;
import utils.BoundingAxis;
import utils.BoundingSphere;
import utils.R;
import utils.math.Vector3f;
import blender.model.Model;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;

public interface Entity {
	
	public void renderInit();
	
	
	public void update(float dt);
	
	public void render();
	
	
	public void dispose();
	
	
	/**
	 * Creates new RigidBody from given RigidBodyConstructionInfo and
	 * sets it to this entity's RigidBody.
	 * @param rbci RigidBodyConstructionInfo
	 */
	public void createRigidBody(RigidBodyConstructionInfo rbci);
	
	
	/**
	 * This method should be invoked only in updating state.
	 */
	public boolean setStatic();
	
	public boolean isStatic();
	
	
	/**
	 * In order to set object dynamic, RigidBodyConstructionInfo must be available in this class.
	 * This method should be invoked only in updating state.
	 */
	public boolean setDynamic();
	
	
	public void setRigidBody(RigidBody rigidShape);
	
	public RigidBody getRigidBody();
	
	
	public RigidBodyConstructionInfo getRigidBodyConstructionInfo();
	
	public void setRigidBodyConstructionInfo(RigidBodyConstructionInfo r);
	
	
	public void setModel(Model modelShape);
	
	public Model getModel();
	
	
	public void setMotionState(Transform t);
	
	public Transform getMotionState();
	
	
	public void setPos(Vector3f v);
	
	public Vector3f getPos();
	
	
	public void setId(int id);
	
	public int getId();
	
	
	public void setVisible(boolean b);
	
	public boolean isVisible();
	
	
	public void setWorld(World world);
	
	public World getWorld();
	
	
	//Debugging purpose
	public void setTag(int i);
	
	public int getTag();
	
	
	//Frustum culling
	public BoundingSphere getBoundingSphere();
	
	public BoundingAxis getBoundingAxis();
	
	
	/**
	 * @return Returns copy of this entity with some common variables.
	 */
	public Entity getLinked();


}
