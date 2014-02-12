package main;

import game.State;
import game.vbotemplates.AbstractVBO;
import game.vbotemplates.CuboidVBO;
import game.vbotemplates.PointVBO;
import game.world.World;
import game.world.entities.DefaultEntity;
import game.world.entities.Entity;
import game.world.entities.Entity.Motion;
import game.world.entities.Player;
import game.world.entities.Point;
import game.world.gui.Rectangle;
import game.world.gui.graphics.Graphics;
import game.world.sync.Request;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import blender.model.Model;
import blender.model.OBJLoader;

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
		Main.debugPrint("PlayState init");
		World world = this.getUpToDateState().getWorld();

		player = new Player(25,20,15);
		
		world.addEntity(player);
		
		/*Request request = new UpdateRequest(Action.CAMERAFOCUS, player);
		getSyncManager().add(request);*/

		world.addComponent(new Rectangle(new Vector2f(100,100), 200, 50));
		
		
		Entity testObject = new DefaultEntity();
		//visual
		//AbstractVBO testModel = new CuboidVBO(2,4,2);
		//testObject.setModel(testModel);
		try {
			AbstractVBO model2 = new Model("src\\resources\\mees.obj");
			System.out.println("created model " + model2);
			testObject.setModel(model2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//physics
		CollisionShape testShape = new BoxShape(new Vector3f(2/2, 4/2, 2/2));
		MotionState testMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(0,5,0), 1.0f)));
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
		//testObject.setStatic();
		testObject.setDynamic();
		testObject.createPhysicsModel();
		world.addEntity(testObject);
		
		
		Entity testGround = new DefaultEntity();
		testGround.setMotion(Motion.STATIC);
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0,1,0), 0.25f);
		MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1), 
				new Vector3f(0,0,0), 1.0f)));
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0,0,0));
		groundBodyConstructionInfo.restitution =  0.25f;
		groundBodyConstructionInfo.friction = .8f;
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		
		testGround.setRigidBody(groundRigidBody);
		world.addEntity(testGround);

		//Add random points
		int totalPoints = 200;
		
		int width = 400; //pixels
		
		int starty = 20;
		int height = 200; //pixels (from y=starty)
		
		int depth = 400; //pixels
		
		Random rand = new Random();
		
		PointVBO oneVBO = new PointVBO();
		
		//First
		Point p = new Point(new Vector3f((rand.nextFloat() - 0.5f)*width,starty+(rand.nextFloat())*height,rand.nextInt(depth) - (int)(depth/2)));
		p.setModel(oneVBO);
		p.setMotion(Motion.STATIC);
		Request req = world.addEntity(p);

		//Other
		for(int i=0;i<totalPoints;i++){
			p = new Point(new Vector3f((rand.nextFloat() - 0.5f)*width,starty+(rand.nextFloat())*height,rand.nextInt(depth) - (int)(depth/2)));
			p.setModel(oneVBO);
			p.setMotion(Motion.STATIC);
			world.addEntity(p, req);
		}
	}

	@Override
	public void update(float dt){

		World world = getUpdatingState().getWorld();
	    world.update(dt);
	    //container.update();
	}
	
	@Override
	public void render(Graphics g){
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
