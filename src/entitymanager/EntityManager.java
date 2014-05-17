package entitymanager;

import input.InputHandler;

import java.io.Serializable;
import java.util.Collections;
import java.util.ListIterator;

import main.PlayState;
import main.state.RenderState;

import org.lwjgl.opengl.Display;

import utils.ArrayList;
import utils.HashSet;
import utils.LinkedList;
import entity.blueprint.AbstractVisualPhysicsEntity;
import entity.creation.DynamicEntity;
import entity.creation.SunLight;
import entity.sheet.Entity;
import entity.sheet.Lighting;
import entity.sheet.VisualEntity;
import entitymanager.Request.Action;
import entitymanager.Request.Status;
import graphics.Graphics3D;
import graphics.culling.Octree;
import graphics.gui.Component;

public class EntityManager{
	
	private ArrayList<Request<Entity>> entityRequests = new ArrayList<Request<Entity>>();
	
	//World info
	private PlayState state;
	
	private Level currentLevel;
	
	//2D Entities (GUI)
	private java.util.Set<Component> components = new HashSet<Component>();
	
	private int worldSize = 100000;
	Octree<VisualEntity> staticOctree;
	
	private LinkedList<EntitySelectionRequest> entitySelectionRequests = new LinkedList<EntitySelectionRequest>();
	
	public EntityManager(PlayState state){
		
		currentLevel = new Level(this);
		this.state = state;
		
		//Octree
		staticOctree = new Octree<VisualEntity>(worldSize);
	}
	
	public Level getLevel(){
		return currentLevel;
	}
	
	public void update(float dt){
		updateRequests();
		
		//System.out.println("start");
		for(Entity e: getWorldEntities(RenderState.updating())){
			e.update(dt);
			//System.out.println(e);
		}
	}
	private void entityUpdateRequest(Request<Entity> req, Status status){
		Entity e = req.getItem();
		
		if(req.getAction() == Action.ADD){
			//Dynamic World
			if(status == Status.FIRST){
				if(e instanceof AbstractVisualPhysicsEntity){
					AbstractVisualPhysicsEntity es = (AbstractVisualPhysicsEntity)e;
					getState().getDynamicsWorld().addRigidBody(es.getBody());
				}
			}
			
			//InputListener implemented entities
			if(e instanceof InputHandler){ //input objects
				getState().getInput().addInput((InputHandler)e);
			}
			
			//Entities
			getWorldEntities(RenderState.updating()).add((Entity)e);
			
			//Moving entities
			if(e instanceof DynamicEntity)
				getDynamicEntities(RenderState.updating()).add((DynamicEntity)e);

			//All visible entities except sunlight
			if(e instanceof VisualEntity && !(e instanceof SunLight)){
				getVisualEntities(RenderState.updating()).add((VisualEntity)e);
			}
			
			//All lights
			if(e instanceof Lighting){
				getLightingEntities(RenderState.updating()).add((Lighting)e);
			}
		}else if(req.getAction() == Action.REMOVE){
			if(status == Status.FIRST){
				if(e instanceof AbstractVisualPhysicsEntity){
					AbstractVisualPhysicsEntity es = (AbstractVisualPhysicsEntity)e;
					
					es.getBody().destroy();
					getState().getDynamicsWorld().removeRigidBody(es.getBody());
				}
			}
			
			if(e instanceof InputHandler){ //input objects
				getState().getInput().removeInput((InputHandler)e);
			}
			
			getWorldEntities(RenderState.updating()).remove((Entity)e);
			
			if(e instanceof DynamicEntity)
				getDynamicEntities(RenderState.updating()).remove((DynamicEntity)e);
			
			
			if(e instanceof VisualEntity && !(e instanceof SunLight)){
				getVisualEntities(RenderState.updating()).remove((VisualEntity)e);
			}
			if(e instanceof Lighting)
				getLightingEntities(RenderState.updating()).remove((Lighting)e);
			

		}
	}

	public void updateRequests(){
		ListIterator<Request<Entity>> itr = entityRequests.listIterator();
		boolean listChanged = false;
		while(itr.hasNext()){
			Request<Entity> req = itr.next();
			Status status = req.requestStatus();
			//System.out.println(req + " Status " + status + " at world " + uniqueID + " -> " + req.changedWorlds);
			if(status == Status.IDLE)
				continue; //skip, request must be handled later or is already done in this state
			
			entityUpdateRequest(req, status);
			listChanged = true;
			
			if(status == Status.FINAL){
				itr.remove();
			}
		}
		if(listChanged)
			Collections.sort(getWorldEntities(RenderState.updating()));
	}
	
	public void render(){
		Graphics3D.render(this);
	
		//Graphics3D.perspective3D(Display.getWidth(), Display.getHeight());
		//Graphics3D.renderAxes(state);

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

		entityRequests.add(adding);
	}
	
	public void removeEntity(Entity e){
		Request<Entity> removing = new Request<Entity>(Action.REMOVE, e);

		entityRequests.add(removing);
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
	
	public ArrayList<Entity> getWorldEntities(int id){
		return currentLevel.entities.vars.get(id);
	}
	
	public ArrayList<Lighting> getLightingEntities(int id){
		return currentLevel.lightingEntities.vars.get(id);
	}
	
	public Octree<VisualEntity> getStaticOctree() {
		return staticOctree;
	}
	
	public void storeDynamicEntitesPreviousTransform(){
		for(DynamicEntity e: getDynamicEntities(RenderState.updating())){
			e.storePreviousTransform();
		}
	}
	
	public void interpolateDynamicEntities(float alpha){
		for(DynamicEntity e: getDynamicEntities(RenderState.updating())){
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
	
	public void addEntitySelectionRequest(EntitySelectionRequest req){
		entitySelectionRequests.add(req);
	}
	
	public LinkedList<EntitySelectionRequest> getEntitySelectionRequests(){
		return entitySelectionRequests;
	}
	
}
