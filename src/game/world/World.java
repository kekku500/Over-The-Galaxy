package game.world;

import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glMultMatrix;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import game.Game;
import game.State;
import game.world.ViewFrustum.Frustum;
import game.world.entities.Entity;
import game.world.entities.LightSource;
import game.world.entities.Player;
import game.world.entities.LightSource.LightType;
import game.world.graphics.Graphics2D;
import game.world.graphics.RenderEngine3D;
import game.world.graphics.ShadowMapper;
import game.world.gui.Component;
import game.world.gui.Container;
import game.world.input.Input;
import game.world.input.InputListener;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Action;
import game.world.sync.Request.Status;
import game.world.sync.RequestManager;
import game.world.sync.UpdateRequest;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;

import shader.Shader;
import utils.math.Vector3f;
import utils.math.Vector4f;
import blender.model.Material;
import blender.model.Model;
import blender.model.SubModel;
import blender.model.custom.Sphere;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

import controller.Camera;

public class World{
	
	public static RenderEngine3D renderEngine = new RenderEngine3D();
	public static Graphics2D graphics2D = new Graphics2D();
	
	DynamicsWorld dynamicsWorld; //Physics World
	
	//Store entities
	Set<Entity> dynamicEntities = new HashSet<Entity>();
	Set<Entity> staticEntities = new HashSet<Entity>();
	Set<LightSource> lightSources = new HashSet<LightSource>();

	//GUI
	public Container container;
	
	//Camera stuff
	//private FrustumCulling frustum;
	private ViewFrustum cameraFrustum;
	private Camera camera = new Camera(15,15,-30, this);
	
	private State state;
	
	public Grid grid;
	
	private int uniqueID;
	private int id;
	
	private static int idCounter = 0;
	private int entityIdCounter = 0;
	private int componentIdCounter = 0;
	
	public World(State state, int id){
		uniqueID = idCounter;
		idCounter++;
		this.id = id;
		this.state = state;
		grid = new Grid(this);
		cameraFrustum = new ViewFrustum();
		cameraFrustum.setProjection(Game.fov, Game.width, Game.height, Game.zNear, Game.zFar);
		container = new Container();
	}
	
	public void init(){
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
	}
	
	public void renderInit() {
		graphics2D.init();
		renderEngine.init();	
	}
	
	public void linkWorlds(World otherWorld){
		otherWorld.getCamera().linkTo(camera);
		otherWorld.setDynamicsWorld(getDynamicsWorld());
	}
	
	public void setCamera(Camera cam){
		camera = cam;
	}
	
	/**
	 * No action is taken when object isn't in this world.
	 * Removes entity from static entity collection if it's there and adds it to dynamic collection.
	 * @param e
	 */
	private void setEntityDynamic(Entity e){
		if(!e.isStatic()){ //Set entity dynamic only if it's ready to be set dynamic
			Entity removedEntity = removeEntity(e, getStaticEntities()); //Remove object from static collection
			if(removedEntity != null){ //Was able to remove something
				//removedEntity.sets
				getDynamicEntities().add(removedEntity); //Add entity to dynamic world
			}
		}
	}
	
	/**
	 * No action is taken when object isn't in this world.
	 * Removes entity from dynamic entity collection if it's there and adds it to static collection.
	 * @param e
	 */
	private void setEntityStatic(Entity e){
		if(e.isStatic()){ //Set entity static only if it's ready to be set static
			Entity removedEntity = removeEntity(e, getDynamicEntities()); //Remove object from dynamic collection
			System.out.println("moving entity " + removedEntity + " " + removedEntity.getId() + " to static collection");
			if(removedEntity != null){ //Was able to remove something
				getStaticEntities().add(removedEntity); //Add entity to dynamic world
			}
		}
	}
	
	public void setCameraFocus(Entity e){
		camera.setFollowing(e);
	}
	
