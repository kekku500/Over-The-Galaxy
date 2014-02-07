package main;

import game.RenderState;
import game.State;
import game.threading.RenderThread;
import game.vbotemplates.AbstractVBO;
import game.vbotemplates.CuboidVBO;
import game.vbotemplates.LineVBO;
import game.vbotemplates.PointVBO;
import game.world.World;
import game.world.entities.DefaultEntity;
import game.world.entities.Entity;
import game.world.entities.Entity.Motion;
import game.world.entities.Line;
import game.world.entities.Point;
import game.world.entities.Player;
import game.world.gui.Container;
import game.world.gui.Rectangle;
import game.world.gui.graphics.Graphics;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.UpdateRequest;
import game.world.sync.Request.Action;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;




import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import test.OBJloader.MTLLoader;
import test.OBJloader.Material;
import test.OBJloader.Model;
import test.OBJloader.OBJLoader;
import utils.Utils;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import controller.Camera;
import controller.Camera;

public class PlayState extends State{
	
	private int stateId;
	
	private Player player;
	
	public PlayState(int stateId){
		this.stateId = stateId;
	}
	
	Model model;
	Material material;
	
	@Override
	public void init() {
		Main.debugPrint("PlayState init");
		World world = this.getUpToDateState().getWorld();

		player = new Player(25,20,15);
		
		world.addEntity(player);
		
		Request request = new UpdateRequest(Action.CAMERAFOCUS, player);
		getSyncManager().add(request);

		//world.addComponent(new Rectangle(new Vector2f(100,100), 200, 50));*/
		
		
		Entity testObject = new DefaultEntity();
		//visual
		AbstractVBO testModel = new CuboidVBO(10,10,10);
		testObject.setModel(testModel);
		//physics
		CollisionShape testShape = new BoxShape(new Vector3f(10/2, 10/2, 10/2));
		MotionState testMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(0,0,0,1),
				new Vector3f(0,14,20), 1.0f)));
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
		testObject.setStatic();
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
		
		//GL11.glColor4f(1, 1, 1, 1);
	    world.render(g);
	    //GL11.glColor4f(1, 1, 1, 1);
	    /*if(model == null){
			try {
				model = OBJLoader.loadModel(new File("D:\\Programming\\eclipse\\workspaces\\java\\github\\Over-The-Galaxy\\src\\resources\\mees.obj"));
			} catch (FileNotFoundException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.prepareVBO();
	    }*/
	    //model.Render();
	}

	@Override
	public void dispose(){
		World world = getUpToDateState().getWorld();
		world.dispose();
		//container.dispose();
	}
	
	@Override
	public int getId() {
		return stateId;
	}

}
