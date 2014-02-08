package game.world;

import game.State;
import game.threading.RenderThread;
import game.threading.UpdateThread;
import game.world.FrustumCulling.Frustum;
import game.world.entities.Cuboid;
import game.world.entities.Entity;
import game.world.entities.Entity.Motion;
import game.world.gui.Component;
import game.world.gui.Container;
import game.world.gui.graphics.Graphics;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Action;
import game.world.sync.Request.Status;
import game.world.sync.Request.Type;
import game.world.sync.RequestManager;
import game.world.sync.UpdateRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.vecmath.Vector3f;

import org.newdawn.slick.Color;

import utils.Utils;
import main.Main;

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
	
	DynamicsWorld dynamicsWorld; //physics calc
	
	//Store objects
	Set<Entity> dynamicEntities = new HashSet<Entity>();
	Set<Entity> staticEntities = new HashSet<Entity>();
	
	//GUI
	public Container container;

	private int entityIdCounter = 0;
	private int componentIdCounter = 0;
	
	//Camera stuff
	private FrustumCulling frustum;
	private Camera camera = new Camera(0,15,60, this);
	
	private State state;
	
	public Grid grid;
	
	private int uniqueID;
	private int id;
	private static int idCounter = 0;
		
	public void setUpPhysics(){
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		
		dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
	}

	public World(State state, int id){
		uniqueID = idCounter;
		idCounter++;
		this.id = id;
		this.state = state;
		grid = new Grid(this);
		frustum = new FrustumCulling(camera);
		container = new Container();
	}
	
	public void updateRequests(){
		RequestManager syncM = state.getSyncManager();
		ListIterator<UpdateRequest> itr = syncM.getUpdateRequests().listIterator();
		while(itr.hasNext()){
			UpdateRequest req = itr.next();
			Status status = req.requestStatus(this);
			//System.out.println(req + " Status " + status + " at world " + uniqueID + " -> " + req.changedWorlds);
			if(status == Status.IDLE)
				continue; //skip, request must be handled later
			if(req.getType() == Type.ENTITY){
				Entity e = req.getEntity();
				if(e.getMotion() == Motion.STATIC){
					if(req.getAction() == Action.ADD){
						//System.out.println("added " + e + " to staticentities");
						getStaticEntities().add(e); //not copying, reference to all
					}else if(req.getAction() == Action.MOVE){
						removeDynamicEntity(e);
						getStaticEntities().add(e);
					}
				}else if(e.getMotion() == Motion.DYNAMIC){
					if(req.getAction() == Action.ADD){
						Entity addThis = e.copy();
						e.setWorld(this);
						addThis.setWorld(this);
						getDynamicEntities().add(addThis);
						//System.out.println("added " + e + " to dynamicentities");
					}else if(req.getAction() == Action.UPDATE || req.getAction() == Action.UPDATEALL){
						replace(getDynamicEntities(), e.copy());
					}else if(req.getAction() == Action.MOVE){
						Entity moveE = removeStaticEntity(e);
						getDynamicEntities().add(moveE.copy());
					}else if(req.getAction() == Action.CAMERAFOCUS){
						Entity focusThis = getDynamicEntityByID(e.getId());
						camera.setFollowing(focusThis);
					}
				}
			}else if(req.getType() == Type.COMPONENT){
				Component c = req.getComponent();
				if(req.getAction() == Action.ADD){
					container.addComponent(c);
				}
			}else if(req.getType() ==Type.CAMERA){
				if(req.getAction() == Action.UPDATE){
					Camera betterCam = req.getCamera();
					camera.copyFrom(betterCam);
				}
			}
			if(status == Status.FINAL){
				itr.remove();
			}
		}
		
	}

	public void update(float dt){
		//Check for added entities
		updateRequests();
		
		

		grid.update();
		
		//Dynamic
		
		for(Entity e: getDynamicEntities()){
			e.update(dt);
		}
		
		camera.update(dt);
		
		dynamicsWorld.stepSimulation(dt);
		
		
		
		//checkFrustum();
	}
	
	/**
	 * 
	 * @param rem
	 * @return Removed entity
	 */
	public Entity removeStaticEntity(Entity rem){
		for(Entity e: getStaticEntities()){
			if(e.getId() == rem.getId()){
				getStaticEntities().remove(e);
				return e;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param rem
	 * @return Removed entity
	 */
	public Entity removeDynamicEntity(Entity rem){
		for(Entity e: getDynamicEntities()){
			if(e.getId() == rem.getId()){
				getDynamicEntities().remove(e);
				return e;
			}
		}
		return null;
	}
	
	public Entity getDynamicEntityByID(int id){
		for(Entity e: getDynamicEntities()){
			if(e.getId() == id)
				return e;
		}
		return null;
	}
	
	public void checkFrustum(){
		float[] visibleCount = {0,0};
		frustum.update(); //Update variables required for culling check
		for(Entity e: getEntities()){
			//if(e instanceof Cuboid){
				if(frustum.inView(e) != Frustum.OUTSIDE){ //draw object if it's not outside of frustum
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
		ListIterator<RenderRequest> itr = syncM.getRenderRequests().listIterator();
		while(itr.hasNext()){
			RenderRequest req = itr.next();
			Status stat = req.requestStatus(this);
			//System.out.println(req + " Status " + stat + " at world " + getID());
			
			if(stat == Status.FINAL){
				if(req.getAction() == Action.CREATEVBO){
					if(req.getType() == Type.ENTITY){
						Entity e = req.getEntity();
						e.createVBO();
						req.done();
					}else if(req.getType() == Type.COMPONENT){
						Component c = req.getComponent();
						c.createVBO();
						req.done();
					}

				}
				itr.remove();
			}
		}
	}
	
	public void render(Graphics g){
		//Create objects
		renderRequests();
		
		camera.lookAt();
		
		grid.render();
		
		for(Entity e: getEntities()){
			e.render();
		}
		
	    //Render 2D stuff
	    /*RenderThread.perspective2D();
	    
	    container.render();
	    
	    g.setFontSize(18);
	    
	    g.drawString(500, 50, "EPIC MAN" + 50, Color.red);

	    
	    g.setFontSize(20);
	    
	    g.drawString(100, 50, "DEFAULT" + 50);
	    
	    //Back to 3D
	    RenderThread.perspective3D(); //reset perspective to 3d
	    */
	    
	}
	
	public void addComponent(Component c){
		componentIdCounter++;
		c.setId(componentIdCounter);
		//c.setWorld(this);
		Request request = new UpdateRequest(Action.ADD, c);
		Request request2 = new RenderRequest(Action.CREATEVBO, c);
		request.waitFor(request2);
		state.getSyncManager().add(request);
		state.getSyncManager().add(request2);
	}
	
	public Request addEntity(Entity e){
		entityIdCounter++;
		e.setId(entityIdCounter);
		//e.setWorld(this);
		Request request = new UpdateRequest(Action.ADD, e);
		Request request2 = new RenderRequest(Action.CREATEVBO, e);
		if(e.getRigidBody() != null)
			dynamicsWorld.addRigidBody(e.getRigidBody()); //add to physics world
		
		request.waitFor(request2);
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
			newList.add(e.copy());
		}
		return newList;
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public void setCamera(Camera cam){
		camera.setImportant(cam);
		//camera = cam;
		//frustum = new FrustumCulling(camera);
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
}