	private void entityUpdateRequest(UpdateRequest<?> req, Entity e){
		boolean isStatic = e.isStatic();
		if(req.getAction() == Action.SETDYNAMIC){
			setEntityDynamic(e);
		}else if(req.getAction() == Action.SETSTATIC){
			setEntityStatic(e);
		}
		if(!isStatic){ //DYNAMIC
			if(req.getAction() == Action.ADD){
				/*if(e instanceof LightSource){
					LightSource ls = (LightSource)req.getItem();
					lightSources.add(ls);
					if(ls.getModel() != null)
						getStaticEntities().add(ls);
				}else{*/
					Entity addThis = e.getLinked();
					e.setWorld(this);
					addThis.setWorld(this);
					getDynamicEntities().add(addThis);
				//}
			}else if(req.getAction() == Action.REMOVE){
				removeEntity(e, getStaticEntities());
			}
		}else if(isStatic){
			if(req.getAction() == Action.ADD){
				if(e instanceof LightSource){
					LightSource ls = (LightSource)req.getItem();
					lightSources.add(ls);
					if(ls.getModel() != null)
						getStaticEntities().add(ls);
				}else{
					e.setWorld(this);
					getStaticEntities().add(e); //not copying, reference to all
				}
			}else if(req.getAction() == Action.REMOVE){
				removeEntity(e, getDynamicEntities());
			}
		}
	}
	
	private void componentUpdateRequest(UpdateRequest<?> req, Component c){
		if(req.getAction() == Action.ADD){
			container.addComponent(c);
		}
	}

	public void updateRequests(){
		RequestManager syncM = state.getSyncManager();
		ListIterator<UpdateRequest<?>> itr = syncM.getUpdateRequests().listIterator();
		while(itr.hasNext()){
			UpdateRequest<?> req = itr.next();
			Status status = req.requestStatus(this);
			//System.out.println(req + " Status " + status + " at world " + uniqueID + " -> " + req.changedWorlds);
			if(status == Status.IDLE)
				continue; //skip, request must be handled later or is already done in this world
			
			if(req.getItem() instanceof Entity){ //ENTITY -------------------------------
				Entity e = (Entity) req.getItem();
				entityUpdateRequest(req, e);
			}else if(req.getItem() instanceof Component){ //COMPONENT --------------------------------
				Component c = (Component) req.getItem();
				componentUpdateRequest(req, c);
			}
			
			if(status == Status.FINAL){
				itr.remove();
			}
		}
		
	}

	public void update(float dt){
		//Check for added entities
		updateRequests();
		
		checkKeyboardInput();
		checkMouseInput();
		
		//grid.update();
		
		for(Entity e: getDynamicEntities()){
			e.update(dt);
		}
		
		camera.update(dt);
		
		dynamicsWorld.stepSimulation(dt);
		
		World.renderEngine.update(dt);
		
		container.update();
		
		checkFrustum();
	}
	
	public void checkMouseInput(){
		while(Mouse.next()){
			if(Mouse.getEventButtonState()){
				int m = Mouse.getEventButton();
				for(Input e: InputListener.inputCheckObjects){
					e.checkMouseInput(m);
				}
			}
		}
	}
	
