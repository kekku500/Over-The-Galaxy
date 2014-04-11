package world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import input.Input;
import input.InputListener;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import resources.Resources;
import resources.model.Material;
import resources.model.Model;
import resources.model.SubModel;
import resources.model.custom.Sphere;
import resources.texture.Texture;
import shader.Shader;
import state.Game;
import state.State;
import threading.RenderThread;
import utils.math.Vector3f;
import utils.math.Vector4f;
import world.culling.BoundingAxis;
import world.culling.BoundingCube;
import world.culling.Octree;
import world.culling.ViewFrustum;
import world.culling.ViewFrustum.Frustum;
import world.entity.Entity;
import world.entity.PhysicalEntity;
import world.entity.VisualEntity;
import world.entity.WorldEntity;
import world.entity.dumb.DynamicEntity;
import world.entity.gui.Component;
import world.entity.gui.hud.HeadsUpDisplay;
import world.entity.lighting.Lighting;
import world.entity.lighting.SunLight;
import world.entity.smart.Player;
import world.graphics.Graphics2D;
import world.graphics.Graphics3D;
import world.graphics.ShadowMapper;
import world.sync.Linkable;
import world.sync.Request;
import world.sync.RequestList;
import world.sync.Request.Action;
import world.sync.Request.Status;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

import controller.Controller;

public class World implements Linkable<World>{
	
	//World info
	private int id;
	private State state;
	
	DynamicsWorld dynamicsWorld; //Physics World
	
	//General entities 
	private Set<Entity> entities = new HashSet<Entity>();
	
	//3D Entities
	private Set<WorldEntity> worldEntities = new HashSet<WorldEntity>();
	private Set<VisualEntity> visualEntites = new HashSet<VisualEntity>();
	private List<Lighting> lightingEntities = new ArrayList<Lighting>();
	
	//2D Entities (GUI)
	private Set<Component> components = new HashSet<Component>();

	//Input
	private InputListener inputListener = new InputListener();
	
	//Reference to specific entities
	private Controller camera;
	private Player player;
	
	private int entityIdCounter = 0;
	
	private int worldSize = 100000;
	Octree<VisualEntity> staticOctree;
	
	private int width = Game.width, height = Game.height;
	
	public World(State state, int id){
		this.id = id;
		this.state = state;
	}
	
	public void init(){
		//Physics
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
		
		//Octree
		staticOctree = new Octree<VisualEntity>(worldSize);
		
		
	}

	@Override
	public World setLink(World t) {
		dynamicsWorld = t.getDynamicsWorld();
		staticOctree = t.getStaticOctree();
		return this;
	}

	@Override
	public World getLinked() {
		return new World(state, id).setLink(this);
	}
	
