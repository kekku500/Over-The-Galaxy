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
import game.State;
import game.world.FrustumCulling.Frustum;
import game.world.entities.Entity;
import game.world.graphics.DeferredLightPoints;
import game.world.graphics.Graphics3D;
import game.world.gui.Component;
import game.world.gui.Container;
import game.world.gui.graphics.Graphics2D;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Action;
import game.world.sync.Request.Status;
import game.world.sync.Request.Type;
import game.world.sync.RequestManager;
import game.world.sync.UpdateRequest;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import shader.GLSLProgram;
import blender.model.Material;
import blender.model.Model;
import blender.model.SubModel;

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
	
	DynamicsWorld dynamicsWorld; //Physics World
	
	//Store entities
	Set<Entity> dynamicEntities = new HashSet<Entity>();
	Set<Entity> staticEntities = new HashSet<Entity>();
	
	//GUI
	public Container container;
	
	//Camera stuff
	private FrustumCulling frustum;
	private Camera camera = new Camera(15,15,-30, this);
	
	private State state;
	
	public Grid grid;
	
	private int uniqueID;
	private int id;
	
	private static int idCounter = 0;
	private int entityIdCounter = 0;
	private int componentIdCounter = 0;
		
	public void setUpPhysics(){
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
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
				continue; //skip, request must be handled later or done in this world
			if(req.getType() == Type.ENTITY){
				Entity e = req.getEntity();
				if(e.isDynamic()){
					if(req.getAction() == Action.ADD){
						getStaticEntities().add(e); //not copying, reference to all
					}else if(req.getAction() == Action.MOVE){ //moving object to static
						removeEntity(e, getDynamicEntities());
						getStaticEntities().add(e);
					}else if(req.getAction() == Action.REMOVE){
						removeEntity(e, getStaticEntities());
					}
				}else if(e.isStatic()){
					if(req.getAction() == Action.ADD){
						Entity addThis = e.copy();
						e.setWorld(this);
						addThis.setWorld(this);
						getDynamicEntities().add(addThis);
						//System.out.println("added " + e + " to dynamicentities");
					}else if(req.getAction() == Action.UPDATE || req.getAction() == Action.UPDATEALL){
						replace(getDynamicEntities(), e.copy());
					}else if(req.getAction() == Action.MOVE){
						Entity moveE = removeEntity(e, getStaticEntities());
						getDynamicEntities().add(moveE.copy());
					}else if(req.getAction() == Action.REMOVE){
						removeEntity(e, getDynamicEntities());
					}else if(req.getAction() == Action.CAMERAFOCUS){
						Entity focusThis = getEntityByID(e.getId(), getDynamicEntities());
						camera.setFollowing(focusThis);
					}
				}
			}else if(req.getType() == Type.COMPONENT){
				Component c = req.getComponent();
				if(req.getAction() == Action.ADD){
					container.addComponent(c);
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
		
		for(Entity e: getDynamicEntities()){
			e.update(dt);
		}
		
		camera.update(dt);
		
		dynamicsWorld.stepSimulation(dt);
		
		//checkFrustum();
	}
	
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
						if(req.getAction() == Action.CREATEVBO){
							Entity e = req.getEntity();
							e.createVBO();
							e.preparePhysicsModel();
							req.done();
						}
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
	
	public void render(Graphics2D g){
		//Create objects
		renderRequests();
		
		Graphics3D.updateMatrices(camera);
		Graphics3D.render(getEntities(), this);
		
		//Graphics3D.cameraPerspective();
        //glEnable(GL_DEPTH_TEST);
	    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //grid.render();
		//glDisable(GL_DEPTH_TEST);
		
	    //Render 2D stuff
	    /*Graphics3D.perspective2D();
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
		Graphics3D.dispose();
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
	
	public void linkCamera(Camera cam){
		camera.setImportant(cam);
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
