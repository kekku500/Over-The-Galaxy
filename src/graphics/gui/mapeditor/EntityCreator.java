package graphics.gui.mapeditor;

import resources.Resources;
import resources.model.Model;
import utils.Utils;

import com.bulletphysics.linearmath.QuaternionUtil;

import main.PlayState;
import main.state.RenderState;
import math.Quat4f;
import math.Vector3f;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.ValueAdjusterFloat;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.model.SimpleFloatModel;
import entity.blueprint.AbstractVisualPhysicsEntity;
import entity.creation.DynamicEntity;
import entity.creation.ModeledEntity;
import entity.creation.StaticEntity;
import entity.sheet.Entity;
import entity.sheet.Lighting;
import entitymanager.EntitySelectionHandler;

public class EntityCreator extends CloseableFrame{
	
	SimpleChangableListModel<String> model;
	ListBox<String> list;
	
	ValueAdjusterFloat xAdjuster;
	ValueAdjusterFloat yAdjuster;
	ValueAdjusterFloat zAdjuster;
	ValueAdjusterFloat xsAdjuster;
	ValueAdjusterFloat ysAdjuster;
	ValueAdjusterFloat zsAdjuster;
	ValueAdjusterFloat xrAdjuster;
	ValueAdjusterFloat yrAdjuster;
	ValueAdjusterFloat zrAdjuster;
	
    final ComboBox<String> cb;
    
    private PlayState state;
    public MapEditor editor;
    private LightingPropFrame lightingProperties;
    ToggleButton enableLighting;
    public ModeledEntity creatingEntity;
	