	public void update(float dt){
		if(width != RenderThread.displayWidth || height != RenderThread.displayHeight)
			resized(RenderThread.displayWidth, RenderThread.displayHeight);
		
		updateRequests();
		
		try {
			inputListener.checkKeyboardInput();
			inputListener.checkMouseInput();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		camera.update(dt);
		
		for(Entity e: getUpdatableEntities()){
			e.update(dt);
		}
		
		dynamicsWorld.stepSimulation(dt);
	}
	
	private void entityUpdateRequest(Request<?> req){
		Entity e = (Entity)req.getItem();
		if(req.getAction() == Action.ADD){
			e.setWorld(this);
			
			Entity copied = e.getLinked(); //get copy of the object
			
			if(!(e instanceof Controller)) //Camera needs to be updated separately
				entities.add(copied); //all objects
			
			if(e instanceof Controller){
				camera = (Controller)copied;
				if(camera.isFollowing()){ //Get correct entity in this world for camera to follow
					WorldEntity following = camera.getFollowing();
					WorldEntity correctEntity = (WorldEntity)getEntityByID(following.getID(), getUpdatableEntities());
					camera.setFollowing(correctEntity);
				}
			}
			
			if(e instanceof Player)
				player = (Player)copied;
			
			if(e instanceof Input){ //input objects
				inputListener.addInput((Input)copied);
			}
			
			if(e instanceof WorldEntity){ //3d objects
				worldEntities.add((WorldEntity)copied);
				if(e instanceof VisualEntity && !(e instanceof SunLight)){
					visualEntites.add((VisualEntity)copied);
				}
				if(e instanceof Lighting)
					lightingEntities.add((Lighting)copied);
			}else if(e instanceof Component){ //2d objects
				components.add((Component)copied);
			}
		}else if(req.getAction() == Action.REMOVE){
			//meh, later
		}else if(req.getAction() == Action.RELINKNEXT || req.getAction() == Action.RELINKNEXT){
			Entity updateThis = getEntityByID(e.getID(), getUpdatableEntities());
			if(updateThis != null){ //found an entity to update
				updateThis.setLink(e);
			}
		}
	}

	public void updateRequests(){
		RequestList syncM = state.getRequestList();
		ListIterator<Request<?>> itr = syncM.getUpdateRequests().listIterator();
		while(itr.hasNext()){
			Request<?> req = itr.next();
			Status status = req.requestStatus(this);
			//System.out.println(req + " Status " + status + " at world " + uniqueID + " -> " + req.changedWorlds);
			if(status == Status.IDLE)
				continue; //skip, request must be handled later or is already done in this world

			if(req.getItem() instanceof Entity){
				entityUpdateRequest(req);
			}
			
			if(status == Status.FINAL){
				itr.remove();
			}
		}
	}
	
	public void render(){
		//RenderThread.graphics3D.render(this);

		
		//RenderThread.graphics3D.perspective3D();
		//RenderThread.graphics3D.renderAxes();

		Graphics2D g = RenderThread.graphics2D;
	    //Render 2D stuff
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    Graphics2D.perspective2D();
	    //just TESting some stuff
	    for(Component c: components){
	    	c.render();
	    }
	    
	    /*g.setFontSize(18);
	    
	    g.drawString(500, 50, "EPIC MAN" + 50, Color.red);
	    g.setFontSize(20);
	    
	    g.drawString(100, 50, "DEFAULT" + 50);
		RenderThread.graphics2D.drawTexture(RenderThread.spritesheet.getTex().getID());
	    Texture tex = null;
		try {
			tex = Resources.getTexture("smiley.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    g.drawTexture(tex, 10, 200, 1/4f, 1/4f);
	    
	    g.drawTexture(tex, 20, 210, 1/4f, 1/4f);
	    
	    g.drawTexture(tex, 80, 200, 1/4f, 1/4f, .5f);*/
	}
	
	public void addEntity(Entity e){
		entityIdCounter++;
		e.setID(entityIdCounter);
		Request<Entity> adding = new Request<Entity>(Action.ADD, e);
		if(e instanceof PhysicalEntity){
			PhysicalEntity pe = (PhysicalEntity)e;
			if(pe.isPhysical()){
				dynamicsWorld.addRigidBody(pe.getBody());
			}
		}
		state.getRequestList().add(adding);
	}
	
	/**
	 * Removed entity by it's id.
	 * @return Returns removed entity
	 */
	public Entity removeEntity(Entity rem, Set<Entity> list){
		for(Entity e: list){
			if(e.getID() == rem.getID()){
				list.remove(e);
				return e;
			}
		}
		return null;
	}
	
	public Entity getEntityByID(int id, Set<Entity> list){
		for(Entity e: list){
			if(e.getID() == id)
				return e;
		}
		return null;
	}
	
	
	
	public void replace(Set<Entity> list, Entity replaceWith){
		for(Entity e: list){
			if(e.getID() == replaceWith.getID()){
				list.remove(e);
				list.add(replaceWith);
				break;
			}
		}
	}
	
	public DynamicsWorld getDynamicsWorld(){
		return dynamicsWorld;
	}

	public Set<Entity> getUpdatableEntities(){
		return entities;
	}
	
	public Set<VisualEntity> getVisualEntities(){
		return visualEntites;
	}
	
	public Set<WorldEntity> getWorldEntities(){
		return worldEntities;
	}
	
	public List<Lighting> getLightingEntities(){
		return lightingEntities;
	}
	
	public Octree<VisualEntity> getStaticOctree() {
		return staticOctree;
	}
	
	/*public void dispose(){
		for(Entity e: getUpdatableEntities()){
			e.dispose();
		}
	}*/
	
	public Controller getController(){
		return camera;
	}
		
	public int getID(){
		return id;
	}
	
	public State getState(){
		return state;
	}
	
	public void resized(int width, int height){
		this.width = width;this.height = height;
		camera.cameraFrustum.setProjection(Game.fov, width, height, Game.zNear, Game.zFar);
	}

	public Player getPlayer(){
		return player;
	}
}