	public void checkKeyboardInput(){
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				int k = Keyboard.getEventKey();
				for(Input e: InputListener.inputCheckObjects){
					e.checkKeyboardInput(k);
				}
			}
		}
	}
	
	/**
	 * Removed entity by it's id.
	 * @param rem
	 * @param list
	 * @return Returns removed entity
	 */
	public Entity removeEntity(Entity rem, Set<Entity> list){
		for(Entity e: list){
			if(e.getId() == rem.getId()){
				getStaticEntities().remove(e);
				return e;
			}
		}
		return null;
	}
	
	public Entity getEntityByID(int id, Set<Entity> list){
		for(Entity e: list){
			if(e.getId() == id)
				return e;
		}
		return null;
	}
	
	public void checkFrustum(){
		float[] visibleCount = {0,0};
		cameraFrustum.setView(camera.getViewRay(), camera.getRightVector(), camera.getUpVector());
		cameraFrustum.setPos(camera.getPos());
		//frustum.update(); //Update variables required for culling check
		for(Entity e: getEntities()){
			//if(e instanceof Cuboid){
				//if(frustum.inView(e) != Frustum.OUTSIDE){ //draw object if it's not outside of frustum
				if(cameraFrustum.inView(e) != Frustum.OUTSIDE){ //draw object if it's not outside of frustum
					e.setVisible(true);
					visibleCount[0]++;
				}else{
					e.setVisible(false);
					visibleCount[1]++;
				}
			//}
		}
		//System.out.println("Visible " + visibleCount[0] + " Outside " + visibleCount[1]);
	}
	
	public void renderRequests(){
		RequestManager syncM = state.getSyncManager();
		ListIterator<RenderRequest<?>> itr = syncM.getRenderRequests().listIterator();
		while(itr.hasNext()){
			RenderRequest<?> req = itr.next();
			Status stat = req.requestStatus(this);
			//System.out.println(req + " Status " + stat + " at world " + getID());
			
			if(stat == Status.FINAL){
				if(req.getAction() == Action.INIT){
					if(req.getItem() instanceof Entity){
						Entity e = (Entity) req.getItem();
						e.renderInit();
					}else if(req.getItem() instanceof Component){
						Component c = (Component) req.getItem();
						c.renderInit();
					}
					req.done();
				}
				itr.remove();
			}
		}
	}
	
	public void render(){
		//Create objects
		renderRequests();

		World.renderEngine.render(this);
		
		/*Graphics3D.updateMatrices(camera);
		Graphics3D.render(getEntities(), this);*/
		
		//Graphics3D.cameraPerspective();
        //glEnable(GL_DEPTH_TEST);
	    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //grid.render();
		//glDisable(GL_DEPTH_TEST);
		Graphics2D g = graphics2D;
	    //Render 2D stuff
	    Graphics2D.perspective2D();
	    //just TESting some stuff
	    container.render();
	    
	    g.setFontSize(18);
	    
	    g.drawString(500, 50, "EPIC MAN" + 50, Color.red);

	    
	    g.setFontSize(20);
	    
	    g.drawString(100, 50, "DEFAULT" + 50);

	    
	    
	}
	
	public void addComponent(Component c){
		componentIdCounter++;
		c.setId(componentIdCounter);
		//c.setWorld(this);
		Request request = new UpdateRequest(Action.ADD, c);
		Request request2 = new RenderRequest(Action.INIT, c);
		request.waitFor(request2);
		state.getSyncManager().add(request);
		state.getSyncManager().add(request2);
	}
	
	public Request addEntity(Entity e){
		entityIdCounter++;
		e.setId(entityIdCounter);
		Request request = new UpdateRequest<Entity>(Action.ADD, e);
		Request request2 = new RenderRequest<Entity>(Action.INIT, e);
		if(e.getRigidBody() != null)
			dynamicsWorld.addRigidBody(e.getRigidBody()); //add to physics world
		
		//request.waitFor(request2);
		state.getSyncManager().add(request);
		state.getSyncManager().add(request2);
		return request2;
	}
	
	public void addEntity(Entity e, Request r){
		entityIdCounter++;
		e.setId(entityIdCounter);
		e.setWorld(this);
		Request request = new UpdateRequest(Action.ADD, e);
		Request request2 = r;
		if(e.getRigidBody() != null)
			dynamicsWorld.addRigidBody(e.getRigidBody()); //add to physics world
		
		request.waitFor(request2);
		state.getSyncManager().add(request);
	}
	
	public List<Entity> getEntities(){
		List<Entity> allEntities = new ArrayList<Entity>();
		allEntities.addAll(getDynamicEntities());
		allEntities.addAll(getStaticEntities());
		return allEntities;
	}
	
	public void dispose(){
		for(Entity e: getEntities()){
			e.dispose();
		}
		World.renderEngine.dispose();
		for(LightSource e: lightSources){
			e.dispose();
		}
		container.dispose();
	}
	
	public void replace(Set<Entity> list, Entity replaceWith){
		for(Entity e: list){
			if(e.getId() == replaceWith.getId()){
				list.remove(e);
				list.add(replaceWith);
				break;
			}
		}
	}
	
	public Set<Entity> getDynamicEntities(){
		return dynamicEntities;
	}
	
	public Set<Entity> getStaticEntities(){
		return staticEntities;
	}
	
	public DynamicsWorld getDynamicsWorld(){
		return dynamicsWorld;
	}
	
	public void setDynamicsWorld(DynamicsWorld dyn){
		dynamicsWorld = dyn;
	}
	
	public Set<Entity> getCopiedEntities(Set<Entity> list){
		Set<Entity> newList = new HashSet<Entity>();
		for(Entity e: list){
			newList.add(e.getLinked());
		}
		return newList;
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public int getID(){
		return id;
	}
	
	public int getUniqueID(){
		return uniqueID;
	}
	
	public State getState(){
		return state;
	}
	
	public Set<LightSource> getLightSources() {
		return lightSources;
	}

	

}
