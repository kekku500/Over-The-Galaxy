package game.world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import game.Game;
import game.State;
import game.threading.RenderThread;
import game.world.culling.BoundingAxis;
import game.world.culling.BoundingCube;
import game.world.culling.Octree;
import game.world.culling.ViewFrustum;
import game.world.culling.ViewFrustum.Frustum;
import game.world.entities.DynamicEntity;
import game.world.entities.Entity;
import game.world.entities.PhysicalEntity;
import game.world.entities.Player;
import game.world.entities.VisualEntity;
import game.world.entities.lighting.Lighting;
import game.world.entities.lighting.SunLight;
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
import org.lwjgl.opengl.Display;
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
	private Set<Entity> updatableEntities = new HashSet<Entity>();
	private Set<VisualEntity> visualEntites = new HashSet<VisualEntity>();
	private List<Lighting> lightingEntities = new ArrayList<Lighting>();

	//GUI
	public Container container;
	
	private Camera camera = new Camera(15,15,-30, this);
	
	private State state;
	
	private int uniqueID;
	private int id;
	
	private static int idCounter = 0;
	private int entityIdCounter = 0;
	private int componentIdCounter = 0;
	
	private int worldSize = 100000;
	Octree<VisualEntity> staticOctree;
	
	private int width = Game.width, height = Game.height;
	
	public Set<Entity> getUpdatableEntities(){
		return updatableEntities;
	}
	
	public Set<VisualEntity> getVisualEntities(){
		return visualEntites;
	}
	
	public List<Lighting> getLightingEntities(){
		return lightingEntities;
	}
	
	public void addEntity(Entity e){
		entityIdCounter++;
		e.setID(entityIdCounter);
		Request adding = new UpdateRequest<Entity>(Action.ADD, e);
		Request openglInit = new RenderRequest<Entity>(Action.INIT, e);
		if(e instanceof PhysicalEntity){
			PhysicalEntity pe = (PhysicalEntity)e;
			if(pe.isPhysical()){
				dynamicsWorld.addRigidBody(pe.getBody());
			}
		}
		state.getSyncManager().add(adding);
		state.getSyncManager().add(openglInit);
	}
	
	
	public World(State state, int id){
		uniqueID = idCounter;
		idCounter++;
		this.id = id;
		this.state = state;
		container = new Container();
	}
	
	public void init(){
		//Physics
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
		
		//Octree
		staticOctree = new Octree<VisualEntity>(worldSize);
	}
	
	public void renderInit() {
		graphics2D.init();
		renderEngine.init();	
	}
	
	public void linkWorlds(World otherWorld){
		otherWorld.getCamera().setLink(camera);
		otherWorld.setDynamicsWorld(getDynamicsWorld());
		otherWorld.setStaticOctree(getStaticOctree());
	}
	
	public Octree<VisualEntity> getStaticOctree() {
		return staticOctree;
	}

	public void setStaticOctree(Octree<VisualEntity> staticOctree) {
		this.staticOctree = staticOctree;
	}

	public void setCamera(Camera cam){
		camera = cam;
	}
	

	
	public void setCameraFocus(Entity e){
		camera.setFollowing(e);
	}
	
	private void entityUpdateRequest(UpdateRequest<?> req){
		Entity e = (Entity)req.getItem();
		if(req.getAction() == Action.ADD){
			e.setWorld(this);
			
			Entity copied = e.getLinked();
			
			updatableEntities.add(copied);
			
			if(e instanceof VisualEntity && !(e instanceof SunLight)){
				visualEntites.add((VisualEntity)copied);
			}
			if(e instanceof Lighting)
				lightingEntities.add((Lighting)copied);
		}else if(req.getAction() == Action.RENEWNEXT || req.getAction() == Action.RENEWNEXT){
				Entity updateThis = getEntityByID(e.getID(), getEntities());
				if(updateThis != null){ //found an entity
					updateThis.setLink(e);
				}
			}
	}
	
	/*private void oldEntityUpdateRequest(UpdateRequest<?> req, OldEntity e){
		boolean isStatic = e.isStatic();
		if(req.getAction() == Action.RENEWNEXT || req.getAction() == Action.RENEWNEXT){
			Entity updateThis = getEntityByID(e.getID(), getEntities());
			if(updateThis != null){ //found an entity
				updateThis.updateVariables(e);
			}
		}
		if(!isStatic){ //DYNAMIC
			if(req.getAction() == Action.ADD){
				if(e instanceof OldLightSource){
					OldEntity asEntity = (OldEntity)req.getItem();
					OldLightSource ls = (OldLightSource)asEntity.getVariables();
					ls.setWorld(this);
					lightSources.add(ls);
					if(ls.getModel() != null)
						getStaticEntities().add(ls);
				}else{
					OldEntity addThis = e.getVariables();
					e.setWorld(this);
					addThis.setWorld(this);
					getDynamicEntities().add(addThis);
				}
			}else if(req.getAction() == Action.REMOVE){
				removeEntity(e, getStaticEntities());
			}
		}else if(isStatic){
			if(req.getAction() == Action.ADD){
				if(e instanceof OldLightSource){
					OldLightSource ls = (OldLightSource)req.getItem();
					lightSources.add(ls);
					ls.setWorld(this);
					if(ls.getModel() != null)
						getStaticEntities().add(ls);
				}else{
					staticOctree.insert(e);
					e.setWorld(this);
					getStaticEntities().add(e); //not copying, reference to all
				}
			}else if(req.getAction() == Action.REMOVE){
				removeEntity(e, getDynamicEntities());
			}
		}
	}*/
	
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
			
			if(req.getItem() instanceof Entity){
				entityUpdateRequest(req);
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
		if(width != RenderThread.displayWidth || height != RenderThread.displayHeight)
			resized(RenderThread.displayWidth, RenderThread.displayHeight);
		//Check for added entities
		updateRequests();
		
		checkKeyboardInput();
		checkMouseInput();
		
		//grid.update();
		
		camera.update(dt);
		
		for(Entity e: getUpdatableEntities()){
			e.update(dt);
		}
		
		dynamicsWorld.stepSimulation(dt);
		
		World.renderEngine.update(dt);
		
		container.update();
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
	
	/*static List<Obj> objs; 
	static int totalItems = 0;
	static Obj camObj;
	
	private static boolean canRender = true;*/
	public void checkKeyboardInput(){
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				int k = Keyboard.getEventKey();
				for(Input e: InputListener.inputCheckObjects){
					e.checkKeyboardInput(k);
				}
				/*if(k == Keyboard.KEY_B){
					canRender = false;
					int items = 1000;
					totalItems += items;
					float itemSize = 10;
					float size = octree.getBounds().getSize();
					for(int i=0;i<items;i++){
						Obj one = new Obj("1", new BoundingCube(new Vector3f(-size/2+(float)Math.random()*(size-itemSize),-size/2+(float)Math.random()*(size-itemSize),-size/2+(float)Math.random()*(size-itemSize)), itemSize));
						octree.insert(one);
					}
					float cover = 100;
					Vector3f[] minmax = cameraFrustum.getFrustumBox();
					//Vector3f[] minmax = new Vector3f[]{camera.getPos(),
					//		new Vector3f(camera.getPos().x+cover, camera.getPos().y+cover, camera.getPos().z+cover)};
					camObj = new Obj("camera", new BoundingAxis(minmax[0], minmax[1]));
					objs = octree.retrieve(camObj);
					System.out.println("Retrieved " + objs.size() + "/" + totalItems);
					canRender = true;
				}*/
				
			}
		}
		/*Vector3f[] minmax = cameraFrustum.getFrustumBox();
		camObj = new Obj("camera", new BoundingAxis(minmax[0], minmax[1]));
		objs = octree.retrieve(camObj);
		System.out.println("Retrieved " + objs.size() + "/" + totalItems);*/
	}
	
	
	/**
	 * Removed entity by it's id.
	 * @param rem
	 * @param list
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
						Entity ve = (Entity)req.getItem();
						ve.openGLInitialization();
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
		//glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		World.renderEngine.perspective3D();
		renderEngine.renderAxes();
		
		
		
		
		/*if(canRender){
			//octree.render();
			if(objs != null){
				List<Obj> renderList = new ArrayList<Obj>(objs);
		        glColor3f(0f, 1f, 1f);
		        glLineWidth(3);
		        camObj.render();
		        glLineWidth(1);
		        glColor3f(0f, 1f, 0f);
				for(Obj o: renderList){
					o.render();
				}
			}
		}*/


		//Graphics3D.cameraPerspective();
        //glEnable(GL_DEPTH_TEST);
	    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //grid.render();
		//glDisable(GL_DEPTH_TEST);
		/*Graphics2D g = graphics2D;
	    //Render 2D stuff
	    Graphics2D.perspective2D();
	    //just TESting some stuff
	    container.render();
	    
	    g.setFontSize(18);
	    
	    g.drawString(500, 50, "EPIC MAN" + 50, Color.red);

	    
	    g.setFontSize(20);
	    
	    g.drawString(100, 50, "DEFAULT" + 50);*/

	    
	    
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
	

	public Set<Entity> getEntities(){
		return updatableEntities;
	}
	
	public void dispose(){
		World.renderEngine.dispose();
		for(Entity e: getEntities()){
			e.dispose();
		}
		container.dispose();
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
	
	public void setDynamicsWorld(DynamicsWorld dyn){
		dynamicsWorld = dyn;
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

	public void resized(int width, int height){
		this.width = width;this.height = height;
		camera.cameraFrustum.setProjection(Game.fov, width, height, Game.zNear, Game.zFar);
	}

}
