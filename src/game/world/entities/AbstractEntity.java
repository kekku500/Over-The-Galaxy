package game.world.entities;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import game.RenderState;
import game.vbotemplates.AbstractVBO;
import game.world.World;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.RequestManager;
import game.world.sync.UpdateRequest;
import game.world.sync.Request.Action;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import utils.BoundingAxis;
import utils.BoundingSphere;
import utils.Utils;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public abstract class AbstractEntity implements Entity{
	
	protected Transform motionState = new Transform(new Matrix4f(new Quat4f(0,0,0,1),new Vector3f(0,0,0), 1.0f));
	
	protected AbstractVBO modelShape; //visual object
	protected RigidBody rigidShape; //physics object
	protected RigidBodyConstructionInfo rigidInfo;
	
	protected boolean visible = true; //in camera

	protected Motion motion = Motion.STATIC;
	
	protected World world;
	protected int id;
	
	protected BoundingAxis boundingAxis;
	protected BoundingSphere boundingSphere;
	protected float radius;
	
	public boolean setStatic(){
		if(rigidShape == null) //no physics shape, can't set static
			return false;
		if(world != null){
			getWorld().getDynamicsWorld().removeRigidBody(rigidShape);
		}
		rigidShape.setMassProps(0f, new Vector3f(0,0,0)); //remove mass
		rigidShape.updateInertiaTensor();
		rigidShape.setLinearVelocity(new Vector3f(0,0,0)); //zero velocity
		rigidShape.setAngularVelocity(new Vector3f(0,0,0)); //zero angular velocity
		rigidShape.getMotionState().getWorldTransform(motionState); //update motionstate to the latest
		if(world != null){
			getWorld().getDynamicsWorld().addRigidBody(rigidShape);
		}
		motion = Motion.STATIC;
		if(world != null){ //update other worlds aswell
			RequestManager sync = getWorld().getState().getSyncManager();
			sync.add(new UpdateRequest(Action.MOVE, this));
		}
		return true;
	}
	
	/**
	 * In order to set object dynamic, RigidBodyConstructionInfo must be available in this class.
	 */
	public boolean setDynamic(){
		if(rigidShape == null || rigidInfo == null) //no physics shape, can't set dynamic then
			return false;
		if(world != null) //added to the world, then remove it before changing anything
			getWorld().getDynamicsWorld().removeRigidBody(rigidShape);
		
		reconstructRigidBody();

		if(world != null){ //add it back to the world
			getWorld().getDynamicsWorld().addRigidBody(rigidShape);
			rigidShape.activate();
		}
		motion = Motion.DYNAMIC;
		if(world != null){ //update other worlds as well
			RequestManager sync = getWorld().getState().getSyncManager();
			sync.add(new UpdateRequest(Action.MOVE, this));
		}
		return true;
	}
	
	private void reconstructRigidBody(){
		rigidInfo.motionState = rigidShape.getMotionState();
		rigidShape = new RigidBody(rigidInfo);
	}
	
	protected Transform oldMotionState = new Transform();
	@Override
	public void update(float dt){
		firstUpdate(dt);
		
		//update model motion state
		if(rigidShape != null){
			if(rigidShape.isActive()){
				rigidShape.getMotionState().getWorldTransform(motionState); //update position
				
				Vector3f min = new Vector3f();
				Vector3f max = new Vector3f();
				rigidShape.getAabb(min, max);
				boundingAxis = new BoundingAxis(min, max);
				
				calcBoundingSphere();

				///not required if only updating motionState
				/*RequestManager sync = getWorld().getState().getSyncManager();
				sync.addCheck(new UpdateRequest(Action.UPDATEALL, this, id));*/
			}
		}
		lastUpdate(dt);
	}
	@Override
	public void setRigidBody(RigidBody rigidShape){
		this.rigidShape = rigidShape;
	}
	
	@Override
	public RigidBody getRigidBody(){
		return rigidShape;
	}
	
	@Override
	public void setModel(AbstractVBO model){
		this.modelShape = model;
	}
	
	@Override
	public AbstractVBO getModel(){
		return modelShape;
	}
	
	@Override
	public RigidBodyConstructionInfo getRigidBodyConstructionInfo(){
		return rigidInfo;
	}
	
	@Override
	public void setRigidBodyConstructionInfo(RigidBodyConstructionInfo r){
		rigidInfo = r;
	}
	

	
	@Override
	public void render(){
		if(modelShape == null)
			return;
		if(!isVisible()){
			return;
		}
		startRender();
		
		glPushMatrix(); //save current transformations
		
		//Transform t = new Transform();
		float[] f = new float[16];
		//body.getMotionState().getWorldTransform(t);

		motionState.getOpenGLMatrix(f);
		
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		fb.put(f);
		fb.rewind();
		
		glMultMatrix(fb);


		modelShape.render();
	    
	    glPopMatrix(); //reset transformations
	    
	    endRender();
	}
	
	@Override
	public void dispose(){
		if(modelShape != null)
			modelShape.dispose();
	}
	
	@Override
	public void createVBO() {
		if(modelShape != null)
			modelShape.prepareVBO();
	}
	
	@Override
	public void setMotion(Motion m){
		motion = m;
	}
	
	@Override
	public Motion getMotion(){
		return motion;
	}
	
	@Override
	public void setPos(Vector3f v){
		motionState.transform(v);
	}
	
	@Override
	public Vector3f getPos() {
		return motionState.origin;
	}
	
	@Override
	public void setVisible(boolean b){
		visible = b;
	}
	
	@Override
	public boolean isVisible(){
		return visible;
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
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setMotionState(Transform t){
		motionState = t;
	}
	
	@Override
	public Transform getMotionState(){
		return motionState;
	}
	
	@Override
	public BoundingSphere getBoundingSphere(){
		return boundingSphere;
	}
	
	@Override
	public BoundingAxis getBoundingAxis(){

		return boundingAxis;
	}

	public Entity copy2(Entity e){
		e.setVisible(isVisible());
		e.setWorld(getWorld());
		e.setId(getId());
		e.setMotion(getMotion());
		e.setVBOObject(modelShape);
		e.getMotionState().set(motionState);
		e.setRigidBody(rigidShape);
		e.setRigidBodyConstructionInfo(rigidInfo);
		return e;
	}	
	
	public AbstractVBO getVBOOBject(){
		return modelShape;
	}
	
	public void setVBOObject(AbstractVBO o){
		modelShape = o;
	}
	
	public abstract void lastUpdate(float dt);
	
	public abstract void firstUpdate(float dt);
	
	public abstract void startRender();
	
	public abstract void endRender();
	
	public abstract void calcBoundingSphere();
	
	public abstract void calcBoundingAxis();

}