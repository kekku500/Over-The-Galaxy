package main;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import resources.Resources;
import resources.model.Model;
import resources.model.custom.Sphere;
import state.Game;
import state.State;
import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Vector3f;
import utils.math.Vector4f;
import world.World;
import world.entity.dumb.DynamicEntity;
import world.entity.dumb.StaticEntity;
import world.entity.gui.HudExample;
import world.entity.gui.hud.HeadsUpDisplay;
import world.entity.lighting.DefaultPointLight;
import world.entity.lighting.DefaultSpotLight;
import world.entity.lighting.SunLight;
import world.entity.smart.Player;
import world.graphics.Graphics2D;
import world.graphics.ShadowMapper;
import world.sync.Request;

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

import controller.Controller;
import controller.Controller.CamType;

public class PlayState extends State{
	
	private int stateId;
	

	
	public PlayState(int stateId){
		this.stateId = stateId;
	}
	
	Model model;
	
	@Override
	public void init() {
		Game.println("PlayState init");
		World world = this.getUpToDateState().getWorld();

		
		Player player = new Player(15,30,35);
		world.addEntity(player);
		
		HeadsUpDisplay HUD = new HeadsUpDisplay(player, world);
		
		Controller cam = new Controller(10,10,10);
		
		//cam.setFollowing(player);
		//cam.setType(CamType.LOCK);
		
		world.addEntity(cam);
		
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
		sun.setModel(new Sphere(100, 16, 16));
		sun.setPosition(1000, 1000, 1000);
		sun.setLightScattering(true);
		world.addEntity(sun);
		
		DefaultPointLight pointLight = new DefaultPointLight();
		pointLight.setDiffuse(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		pointLight.setPosition(50, 50, 50);
		world.addEntity(pointLight);

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

	@Override
	public void update(float dt){

		World world = getUpdatingState().getWorld();
	    world.update(dt);
	    //container.update();
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
		//world.dispose();
		//container.dispose();
		//model.dispose();
	}
	
	@Override
	public int getId() {
		return stateId;
	}



}
