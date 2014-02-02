package game.world;

import static org.lwjgl.input.Keyboard.KEY_DOWN;
import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;
import static org.lwjgl.input.Keyboard.KEY_UP;
import static org.lwjgl.input.Keyboard.isKeyDown;
import game.State;
import game.threading.RenderThread;
import game.world.FrustumCulling.Frustum;
import game.world.entities.Entity;
import game.world.entities.Entity.Motion;
import game.world.entities.Line;
import game.world.entities.Player;
import game.world.gui.Component;
import game.world.gui.Container;
import game.world.gui.graphics.Graphics;
import game.world.subworlds.DynamicWorld;
import game.world.subworlds.PhysicsWorld;
import game.world.subworlds.StaticWorld;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Type;
import game.world.sync.SyncManager;
import game.world.sync.Request.Status;
import game.world.sync.UpdateRequest;
import game.world.sync.Request.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import main.Main;
import main.PlayState;
import controller.Camera;

public class World extends AbstractWorld{
	
	//Sub-Worlds
	private StaticWorld staticWorld = new StaticWorld();
	private DynamicWorld dynamicWorld = new DynamicWorld();
	private PhysicsWorld physicsWorld = new PhysicsWorld();
	
	//GUI
	private Container container;

	private int entityIdCounter = 0;
	private int componentIdCounter = 0;
	
	//Camera stuff
	private FrustumCulling frustum;
	private Camera camera = new Camera(0,30,0);
	
	private State state;
	
	public Grid grid;
	
	private int uniqueID;
	private int id;
	private static int idCounter = 0;
	
	public World(State state, int id){
		uniqueID = idCounter;
		idCounter++;
		this.id = id;
		this.state = state;
		grid = new Grid(this);
		frustum = new FrustumCulling(camera);
		container = new Container();
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
	
	public void addEntity(Entity e){
		entityIdCounter++;
		e.setId(entityIdCounter);
		e.setWorld(this);
		Request request = new UpdateRequest(Action.ADD, e);
		Request request2 = new RenderRequest(Action.CREATEVBO, e);
		request.waitFor(request2);
		state.getSyncManager().add(request);
		state.getSyncManager().add(request2);
	}
	
	public State getState(){
		return state;
	}
	
	public void updateRequests(){
		SyncManager syncM = state.getSyncManager();
		ListIterator<UpdateRequest> itr = syncM.getUpdateRequests().listIterator();
		while(itr.hasNext()){
			UpdateRequest req = itr.next();
			Status status = req.requestStatus(this);
			//System.out.println(req + " Status " + status + " at world " + getID() + " -> " + req.changedWorlds);
			if(status == Status.IDLE)
				continue; //skip, request must be handled later
			if(req.getType() == Type.ENTITY){
				Entity e = req.getEntity();
				if(e.getMotion() == Motion.STATIC){
					if(req.getAction() == Action.ADD){
						staticWorld.addEntity(e.copy());
					}
				}else if(e.getMotion() == Motion.DYNAMIC){
					if(req.getAction() == Action.ADD){
						dynamicWorld.addEntity(e.copy());
					}else if(req.getAction() == Action.UPDATE || req.getAction() == Action.UPDATEALL){
						dynamicWorld.replace(e.copy());
					}
				}else if(e.getMotion() == Motion.PHYSICS){
					if(req.getAction() == Action.ADD){
						physicsWorld.addEntity(e.copy());
					}else if(req.getAction() == Action.UPDATE || req.getAction() == Action.UPDATEALL){
						physicsWorld.replace(e.copy());
					}
				}
				if(status == Status.FINAL){
					itr.remove();
				}
			}else if(req.getType() == Type.COMPONENT){
				Component c = req.getComponent();
				if(req.getAction() == Action.ADD){
					container.addComponent(c);
				}
				if(status == Status.FINAL){
					itr.remove();
				}
			}

			
		}
	}
		
	public List<Entity> getEntities(){
		List<Entity> allEntities = new ArrayList<Entity>();
		allEntities.addAll(staticWorld.getEntities());
		allEntities.addAll(dynamicWorld.getEntities());
		allEntities.addAll(physicsWorld.getEntities());
		return allEntities;
	}
	
	public void update(float dt){
		//Check for added entities
		updateRequests();
		
		camera.update(dt);
		
		grid.update();
		
		dynamicWorld.update(dt);
		physicsWorld.update(dt);
		
		checkFrustum();
	}
	
	public void checkFrustum(){
		float[] visibleCount = {0,0};
		frustum.update(); //Update variables required for culling check
		for(Entity e: getEntities()){
			if(frustum.inView(e) != Frustum.OUTSIDE){ //draw object if it's not outside of frustum
				e.setVisible(true);
				visibleCount[0]++;
			}else{
				e.setVisible(false);
				visibleCount[1]++;
			}
		}
		//System.out.println("Visible " + visibleCount[0] + " Outside " + visibleCount[1]);
	}
	
	public void renderRequests(){
		SyncManager syncM = state.getSyncManager();
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
		
		staticWorld.render(g);
		dynamicWorld.render(g);
		physicsWorld.render(g);
		
	    //Render 2D stuff
	    RenderThread.perspective2D();
	    
	    container.render();
	    
	    g.setFontSize(18);
	    
	    g.drawString(500, 50, "EPIC MAN" + 50, Color.red);

	    g.setFontSize(20);
	    
	    g.drawString(100, 50, "DEFAULT" + 50);
	    
	    //Back to 3D
	    RenderThread.perspective3D(); //reset perspective to 3d
	}
	
	public void dispose(){
		staticWorld.dispose();
		dynamicWorld.dispose();
		physicsWorld.dispose();
		container.dispose();
	}
	
	public Camera getCamera(){
		return camera;
	}
	
	public void setCamera(Camera cam){
		camera = cam;
		frustum.setCamera(cam);
	}
	
	public int getID(){
		return id;
	}
	
	public int getUniqueID(){
		return uniqueID;
	}
	
}
