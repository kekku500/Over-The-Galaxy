package entitymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

import resources.Resources;
import resources.model.Model;
import resources.model.custom.Sphere;
import main.Config;
import main.state.RenderState;
import main.state.StateVariable;
import math.Matrix4f;
import utils.ArrayList;
import utils.HashSet;
import entity.blueprint.AbstractVisualPhysicsEntity;
import entity.creation.Camera;
import entity.creation.Controller;
import entity.creation.DynamicEntity;
import entity.creation.ModeledEntity;
import entity.creation.Player;
import entity.creation.SunLight;
import entity.creation.Controller.CamType;
import entity.sheet.Entity;
import entity.sheet.Lighting;
import entity.sheet.PointLighting;
import entity.sheet.SpotLighting;
import entity.sheet.VisualEntity;
import graphics.culling.BoundingAxis;
import graphics.culling.BoundingSphere;
import graphics.culling.ViewFrustum;
import graphics.gui.hud.HeadsUpDisplay;

public class Level implements Serializable {
	
	private String name = "default_level.lev";
	
	private static final long serialVersionUID = 1L;
	
	public StateVariable<ArrayList<Entity>> entities;
	public StateVariable<ArrayList<VisualEntity>> visualEntites;
	public StateVariable<ArrayList<Lighting>> lightingEntities;
	public StateVariable<HashSet<DynamicEntity>> dynamicEntities;
	
	private EntityManager manager;
	
	public Level(EntityManager manager){
		this.manager = manager;
		entities = new StateVariable<ArrayList<Entity>>(new ArrayList<Entity>());
		visualEntites = new StateVariable<ArrayList<VisualEntity>>(new ArrayList<VisualEntity>());
		lightingEntities = new StateVariable<ArrayList<Lighting>>(new ArrayList<Lighting>());
		dynamicEntities = new StateVariable<HashSet<DynamicEntity>>(new HashSet<DynamicEntity>());
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void loadNewEntities(ArrayList<Entity> newEntities){
		//Remove current entities
		for(Entity e: entities.updating()){
			manager.removeEntity(e);
		}
		//System.out.println("LOADNG!!!");
		for(Entity e: newEntities){
			//System.out.println("loading " + e);
			e.setEntityManager(manager);
			e.load();
			if(e instanceof Controller){
				Controller c = (Controller)e;
				
				c.cameraFrustum = new StateVariable<ViewFrustum>(new ViewFrustum());
				c.setProjection(c.fov, c.aspect, c.zNear, c.zFar);
				
				manager.getState().setCamera(c);
				manager.addEntity(e);
			}
			if(e instanceof Player){
				Player p = (Player)e;
				manager.getState().setPlayer(p);
			}
			if(e instanceof ModeledEntity){
				ModeledEntity me = (ModeledEntity)e;
				
				me.boundingAxis = new StateVariable<BoundingAxis>(new BoundingAxis());
				me.boundingSphere = new StateVariable<BoundingSphere>(new BoundingSphere());
				
				if(me.modelPath != null){
					Model m = null;
					try {
						m = Resources.getModel(me.modelPath);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if(e instanceof AbstractVisualPhysicsEntity){
						AbstractVisualPhysicsEntity ave = (AbstractVisualPhysicsEntity)e;
						ave.createBody(m);
					}else{
						me.setModel(m);
					}
				}else{
					if(e instanceof SunLight){
						SunLight se = (SunLight)e;
						se.setModel(new Sphere(100, 16, 16));
						se.setPosition(2000,100,000);
					}
				}
	
			}
			//if(!(e instanceof Camera) && !(e instanceof Player))
			manager.addEntity(e);
		}
		
		for(Entity parent: newEntities){
			if(!parent.getChildren().isEmpty()){
				Set<Entity> copiedChildren = new HashSet<Entity>();
				copiedChildren.addAll(parent.getChildren());
				parent.getChildren().clear();
				for(Entity child: copiedChildren){
					parent.addChild(child);
				}
			}
		}
		
		
		/*Player player = new Player(manager, 0,
				0,
				0);
		manager.getState().setPlayer(player);
	    
		Controller camera = new Controller(manager, 10,10,10);
		camera.setViewport(Config.VIEWPORT_WIDTH, Config.VIEWPORT_HEIGHT);
		camera.setProjection(Config.FOV, (float) Config.VIEWPORT_WIDTH / (float) Config.VIEWPORT_HEIGHT, Config.Z_NEAR, Config.Z_FAR);	
		camera.setUpdatePriority((short)-5);
		camera.setFollowing(player);
		camera.setType(CamType.LOCK);
		manager.getState().setCamera(camera);*/
	}
	
	@SuppressWarnings({ "unchecked" })
	public static ArrayList<Entity> loadLevelEntities(String levelName){
		try {
			File f = new File(Resources.RESOURCESPATH + "levels\\" + levelName);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			
			ArrayList<Entity> levelEntities = null;
			try {
				levelEntities = (ArrayList<Entity>)ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			
			ois.close();
			
			return levelEntities;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveLevelEntities(Level level){
		try {
			File f = new File(Resources.RESOURCESPATH + "levels\\" + level.getName());
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			
			ArrayList<Entity> savingEntities = level.entities.updating();
			
			for(Entity e: savingEntities)
				e.save();
			
			oos.writeObject(savingEntities);
			
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean removeLevel(String levelName){
		File f = new File(Resources.RESOURCESPATH + "levels\\" + levelName);
		if(f.exists())
			return f.delete();
		return false;
	}

}
