package game.world.entities;

import static org.lwjgl.opengl.GL11.glMultMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import game.world.World;
import game.world.sync.Request.Action;
import game.world.sync.RequestManager;
import game.world.sync.UpdateRequest;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;

import utils.BoundingAxis;
import utils.BoundingSphere;
import utils.R;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import blender.model.Model;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;

public abstract class AbstractEntity implements Entity{
	
	//All variables that are independent from rendering
	protected R<Boolean> isStaticWrapper = new R<Boolean>(true);
	protected R<RigidBody> rigidBodyWrapper = new R<RigidBody>();
	protected R<Model> modelWrapper = new R<Model>(); //visual object
	protected R<RigidBodyConstructionInfo> constructionInfoWrapper = new R<RigidBodyConstructionInfo>(); //for changing object static/dynamic
	protected R<Integer> idWrapper = new R<Integer>();
	
	//Every entity must have each of these variables (multi-threading rendering)
	protected Transform motionState = new Transform(new Matrix4f(new Quat4f(0,0,0,1),new Vector3f(0,0,0), 1.0f));
	protected boolean visible = true; //in camera
	protected World world;
	
	//Other
	private int tag; //just to identify object when needed

	protected BoundingAxis boundingAxis;
	protected BoundingSphere boundingSphere;
	
	@Override
	public void update(float dt){
		//update model motion state
		if(getRigidBody() != null){
			if(getRigidBody().isActive()){
				getRigidBody().getMotionState().getWorldTransform(motionState); //update position
				Vector3f min = new Vector3f();
				Vector3f max = new Vector3f();
				getRigidBody().getAabb(min, max);
				boundingAxis = new BoundingAxis(min, max);
				
				if(boundingSphere != null)
					boundingSphere.pos = getPos();
			}
		}
	}
	
	@Override
	public void render(){
		//if("rendering " + this + " isvisual? " + !isVi) 
		if(!isVisual() || !isVisible())
			return;
		
		glPushMatrix(); //save current transformations
		
		Matrix4f m = new Matrix4f();
		motionState.getMatrix(m);
		m.transpose();
		glMultMatrix(m.asFlippedFloatBuffer()); //translate, rotate, scale
		
		getModel().render();
	    
	    glPopMatrix(); //reset transformations
	}
	
	/**
	 * Creates new RigidBody from given RigidBodyConstructionInfo and
	 * sets it to this entity's RigidBody.
	 * @param rbci RigidBodyConstructionInfo
	 */
	@Override
	public void createRigidBody(RigidBodyConstructionInfo rbci){
		constructionInfoWrapper.set(rbci);
		setRigidBody(new RigidBody(rbci));
		isStaticWrapper.set(false);
		
		//bounding aabb
		Vector3f min = new Vector3f();
		Vector3f max = new Vector3f();
		getRigidBody().getAabb(min, max);
		boundingAxis = new BoundingAxis(min, max);
		
		//Bounding sphere
		float[] f = new float[1];
		Vector3f v = new Vector3f();
		getRigidBodyConstructionInfo().collisionShape.getBoundingSphere(v, f);
		
		boundingSphere = new BoundingSphere(getPos(), f[0]);
	}
	
	public boolean isVisual(){
		if(getModel() == null)
			return false;
		return true;
	}

	
	public void setTag(int i){
		tag = i;
	}
	
	public int getTag(){
		return tag;
	}
	
	

	
	/**
	 * @return Does this entity have RigidBody?
	 */
	public boolean isPhysical(){
		if(rigidBodyWrapper == null)
			return false;
		return true;
	}
	
	/**
	 * @return Has been added to the world?
	 */
	public boolean isInWorld(){
		if(world == null)
			return false;
		return true;
	}
	

