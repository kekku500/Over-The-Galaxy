package world;

import input.InputReciever;

import java.util.ListIterator;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import main.PlayState;
import resources.Resources;
import resources.texture.Texture;
import state.Game;
import state.RenderState;
import state.State;
import state.StateVariable;
import state.threading.RenderThread;
import utils.ArrayList;
import utils.HashSet;
import utils.R;
import utils.math.Vector3f;
import world.culling.Octree;
import world.entity.Entity;
import world.entity.VisualEntity;
import world.entity.create.DynamicEntity;
import world.entity.lighting.Lighting;
import world.entity.lighting.SunLight;
import world.entity.smart.Player;
import world.graphics.Graphics2D;
import world.graphics.Graphics3D;
import world.gui.Component;
import world.sync.Request;
import world.sync.Request.Action;
import world.sync.Request.Status;
import world.sync.RequestList;

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
import controller.Controller;

public class EntityManager{
	
	//World info
	private PlayState state;
	
	private Level currentLevel;
	
	//2D Entities (GUI)
	private java.util.Set<Component> components = new HashSet<Component>();
	
	private int worldSize = 100000;
	Octree<VisualEntity> staticOctree;
	
	public EntityManager(PlayState state){
		
		currentLevel = new Level();
		this.state = state;
		
		//Octree
		staticOctree = new Octree<VisualEntity>(worldSize);
	}
	
	public void update(float dt){
		updateRequests();
		
		for(Entity e: getWorldEntities(RenderState.getUpdatingId())){
			e.update(dt);
		}
	}
	private void entityUpdateRequest(Request<?> req){
		Entity e = (Entity)req.getItem();
		if(req.getAction() == Action.ADD){
			if(e instanceof InputReciever){ //input objects
				getState().getInput().addInput((InputReciever)e);
			}
			
			if(e instanceof DynamicEntity)
				getDynamicEntities(RenderState.getUpdatingId()).add((DynamicEntity)e);
			
			if(e instanceof Entity){ //3d objects
				getWorldEntities(RenderState.getUpdatingId()).add((Entity)e);
				if(e instanceof VisualEntity && !(e instanceof SunLight)){
					getVisualEntities(RenderState.getUpdatingId()).add((VisualEntity)e);
				}
				if(e instanceof Lighting)
					getLightingEntities(RenderState.getUpdatingId()).add((Lighting)e);
			}else if(e instanceof Component){ //2d objects
				components.add((Component)e);
			}
		}else if(req.getAction() == Action.REMOVE){
			if(e instanceof InputReciever){ //input objects
				getState().getInput().removeInput((InputReciever)e);
			}
			
			if(e instanceof DynamicEntity)
				getDynamicEntities(RenderState.getUpdatingId()).remove((DynamicEntity)e);
			
			if(e instanceof Entity){ //3d objects
				getWorldEntities(RenderState.getUpdatingId()).remove((Entity)e);
				if(e instanceof VisualEntity && !(e instanceof SunLight)){
					getVisualEntities(RenderState.getUpdatingId()).remove((VisualEntity)e);
				}
				if(e instanceof Lighting)
					getLightingEntities(RenderState.getUpdatingId()).remove((Lighting)e);
			}	
		}
	}

	public void updateRequests(){
		RequestList syncM = state.getRequestList();
		ListIterator<Request<?>> itr = syncM.getUpdateRequests().listIterator();
		while(itr.hasNext()){
			Request<?> req = itr.next();
			Status status = req.requestStatus();
			//System.out.println(req + " Status " + status + " at world " + uniqueID + " -> " + req.changedWorlds);
			if(status == Status.IDLE)
				continue; //skip, request must be handled later or is already done in this state
			
			if(req.getItem() instanceof Entity){
				entityUpdateRequest(req);
			}
			
			if(status == Status.FINAL){
				itr.remove();
			}
		}
	}
	
	public void render(){
		Graphics3D.render(this);
	
		Graphics3D.perspective3D(Display.getWidth(), Display.getHeight());
		Graphics3D.renderAxes(state);

	    //Render 2D stuff
	    /*Graphics2D.perspective2D();

	    for(Component c: components){
	    	c.render();
	    }
	    
	    Graphics2D.setFontSize(18);
	    
	    Graphics2D.drawString(50, 50, "EPIC MAN");

	    
	    Graphics2D.setFontSize(20);
	    
	    Graphics2D.drawString(100, 50, "DEFAULT" + 50);
	    
	    //RenderThread.graphics2D.drawTexture(id);
	    
	    Texture tex = null;
		try {
			tex = Resources.getTexture("smiley.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Graphics2D.drawTexture(tex, 10, 200, 1/4f, 1/4f);
	    
		//Graphics2D.drawTexture(tex, 20, 210, 1/4f, 1/4f);
	    
		//Graphics2D.drawTexture(tex, 80, 200, 1/4f, 1/4f, .5f);
		
		Graphics2D.drawTexture(tex, 150, 200, 1/4f, 1/4f, 70);*/
	}
	
	public void addEntity(Entity e){
		Request<Entity> adding = new Request<Entity>(Action.ADD, e);

		state.getRequestList().add(adding);
	}
	
	public void removeEntity(Entity e){
		Request<Entity> adding = new Request<Entity>(Action.REMOVE, e);

		state.getRequestList().add(adding);
	}
	
	/**
	 * Removed entity by it's id.
	 * @return Returns removed entity
	 */
	public Entity removeEntity(Entity rem, HashSet<Entity> list){
		for(Entity e: list){
			if(e == rem){
				list.remove(e);
				return e;
			}
		}
		return null;
	}
	
	public ArrayList<VisualEntity> getVisualEntities(int id){
		return currentLevel.visualEntites.vars.get(id);
	}
	
	public HashSet<DynamicEntity> getDynamicEntities(int id){
		return currentLevel.dynamicEntities.vars.get(id);
	}
	
	public HashSet<Entity> getWorldEntities(int id){
		return currentLevel.entities.vars.get(id);
	}
	
	public ArrayList<Lighting> getLightingEntities(int id){
		return currentLevel.lightingEntities.vars.get(id);
	}
	
	public Octree<VisualEntity> getStaticOctree() {
		return staticOctree;
	}
	
	public void storeDynamicEntitesPreviousTransform(){
		for(DynamicEntity e: getDynamicEntities(RenderState.getUpdatingId())){
			e.storePreviousTransform();
		}
	}
	
	public void interpolateDynamicEntities(float alpha){
		for(DynamicEntity e: getDynamicEntities(RenderState.getUpdatingId())){
			e.interpolate(alpha);
		}
	}
	
	public PlayState getState(){
		return state;
	}
	
	private int objectIdCounter;
	int generateGameObjectId(){
		return objectIdCounter++;
	}
	
	
}