	public EntityCreator(PlayState state, MapEditor editor){
		this.state = state;
		this.editor = editor;
		setTheme("resizableframe-title");
		
		setTitle("Entity Creator");
		setSize(200, 300);
		
		DialogLayout layout = new DialogLayout();
		add(layout);
		
		list = new ListBox<String>();
		list.setTheme("listbox");
		model = new SimpleChangableListModel<String>();
		list.setModel(model);
		
		SimpleFloatModel xChanger = new SimpleFloatModel(-100000, 100000, 0);
		xAdjuster = new ValueAdjusterFloat(xChanger);
        Label xLabel = new Label("X Coord: ");
        xLabel.setLabelFor(xAdjuster);
        xChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldXAdjusterVal = true;
			}});
		SimpleFloatModel yChanger = new SimpleFloatModel(-100000, 100000, 0);
		yAdjuster = new ValueAdjusterFloat(yChanger);
        Label yLabel = new Label("Y Coord: ");
        yLabel.setLabelFor(yAdjuster);
        yChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldYAdjusterVal = true;
			}});
		SimpleFloatModel zChanger = new SimpleFloatModel(-100000, 100000, 0);
		zAdjuster = new ValueAdjusterFloat(zChanger);
        Label zLabel = new Label("Z Coord: ");
        zLabel.setLabelFor(zAdjuster);
        zChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldZAdjusterVal = true;
			}});
		SimpleFloatModel xsChanger = new SimpleFloatModel(0.0001f, 100000, 1);
		xsAdjuster = new ValueAdjusterFloat(xsChanger);
        Label xsLabel = new Label("X Scale: ");
        xsLabel.setLabelFor(xsAdjuster);
        xsChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldXSAdjusterVal = true;
			}});
		SimpleFloatModel ysChanger = new SimpleFloatModel(0.0001f, 100000, 1);
		ysAdjuster = new ValueAdjusterFloat(ysChanger);
        Label ysLabel = new Label("Y Scale: ");
        ysLabel.setLabelFor(ysAdjuster);
        ysChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldYSAdjusterVal = true;
			}});
		SimpleFloatModel zsChanger = new SimpleFloatModel(0.0001f, 100000, 1);
		zsAdjuster = new ValueAdjusterFloat(zsChanger);
        Label zsLabel = new Label("Z Scale: ");
        zsLabel.setLabelFor(zsAdjuster);
        zsChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldZSAdjusterVal = true;
			}});
		SimpleFloatModel xrChanger = new SimpleFloatModel(-360, 360, 0);
		xrAdjuster = new ValueAdjusterFloat(xrChanger);
        Label xrLabel = new Label("X Rot: ");
        xrLabel.setLabelFor(xrAdjuster);
        xrChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldXRAdjusterVal = true;
			}});
		SimpleFloatModel yrChanger = new SimpleFloatModel(-360, 360, 0);
		yrAdjuster = new ValueAdjusterFloat(yrChanger);
        Label yrLabel = new Label("Y Rot: ");
        yrLabel.setLabelFor(yrAdjuster);
        yrChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldYRAdjusterVal = true;
			}});
		SimpleFloatModel zrChanger = new SimpleFloatModel(-360, 360, 0);
		zrAdjuster = new ValueAdjusterFloat(zrChanger);
        Label zrLabel = new Label("Z Rot: ");
        zrLabel.setLabelFor(zrAdjuster);
        zrChanger.addCallback(new Runnable(){
			@Override
			public void run() {
				oldZRAdjusterVal = true;
			}});
        lightingProperties = new LightingPropFrame(this);
        lightingProperties.hide();
        editor.desktop.add(lightingProperties);
        
        enableLighting = new ToggleButton("As lightsource");
        enableLighting.setTheme("togglebutton");
        enableLighting.addCallback(new ToggleFrame(lightingProperties));
        
        Button createButton = new Button("Create Object");
        createButton.setTheme("button");
        createButton.addCallback(new Runnable(){

			@Override
			public void run() {
				requestEntityCreate();
			}});
        
        Label cbLabel = new Label("Type: ");
        cbLabel.setTheme("label");
        final SimpleChangableListModel<String> lmStyle = 
        		new SimpleChangableListModel<String>("Static", "Dynamic");
        
       cb = new ComboBox<String>(lmStyle);
        cb.setSelected(1);
        cb.setComputeWidthFromModel(true);
        
        layout.setVerticalGroup(
            	layout.createSequentialGroup(list).
            	addGroup(layout.createParallelGroup().
            		addGroup(layout.createSequentialGroup().
            			addGroup(layout.createParallelGroup(xLabel, xAdjuster)).
            			addGroup(layout.createParallelGroup(yLabel, yAdjuster)).
            			addGroup(layout.createParallelGroup(zLabel, zAdjuster)).
            			addGroup(layout.createParallelGroup(xrLabel, xrAdjuster)).
            			addGroup(layout.createParallelGroup(yrLabel, yrAdjuster)).
            			addGroup(layout.createParallelGroup(zrLabel, zrAdjuster))
            		).
            		addGroup(layout.createSequentialGroup().
            			addGroup(layout.createParallelGroup(xsLabel, xsAdjuster)).
            			addGroup(layout.createParallelGroup(ysLabel, ysAdjuster)).
            			addGroup(layout.createParallelGroup(zsLabel, zsAdjuster)).
            			addGroup(layout.createParallelGroup(cbLabel, cb)).
            			addGroup(layout.createParallelGroup(enableLighting))
            		)
            	).
            	addGroup(layout.createSequentialGroup(createButton))
            );
            
            layout.setHorizontalGroup(
            	layout.createParallelGroup(list).
            	addGroup(layout.createSequentialGroup().
            		addGroup(layout.createParallelGroup().
            			addGroup(layout.createSequentialGroup(xLabel, xAdjuster)).
            			addGroup(layout.createSequentialGroup(yLabel, yAdjuster)).
            			addGroup(layout.createSequentialGroup(zLabel, zAdjuster)).
            			addGroup(layout.createSequentialGroup(xrLabel, xrAdjuster)).
            			addGroup(layout.createSequentialGroup(yrLabel, yrAdjuster)).
            			addGroup(layout.createSequentialGroup(zrLabel, zrAdjuster))
            		).
            		addGroup(layout.createParallelGroup().
            			addGroup(layout.createSequentialGroup(xsLabel, xsAdjuster)).
            			addGroup(layout.createSequentialGroup(ysLabel, ysAdjuster)).
            			addGroup(layout.createSequentialGroup(zsLabel, zsAdjuster)).
            			addGroup(layout.createSequentialGroup(cbLabel, cb)).
            			addGroup(layout.createSequentialGroup(enableLighting))
            		)
            	).
            	addGroup(layout.createParallelGroup(createButton))
            );
            
	}
	
	@Override
	public void hide(){
		super.hide();
		lightingProperties.hide();
		enableLighting.setActive(false);
	}
	
	private int lastSelected = -1;
	public void update(){
		createEntity();
		
		int selected = list.getSelected();
		if(lastSelected != selected){
			lastSelected = selected;
			changeCreatingEntity(model.getEntry(selected));
			lastSelected = -1;
			list.setSelected(-1);
		}
		
		checkAdjustChanges();
	}
	
	boolean entityToBeCreated;
	private void requestEntityCreate(){
		if(creatingEntity != null){
			System.out.println("request received");
			entityToBeCreated = true;
		}
	}
	
	private void createEntity(){
		if(entityToBeCreated){
			AbstractVisualPhysicsEntity entity = null;
			if(cb.getSelected() == 1){
				entity = new DynamicEntity(state.getEntityManager());
			}else{
				entity = new StaticEntity(state.getEntityManager());
			}
			
			entity.setPosition(creatingEntity.getPosition(RenderState.updating()));
			entity.getScaleRotationMatrix().set(creatingEntity.getScaleRotationMatrix());
			entity.getScaleRotationMatrix().mul(creatingEntity.rotationMatrix);
			entity.castShadow(creatingEntity.isShadowEnabled());
			entity.createBody(creatingEntity.getModel());
			
			//copy children
			for(Entity child: creatingEntity.getChildren()){
				System.out.println("added child " + child);
				entity.addChild(child);
			}
			
			if(lightingProperties.lighting != null){
				lightingProperties.lighting = null;
			}
			
			state.getEntityManager().removeEntity(creatingEntity);
			entityToBeCreated = false;
			list.setSelected(-1);
			lastSelected = -1;
			creatingEntity = null;
			lightingProperties.hide();
		}
	}
	
	private boolean oldXAdjusterVal;
	private boolean oldYAdjusterVal;
	private boolean oldZAdjusterVal;
	private boolean oldXSAdjusterVal;
	private boolean oldYSAdjusterVal;
	private boolean oldZSAdjusterVal;
	private boolean oldXRAdjusterVal;
	private boolean oldYRAdjusterVal;
	private boolean oldZRAdjusterVal;
	private void checkAdjustChanges(){
		if(creatingEntity == null)
			return;
		//Position
		float ex = creatingEntity.getTransform().updating().origin.x;
		float ey = creatingEntity.getTransform().updating().origin.y;
		float ez = creatingEntity.getTransform().updating().origin.z;
		if(oldXAdjusterVal){
			oldXAdjusterVal = false;
			creatingEntity.setPosition(xAdjuster.getValue(), ey, ez);
		}
		if(oldYAdjusterVal){
			oldYAdjusterVal = false;
			creatingEntity.setPosition(ex, yAdjuster.getValue(), ez);
		}
		if(oldZAdjusterVal){
			oldZAdjusterVal = false;
			creatingEntity.setPosition(ex, ey, zAdjuster.getValue());
		}

		//Scale
		float exs = creatingEntity.getScaleRotationMatrix().m00;
		float eys = creatingEntity.getScaleRotationMatrix().m11;
		float ezs = creatingEntity.getScaleRotationMatrix().m22;
		
		Vector3f finalScale = new Vector3f(exs, eys, ezs);
		boolean scaleChanged = false;
		if(oldXSAdjusterVal){
			oldXSAdjusterVal = false;
			finalScale.x = xsAdjuster.getValue();
			scaleChanged = true;
		}
		if(oldYSAdjusterVal){
			oldYSAdjusterVal = false;
			finalScale.y = ysAdjuster.getValue();
			scaleChanged = true;
		}
		if(oldZSAdjusterVal){
			oldZSAdjusterVal = false;
			finalScale.z = zsAdjuster.getValue();
			scaleChanged = true;
		}
		if(scaleChanged){
			creatingEntity.scale(finalScale.x/exs, finalScale.y/eys, finalScale.z/ezs);
		}

		//Rotation
		float exr = creatingEntity.rotX;
		float eyr = creatingEntity.rotY;
		float ezr = creatingEntity.rotZ;
		boolean rot = false;
		if(oldXRAdjusterVal){
			oldXRAdjusterVal = false;
			exr = xrAdjuster.getValue();
			creatingEntity.rotX = xrAdjuster.getValue();
			rot = true;
		}

		if(oldYRAdjusterVal){
			oldYRAdjusterVal = false;
			eyr = yrAdjuster.getValue();
			creatingEntity.rotY = yrAdjuster.getValue();
			rot = true;
		}
		if(oldZRAdjusterVal){
			oldZRAdjusterVal = false;
			ezr = zrAdjuster.getValue();
			creatingEntity.rotZ = zrAdjuster.getValue();
			rot = true;
		}
		
		
		if(rot){
			Quat4f xRot = new Quat4f();
			QuaternionUtil.setRotation(xRot, Vector3f.X, Utils.rads(exr));
			Quat4f yRot = new Quat4f();
			QuaternionUtil.setRotation(yRot, Vector3f.Y, Utils.rads(eyr));
			xRot.mul(yRot);
			
			Quat4f zRot = new Quat4f();
			QuaternionUtil.setRotation(zRot, Vector3f.Z, Utils.rads(ezr));
			xRot.mul(zRot);
			
			creatingEntity.rotationMatrix.set(xRot);
		}
		
	}
	
	private void changeCreatingEntity(String modelPath){
		Model entityModel = null;
		try {
			entityModel = Resources.getModelAbs(modelPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		float front = 50;
		Vector3f cam = state.getCamera().getPosition(RenderState.updating()).copy();
		cam.add(state.getCamera().getViewRay(RenderState.updating()).copy().scl(front));
		xAdjuster.setValue(cam.x);
		yAdjuster.setValue(cam.y);
		zAdjuster.setValue(cam.z);
		
		ModeledEntity creatingEntity = new ModeledEntity(state.getEntityManager());
		creatingEntity.setModel(entityModel);
		creatingEntity.setPosition(xAdjuster.getValue(), yAdjuster.getValue(), zAdjuster.getValue());
		
		lightingProperties.lighting = null;
		
		if(enableLighting.isActive() && lightingProperties.lighting == null)
			lightingProperties.createLighting(creatingEntity);
		
		
		editor.setSelectedEntity(creatingEntity);
		setCreatingEntity(creatingEntity);
		
		//list.setSelected(-1);
	}
	
	public void addObject(String obj){
		model.addElement(obj);
	}
	
	public void setCreatingEntity(ModeledEntity e){
		creatingEntity = e;
		if(e != null){
			for(Entity child: creatingEntity.getChildren()){
				if(child instanceof Lighting){
					lightingProperties.setLighting((Lighting)child);
				}
					
			}
		}else{
			lightingProperties.setLighting(null);
		}

	}
}
