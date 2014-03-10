package main;

import game.Game;
import game.State;
import game.world.World;
import game.world.entities.DefaultEntity;
import game.world.entities.Entity;
import game.world.entities.LightSource;
import game.world.entities.LightSource.LightType;
import game.world.entities.Player;
import game.world.gui.Rectangle;
import game.world.gui.graphics.Graphics2D;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import utils.Utils;
import utils.math.Vector3f;
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
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;

public class PlayState extends State{
	
	private int stateId;
	
	private Player player;
	
	public PlayState(int stateId){
		this.stateId = stateId;
	}
	
	Model model;
	public static Entity testBox = null;
	@Override
	public void init() {
		Game.print("PlayState init");
		World world = this.getUpToDateState().getWorld();

		player = new Player(25,20,15);
		world.addEntity(player);
		
		//GROUND ---------------------
		Entity testGround = new DefaultEntity();
		testGround.setGroud(true);
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
		
		//Test Light Source
		LightSource testLightSource = new LightSource(false);
		testLightSource.setLightType(LightType.DIRECTIONAL);
		testLightSource.setPos(new Vector3f(800, 400, 800));
		world.addEntity(testLightSource);
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
