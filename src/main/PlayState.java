package main;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import game.Game;
import game.State;
import game.resources.Resources;
import game.world.World;
import game.world.entities.DynamicEntity;
import game.world.entities.Player;
import game.world.entities.StaticEntity;
import game.world.entities.lighting.DefaultPointLight;
import game.world.entities.lighting.DefaultSpotLight;
import game.world.entities.lighting.SunLight;
import game.world.graphics.Graphics2D;
import game.world.graphics.ShadowMapper;
import game.world.gui.Rectangle;
import game.world.sync.Request;
import game.world.sync.Request.Action;
import game.world.sync.UpdateRequest;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Vector3f;
import utils.math.Vector4f;
import blender.model.Model;
import blender.model.custom.Sphere;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.collision.shapes.VertexData;
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
	public void postRenderInit() {
		Game.print("PlayState init");
		World world = this.getUpToDateState().getWorld();

		Player player = new Player(15,2,35);
		
		world.addEntity(player);

		//world.setCameraFocus(player);
		
		DynamicEntity testBox = new DynamicEntity();
		testBox.setPosition(10, 15, 10);
		testBox.scale(20,8,3);
		try {
			testBox.createBody(Resources.getModel("common\\cuboid.obj"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		world.addEntity(testBox);
		
		
		int range = 2000;
		int amount = 40;
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
			StaticEntity testRock = new StaticEntity();
			testRock.setPosition(-range/2f+(float)Math.random()*range, -range/2f+(float)Math.random()*range, -range/2f+(float)Math.random()*range);

			float scaleVal = scaleMin+(float)Math.random()*(scaleMax-scaleMin);

			testRock.scale(scaleVal);
			testRock.rotate(new Quat4f((float)Math.random(),(float)Math.random(), (float)Math.random(),1.0f));
			
			testRock.createBody(rockModel);

			world.addEntity(testRock);
		}

		
		

		world.addComponent(new Rectangle(new Vector2f(100,100), 200, 50));

		//Brick normal map
		DynamicEntity testNormalMap = new DynamicEntity();
		testNormalMap.setPosition(50,-2,0);
		try {
			testNormalMap.createBody(Resources.getModel("normal_map_test\\brick01g-quad.obj"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		world.addEntity(testNormalMap);

		
		//GROUND
		StaticEntity testGround = new StaticEntity();
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
		world.addEntity(testGround);

		//Sun
		SunLight sun = new SunLight();
		sun.setShadowMapper(new ShadowMapper(true));
		sun.setModel(new Sphere(80, 16, 16));
		sun.setPosition(1000, 1000, 1000);
		sun.setLightScattering(true);
		world.addEntity(sun);
		
		DefaultPointLight pointLight = new DefaultPointLight();
		pointLight.setDiffuse(new Vector4f(.1f, 0.6f, 0.1f, 1.0f));
		pointLight.setPosition(100, 50, 100);
		world.addEntity(pointLight);

			StaticEntity testRock = new StaticEntity();
			testRock.setPosition(-100, 50, -100);
			testRock.createBody(rockModel);
			world.addEntity(testRock);
		
		DefaultPointLight pointLight2 = new DefaultPointLight();
		pointLight2.setDiffuse(new Vector4f(.1f, 0.1f, 0.6f, 1.0f));
		pointLight2.setPosition(-100, 50, -100);
		world.addEntity(pointLight2);
		
		DefaultSpotLight spotLight = new DefaultSpotLight();
		spotLight.setPosition(50, 50, 50);
		world.addEntity(spotLight);
	}

	@Override
	public void init() {
		Game.print("PlayState init");
		World world = this.getUpToDateState().getWorld();

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
