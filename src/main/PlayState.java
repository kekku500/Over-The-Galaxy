package main;

import game.Game;
import game.State;
import game.world.World;
import game.world.entities.AbstractEntity;
import game.world.entities.DefaultEntity;
import game.world.entities.Entity;
import game.world.entities.LightSource;
import game.world.entities.Player;
import game.world.entities.LightSource.LightType;
import game.world.graphics.Graphics2D;
import game.world.graphics.ShadowMapper;
import game.world.gui.Rectangle;
import game.world.sync.Request;
import game.world.sync.Request.Action;
import game.world.sync.UpdateRequest;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import utils.Utils;
import utils.math.Vector3f;
import utils.math.Vector4f;
import blender.model.Model;
import blender.model.custom.Cuboid;
import blender.model.custom.Plane;
import blender.model.custom.Sphere;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

public class PlayState extends State{
	
	private int stateId;
	

	
	public PlayState(int stateId){
		this.stateId = stateId;
	}
	
	Model model;

	@Override
	public void init() {
		Game.print("PlayState init");
		World world = this.getUpToDateState().getWorld();

		Player player = new Player(25,20,15);
		
		world.addEntity(player);

		//world.setCameraFocus(player);
		
		float w = 5, h = 5, d = 5;
		AbstractEntity testBox = null;
		testBox = new DefaultEntity();
		//visual
		Model boxModel = new Cuboid(w,h,d);
		testBox.setModel(boxModel);
		
		CollisionShape shape = new BoxShape(new Vector3f(w/2, h/2, d/2));
		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(10,5f,10), 1)));
		Vector3f intertia = new Vector3f();
		shape.calculateLocalInertia(5f,  intertia);
		RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(5f, motionState, shape, intertia);
		constructionInfo.restitution = 0.1f;
		constructionInfo.friction = 0.95f;
		//RigidBody body = new RigidBody(constructionInfo);
		
		//testBox.setDynamic();
		//body.activate();
		
		testBox.setTag(1);
		
		testBox.createRigidBody(constructionInfo);
		//testBox.setRigidBody(body);
		//testBox.setStatic(); 
		//System.out.println("SET DYNAMIC " + testBox.setDynamic());
		world.addEntity(testBox);
		
		
		
		
		


		world.addComponent(new Rectangle(new Vector2f(100,100), 200, 50));
		
		//Random man
		Entity testObject = new DefaultEntity();
		//visual
		//AbstractVBO testModel = new CuboidVBO(2,4,2);
		//testObject.setModel(testModel);
		try {
			Model model2 = new Model("superman\\mees.obj");
			testObject.setModel(model2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//physics
		CollisionShape testShape = new BoxShape(new Vector3f(2/2, 4/2, 2/2));
		MotionState testMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(-30,10,0), 1.0f)));
		Vector3f ballInertia = new Vector3f(0,0,0);
		testShape.calculateLocalInertia(2.5f, ballInertia);
		RigidBodyConstructionInfo testConstructionInfo = new RigidBodyConstructionInfo(2.5f, testMotionState, testShape, ballInertia);
		testConstructionInfo.restitution = 0.5f;
		testConstructionInfo.angularDamping = 0.95f;
		testConstructionInfo.friction = 0.95f;
		//testBody = new RigidBody(testConstructionInfo);
		testObject.createRigidBody(testConstructionInfo);
		//testObject.setRigidBody(testBody);
		//testObject.setRigidBodyConstructionInfo(testConstructionInfo);
		world.addEntity(testObject);
		
		//Brick normal map
		Entity testNormalMap = new DefaultEntity();
		try {
			Model normalTest = new Model("normal_map_test\\brick01g-quad.obj");
			normalTest.translate(new Vector3f(0,-2,0));
			testNormalMap.setModel(normalTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//physics
		CollisionShape brickShape = new BoxShape(new Vector3f(8/2, 4/2, 20/2));
		DefaultMotionState brickMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(0,5f,0), 1)));
		Vector3f brickInertia = new Vector3f();
		shape.calculateLocalInertia(5f,  intertia);
		RigidBodyConstructionInfo brickConstructionInfo = new RigidBodyConstructionInfo(5f, brickMotionState, brickShape, brickInertia);
		constructionInfo.restitution = 0.1f;
		constructionInfo.friction = 0.95f;
		
		testNormalMap.createRigidBody(brickConstructionInfo);
		world.addEntity(testNormalMap);
		
		///Building -------------------------------------
		Entity testSSAO = new DefaultEntity();
		testSSAO.getMotionState().origin.set(0,43.85f,0);
		try {
			Model normalTest = new Model("ssao_test\\hight_rise_build-tri.obj");
			normalTest.scale(40);
			testSSAO.setModel(normalTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		world.addEntity(testSSAO);
		
		//GROUND
		Entity testGround = new DefaultEntity();
		//visual
		Plane testModel = new Plane(1000,0,1000);
		testGround.setModel(testModel);

		//physics
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0,1,0), 0.25f);
		MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1), 
				new Vector3f(0,0,0), 1.0f)));
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0,0,0));
		groundBodyConstructionInfo.restitution =  0.1f;
		groundBodyConstructionInfo.friction = .8f;
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		
		testGround.setRigidBody(groundRigidBody);
		world.addEntity(testGround);

		//Add random points
		/*int totalPoints = 200;
		
		int width = 400; //pixels
		
		int starty = 20;
		int height = 200; //pixels (from y=starty)
		
		int depth = 400; //pixels
		
		Random rand = new Random();
		
		PointVBO oneVBO = new PointVBO();
		
		//First
		Point p = new Point(new Vector3f((rand.nextFloat() - 0.5f)*width,starty+(rand.nextFloat())*height,rand.nextInt(depth) - (int)(depth/2)));
		p.setModel(oneVBO);
		Request req = world.addEntity(p);

		//Other
		for(int i=0;i<totalPoints;i++){
			p = new Point(new Vector3f((rand.nextFloat() - 0.5f)*width,starty+(rand.nextFloat())*height,rand.nextInt(depth) - (int)(depth/2)));
			p.setModel(oneVBO);
			world.addEntity(p, req);
		}*/
		
		LightSource testLightSource = new LightSource(false);
		///test cube light
		testLightSource.setAmbient(new Vector4f(0.4f,0.4f,0.4f,1.0f));
		Sphere s = new Sphere(8.75f, 16, 16);
		s.isGodRays = true;
		testLightSource.setModel(s);

		testLightSource.setLightType(LightType.POINT);
		
		//testLightSource.setPos(new Vector3f(0, 50, 300));
		//testLightSource.setShadowMapper(new ShadowMapper(false));
		
		testLightSource.setPos(new Vector3f(0, 75, 0));
		testLightSource.setShadowMapper(new ShadowMapper(true));
		
		world.addEntity(testLightSource);
	}

	@Override
	public void update(float dt){

		World world = getUpdatingState().getWorld();
	    world.update(dt);
	    //container.update();
	}
	

	@Override
	public void renderInit() {

		
	}
	
	@Override
	public void render(){
		World world = getRenderingState().getWorld();
		GL11.glColor4f(1,1,1,1);
		world.render(); 
	}

	@Override
	public void dispose(){
		World world = getUpToDateState().getWorld();
		world.dispose();
		//container.dispose();
		//model.dispose();
	}
	
	@Override
	public int getId() {
		return stateId;
	}

}
