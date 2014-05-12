package main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import resources.Resources;
import resources.model.Model;
import resources.model.custom.Sphere;
import state.Game;
import state.State;
import utils.LinkedList;
import utils.math.Vector3f;
import utils.math.Vector4f;
import world.EntityManager;
import world.entity.create.DynamicEntity;
import world.entity.create.ModeledEntity;
import world.entity.create.StaticEntity;
import world.entity.lighting.DefaultPointLight;
import world.entity.lighting.SunLight;
import world.entity.smart.Player;
import world.graphics.Graphics2D;
import world.gui.Component;
import world.gui.GameUserInterface;
import world.gui.HUDManager;
import world.gui.hud.HeadsUpDisplay;
import world.gui.mapeditor.MapEditor;
import world.gui.mapeditor.ObjectSelectionRequest;

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

import controller.Camera;
import controller.Controller;

public class PlayState extends State{
	
	private int stateId;
	//private HeadsUpDisplay HUD;
	
	protected DynamicsWorld dynamicsWorld; //Physics World
	
	protected EntityManager entityManager;
	
	private float accumulator;
	
	private Controller camera;
	
	private Player player;
	
	MapEditor mapEditorUI;
	
	HUDManager hudManager;
	
	HeadsUpDisplay HUD;

	
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
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
	}
	
	Model model;
	
	@Override
	public void init() {
		Game.println("PlayState init");
		if(!Game.NO_RENDER){
		player = new Player(entityManager, 15,30,35);
		//world.addEntity(player);
		
		HUD = new HeadsUpDisplay(player, hudManager);
	    HUD.init();
	    
	    
		camera = new Controller(entityManager, 10,10,10);
		camera.setViewport(Game.viewportWidth, Game.viewportHeight);
		camera.setProjection(Game.fov, (float) Game.displayWidth / (float) Game.displayHeight, Game.zNear, Game.zFar);	
		//cam.setFollowing(player);
		//cam.setType(CamType.LOCK);
		
		//world.addEntity(cam);
		
		//Static cube light test stuff
		Model cuboid = null;
		try {
			cuboid = Resources.getModel("common\\cuboid.obj");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		StaticEntity f1 = new StaticEntity(entityManager);
		f1.setPosition(100, 25, 100);
		f1.scale(50,50,5);
		f1.createBody(cuboid);
		//world.addEntity(f1);
		
		f1 = new StaticEntity(entityManager);
		f1.setPosition(100, 25, 200);
		f1.scale(50,50,5);
		f1.createBody(cuboid);
		//world.addEntity(f1);
		
		f1 = new StaticEntity(entityManager);
		f1.setPosition(150, 25, 150);
		f1.scale(5,50,50);
		f1.createBody(cuboid);
		//world.addEntity(f1);
		
		f1 = new StaticEntity(entityManager);
		f1.setPosition(100, 15, 150);
		f1.scale(6,15,6);
		f1.createBody(cuboid);
		//world.addEntity(f1);
		
		
		DefaultPointLight l1 = new DefaultPointLight(entityManager);
		l1.setConstantAttenuation(0.001f);
		l1.setLinearAttenuation(0.001f);
		l1.setQuadricAttenuation(0.0001f);
		l1.setPosition(120, 9, 160);
		l1.setUserData("light");
		l1.setShadowed(false);
		l1.setEnabled(false);
		//world.addEntity(l1);
		
		/*DynamicEntity f2 = new DynamicEntity(entityManager);
		f2.setPosition(-100, 10, 0);
		f2.scale(10,10,10);
		f2.createBody(cuboid);*/
		
		
		//--------------------
		
		/*DynamicEntity testBox = new DynamicEntity();
		testBox.setPosition(10, 15, 10);
		testBox.scale(20,8,3);
		try {
			testBox.createBody(Resources.getModel("common\\cuboid.obj"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		world.addEntity(testBox);*/
		
		
		/*int range = 500;
		int amount = 10;
		int scaleMax = 80;
		int scaleMin = 5;
		Model rockModel = null;
		try {
			rockModel = Resources.getModel("lowpolyrock\\Rock1.obj");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i=0;i<amount;i++){
			StaticEntity testRock = new StaticEntity(entityManager);
			testRock.setPosition(-range/2f+(float)Math.random()*range, -range/2f+(float)Math.random()*range, -range/2f+(float)Math.random()*range);

			float scaleVal = scaleMin+(float)Math.random()*(scaleMax-scaleMin);

			testRock.scale(scaleVal);
			testRock.rotate(new Quat4f((float)Math.random(),(float)Math.random(), (float)Math.random(),1.0f));
			
			testRock.createBody(rockModel);
		}*/

		//Brick normal map
		/*DynamicEntity testNormalMap = new DynamicEntity();
		testNormalMap.setPosition(50,-2,0);
		try {
			testNormalMap.createBody(Resources.getModel("normal_map_test\\brick01g-quad.obj"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		world.addEntity(testNormalMap);*/

		
		//GROUND
		StaticEntity testGround = new StaticEntity(entityManager);
		//visual
		//testGround.setModel();
		testGround.scale(1000,1,1000);

		//physics
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0,1,0), 0.25f);
		MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1), 
				new Vector3f(0,0,0), 1.0f)));
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0,0,0));
		groundBodyConstructionInfo.restitution =  0.1f;
		groundBodyConstructionInfo.friction = .8f;
		
		try {
			testGround.createBody(Resources.getModel("common\\plane.obj"), groundBodyConstructionInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		entityManager.addEntity(testGround);

		//Sun
		SunLight sun = new SunLight(entityManager);
		//sun.setDiffuse(new Vector4f(2,2,2,1));
		sun.setModel(new Sphere(100, 16, 16));
		sun.setPosition(1000, 1000, 1000);
		sun.setLightScattering(true);
		sun.setEnabled(true);
		//world.addEntity(sun);
		
		/*DefaultPointLight pointLight = new DefaultPointLight();
		pointLight.setDiffuse(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

		pointLight.setPosition(50, 50, 50);
		world.addEntity(pointLight);*/

			/*StaticEntity testRock = new StaticEntity();
			testRock.setPosition(-100, 50, -100);
			testRock.createBody(rockModel);
			world.addEntity(testRock);*/
		
		/*DefaultPointLight pointLight2 = new DefaultPointLight();
		pointLight2.setDiffuse(new Vector4f(.1f, 0.1f, 0.6f, 1.0f));
		pointLight2.setPosition(-100, 50, -100);
		world.addEntity(pointLight2);
		
		DefaultSpotLight spotLight = new DefaultSpotLight();
		spotLight.setPosition(50, 50, 50);
		world.addEntity(spotLight);*/
		}
		//UI
		mapEditorUI = new MapEditor(this);
		
		for(String modelName: Resources.getModels().keySet()){
			mapEditorUI.addObject(modelName);
		}
		
		getGameUI().add(mapEditorUI);
	}

	@Override
	public void update(float dt){
		input();
		
		mapEditorUI.update();
		
		accumulator += dt; //How much time must be updated

		//Updating physics world by fixed timestep
		int stepCount = (int)(accumulator/Game.targetStep);
		for(int i = 1; accumulator >= Game.targetStep; i++){
			if(i == stepCount){
				entityManager.storeDynamicEntitesPreviousTransform();
			}
			//System.out.println(framesBehind + " <- " + accumulator);
			dynamicsWorld.stepSimulation(Game.targetStep);
			accumulator -= Game.targetStep;
		}
		
		//Interpolate physics result
		entityManager.interpolateDynamicEntities(accumulator / Game.targetStep);
		
		entityManager.update(dt);
		if(!Game.MAP_EDITOR_MODE){
			hudManager.update(dt);
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
		GL11.glColor4f(1,1,1,1);
		entityManager.render(); 
		
		if(!Game.MAP_EDITOR_MODE){
			Graphics2D.perspective2D(Display.getWidth(), Display.getHeight());
			
			hudManager.render();
		}

	}

	@Override
	public void dispose(){
		//HUD.dispose();
		//world.dispose();
		//container.dispose();
		//model.dispose();
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
	
	public Player getPlayer(){
		return player;
	}

	@Override
	public void resized(float width, float height) {
		
	}
	
	public EntityManager getEntityManager(){
		return entityManager;
	}
	
	private LinkedList<ObjectSelectionRequest> selectionRequest = new LinkedList<ObjectSelectionRequest>();
	
	public void addObjectSelectionRequest(ObjectSelectionRequest req){
		selectionRequest.add(req);
	}
	
	public LinkedList<ObjectSelectionRequest> getSelectionRequests(){
		return selectionRequest;
	}
	
	



}
