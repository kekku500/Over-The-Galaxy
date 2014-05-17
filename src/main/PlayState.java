package main;

import graphics.Graphics2D;
import graphics.gui.HUDManager;
import graphics.gui.hud.HeadsUpDisplay;
import graphics.gui.mapeditor.MapEditor;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import main.state.Game;
import main.state.State;
import math.Vector3f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import resources.Resources;
import resources.model.Model;
import resources.model.custom.Sphere;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import entity.creation.Controller;
import entity.creation.Controller.CamType;
import entity.creation.DefaultPointLight;
import entity.creation.ModeledEntity;
import entity.creation.Player;
import entity.creation.StaticEntity;
import entity.creation.SunLight;
import entity.sheet.Entity;
import entitymanager.EntityManager;
import entitymanager.Level;

public class PlayState extends State{
	
	private int stateId;
	
	private float accumulator;
	
	private DynamicsWorld dynamicsWorld;
	private EntityManager entityManager;
	
	private Controller camera;
	private Player player;
	
	private MapEditor mapEditorUI;
	
	private HUDManager hudManager;
	private HeadsUpDisplay HUD;

	public PlayState(int stateId){
		this.stateId = stateId;
		
		hudManager = new HUDManager(this);
		entityManager = new EntityManager(this);
		
		//Physics
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
		
	
	}
	
	@Override
	public void init() {
		Game.println("PlayState init");
		if(!Config.NO_GAME_RENDER){
		player = new Player(null, 15,30,35);
		//world.addEntity(player);
		
		HUD = new HeadsUpDisplay(player, hudManager);
	    HUD.init();
	    
	    
		/*camera = new Controller(entityManager, 10,10,10);
		camera.setViewport(Config.VIEWPORT_WIDTH, Config.VIEWPORT_HEIGHT);
		camera.setProjection(Config.FOV, (float) Config.VIEWPORT_WIDTH / (float) Config.VIEWPORT_HEIGHT, Config.Z_NEAR, Config.Z_FAR);	
		camera.setUpdatePriority((short)-5);
		camera.setFollowing(player);
		camera.setType(CamType.LOCK);*/
		

		}
		//UI
		if(Config.MAP_EDITOR_MODE){
			mapEditorUI = new MapEditor(this);
			
			for(String modelName: Resources.getModels().keySet()){
				mapEditorUI.entityCreator.addObject(modelName);
			}
			
			for(String levelPath: Resources.getLevels()){
				mapEditorUI.levelLoader.addLevel(levelPath);
			}
			
			getGameUI().add(mapEditorUI);
		}

		//load level
		entityManager.getLevel().loadNewEntities(
				Level.loadLevelEntities("demo2.lev"));
	}

	@Override
	public void update(float dt){
		input();
		
		
		accumulator += dt; //How much time must be updated

		//Updating physics world by fixed timestep
		int stepCount = (int)(accumulator/Config.TARGET_STEP);
		for(int i = 1; accumulator >= Config.TARGET_STEP; i++){
			if(i == stepCount){
				entityManager.storeDynamicEntitesPreviousTransform();
			}
			//System.out.println(framesBehind + " <- " + accumulator);
			dynamicsWorld.stepSimulation(Config.TARGET_STEP);
			accumulator -= Config.TARGET_STEP;
		}
		
		//Interpolate physics result
		entityManager.interpolateDynamicEntities(accumulator / Config.TARGET_STEP);
		
		
		
		entityManager.update(dt);
		if(!Config.MAP_EDITOR_MODE){
			hudManager.update(dt);
		}
		if(Config.MAP_EDITOR_MODE){
			mapEditorUI.update();
		}
		
	}
	
	private void input(){
    	if(Mouse.isButtonDown(1)){
    		Mouse.setGrabbed(true);
    	}else if(Mouse.isButtonDown(0)){
    		Mouse.setGrabbed(false);
    	}
	}
	
	@Override
	public void render(){
		
		entityManager.render(); 
		
		if(!Config.MAP_EDITOR_MODE){
			Graphics2D.perspective2D(getCamera().viewportWidth, getCamera().viewportHeight);
			
			hudManager.render();
		}
	}

	@Override
	public void dispose(){
	}
	
	@Override
	public int getId() {
		return stateId;
	}
	
	public EntityManager getWorld(){
		return entityManager;
	}
	
	public DynamicsWorld getDynamicsWorld(){
		return dynamicsWorld;
	}
	
	public Controller getCamera(){
		return camera;
	}
	
	public void setCamera(Controller c){
		camera = c;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public void setPlayer(Player p){
		player = p;
	}

	@Override
	public void resized(float width, float height) {
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}

}
