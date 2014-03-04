package main;

import game.Game;
import game.State;
import game.world.World;
import game.world.entities.DefaultEntity;
import game.world.entities.Entity;
import game.world.entities.Player;
import game.world.gui.Rectangle;
import game.world.gui.graphics.Graphics2D;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import blender.model.Model;
import blender.model.custom.Cuboid;
import blender.model.custom.Plane;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class PlayState extends State{
	
	private int stateId;
	
	private Player player;
	
	public PlayState(int stateId){
		this.stateId = stateId;
	}
	
	Model model;
	
	@Override
	public void init() {
		Game.print("PlayState init");
		World world = this.getUpToDateState().getWorld();

		player = new Player(25,20,15);
		
		world.addEntity(player);
		
		//testing shadow
		float w = 5, h = 5, d = 5;
		Entity testBox = new DefaultEntity();
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
		RigidBody body = new RigidBody(constructionInfo);
		testBox.setDynamic();
		body.activate();

		testBox.setRigidBody(body);
		world.addEntity(testBox);
		
		/*Request request = new UpdateRequest(Action.CAMERAFOCUS, player);
		getSyncManager().add(request);*/

		world.addComponent(new Rectangle(new Vector2f(100,100), 200, 50));
		
		
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
		RigidBody testBody;
		testBody = new RigidBody(testConstructionInfo);
		testObject.setRigidBody(testBody);
		testObject.setRigidBodyConstructionInfo(testConstructionInfo);
		testObject.createPhysicsModel();
		world.addEntity(testObject);
		
		
		Entity testNormalMap = new DefaultEntity();
		testNormalMap.getMotionState().origin.set(0,2,0);
		try {
			Model normalTest = new Model("normal_map_test\\brick01g.obj");
			testNormalMap.setModel(normalTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		world.addEntity(testNormalMap);
		
		
		Entity testGround = new DefaultEntity();
		testGround.setGroud(true);
		//visual
		Plane testModel = new Plane(500,0,500);
		
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
	}

	@Override
	public void update(float dt){

		World world = getUpdatingState().getWorld();
	    world.update(dt);
	    //container.update();
	}
	
	@Override
	public void render(Graphics2D g){
		World world = getRenderingState().getWorld();
		GL11.glColor4f(1,1,1,1);
		world.render(g); 
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