	public boolean setStatic(){
		if(isStatic() || !isPhysical()) //no physics shape, can't set static
			return false;
		if(isInWorld()){ //remove from world before changing
			getWorld().getDynamicsWorld().removeRigidBody(getRigidBody());
		}
		getRigidBody().setMassProps(0f, new Vector3f(0,0,0)); //remove mass
		getRigidBody().updateInertiaTensor();
		getRigidBody().setLinearVelocity(new Vector3f(0,0,0)); //zero velocity
		getRigidBody().setAngularVelocity(new Vector3f(0,0,0)); //zero angular velocity
		getRigidBody().getMotionState().getWorldTransform(motionState); //update motionstate to the latest, just in case
		if(isInWorld()){
			getWorld().getDynamicsWorld().addRigidBody(getRigidBody());
		}
		getIsStaticWrapper().set(true);
		if(isInWorld()){ //update other worlds as well
			RequestManager sync = getWorld().getState().getSyncManager();
			sync.add(new UpdateRequest<Entity>(Action.SETSTATIC, this));
		}
		return true;
	}
	

	public boolean setDynamic(){
		if(!isStatic() || !isPhysical() || constructionInfoWrapper == null) //no physics shape, can't set dynamic then
			return false;
		if(isInWorld()){ //then remove it before changing anything
			getWorld().getDynamicsWorld().removeRigidBody(getRigidBody());
			//System.out.println("reconstructiong");
			//reconstructRigidBody();
		}
		createRigidBody(getRigidBodyConstructionInfo());
		getIsStaticWrapper().set(false);
		if(isInWorld()){ //update other worlds as well
			getWorld().getDynamicsWorld().addRigidBody(getRigidBody());
			RequestManager sync = getWorld().getState().getSyncManager();
			sync.add(new UpdateRequest<Entity>(Action.SETDYNAMIC, this));
		}
		return true;
	}
	

	@Override
	public void setRigidBody(RigidBody rigidShape){
		this.rigidBodyWrapper.set(rigidShape);
	}
	
	
	@Override
	public RigidBody getRigidBody(){
		return rigidBodyWrapper.get();
	}
	
	
	@Override
	public void setModel(Model model){
		this.modelWrapper.set(model);
	}
	
	@Override
	public Model getModel(){
		return modelWrapper.get();
	}
	
	
	@Override
	public RigidBodyConstructionInfo getRigidBodyConstructionInfo(){
		return constructionInfoWrapper.get();
	}
	
	
	@Override
	public void setRigidBodyConstructionInfo(RigidBodyConstructionInfo r){
		constructionInfoWrapper.set(r);
	}
	
	

	

	
	@Override
	public void dispose(){
		if(getModel() != null)
			getModel().dispose();
	}
	
	@Override
	public void renderInit() {
		if(getModel() != null)
			getModel().prepareVBO();
	}
	
	@Override
	public void setPos(Vector3f v){
		motionState.origin.set(v);
	}
	
	@Override
	public Vector3f getPos() {
		return new Vector3f(motionState.origin);
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
		idWrapper.set(id);
	}
	
	@Override
	public int getId() {
		return idWrapper.get();
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

	@Override
	public boolean isStatic() {
		return isStaticWrapper.get();
	}
	
	/**
	 * @param masterEntity
	 * @return Returns linked entity (The entity using this method).
	 */
	public Entity linkTo(AbstractEntity masterEntity){
		rigidBodyWrapper = masterEntity.getRigidBodyWrapper();
		modelWrapper = masterEntity.getModelWrapper();
		constructionInfoWrapper = masterEntity.getRigidBodyConstructionInfoWrapper();
		idWrapper = masterEntity.getIdWrapper();
		isStaticWrapper = masterEntity.getIsStaticWrapper();
		setTag(masterEntity.getTag());
		boundingSphere = masterEntity.getBoundingSphere();
		boundingAxis = masterEntity.getBoundingAxis();
		return this;
	}
	
	@Override
	public Entity getLinked(){
		return new DefaultEntity().linkTo(this);
	}
	
	
	private R<RigidBody> getRigidBodyWrapper(){
		return rigidBodyWrapper;
	}
	
	
	private R<Model> getModelWrapper(){
		return modelWrapper;
	}
	
	
	private R<RigidBodyConstructionInfo> getRigidBodyConstructionInfoWrapper(){
		return constructionInfoWrapper;
	}
	
	
	private R<Integer> getIdWrapper(){
		return idWrapper;
	}
	
	
	private R<Boolean> getIsStaticWrapper(){
		return isStaticWrapper;
	}
	

}
