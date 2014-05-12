package world.gui.mapeditor;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.QuaternionUtil;

import resources.Resources;
import resources.model.Model;
import state.Game;
import state.RenderState;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Quat4f;
import utils.math.Ray;
import utils.math.Transform;
import utils.math.Vector3f;
import world.entity.Entity;
import world.entity.ModeledBodyEntity;
import world.entity.VisualEntity;
import world.entity.create.DynamicEntity;
import world.entity.create.ModeledEntity;
import world.entity.create.StaticEntity;
import main.PlayState;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.ValueAdjusterFloat;
import de.matthiasmann.twl.ValueAdjusterInt;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.ListModel;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.model.SimpleFloatModel;
import de.matthiasmann.twl.model.SimpleIntegerModel;
import de.matthiasmann.twl.model.SimpleListModel;

public class MapEditor extends Widget implements EditorRequest{
	
	ResizableFrame frame;
	
	private boolean positionAdjusted;
	
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
	
	
	PlayState state;
	
	public void addObject(String obj){
		model.addElement(obj);
	}
	
	public MapEditor(PlayState state){
		this.state = state;
		frame = new ResizableFrame();
		frame.setTheme("resizableframe-title");
		frame.setTitle("Object Creator");
		frame.setSize(200, 300);
		
		DialogLayout layout = new DialogLayout();
		frame.add(layout);
		
		list = new ListBox<String>();
		list.setTheme("listbox");
		model = new SimpleChangableListModel<String>();
		list.setModel(model);
		
		SimpleFloatModel xChanger = new SimpleFloatModel(-1000, 1000, 100);
		xAdjuster = new ValueAdjusterFloat(xChanger);
        Label xLabel = new Label("X Coord: ");
        xLabel.setLabelFor(xAdjuster);
        
		SimpleFloatModel yChanger = new SimpleFloatModel(-1000, 1000, 100);
		yAdjuster = new ValueAdjusterFloat(yChanger);
        Label yLabel = new Label("Y Coord: ");
        yLabel.setLabelFor(yAdjuster);
        
		SimpleFloatModel zChanger = new SimpleFloatModel(-1000, 1000, 100);
		zAdjuster = new ValueAdjusterFloat(zChanger);
        Label zLabel = new Label("Z Coord: ");
        zLabel.setLabelFor(zAdjuster);
        
		SimpleFloatModel xsChanger = new SimpleFloatModel(0.0001f, 100, 1);
		xsAdjuster = new ValueAdjusterFloat(xsChanger);
        Label xsLabel = new Label("X Scale: ");
        xsLabel.setLabelFor(xsAdjuster);
        
		SimpleFloatModel ysChanger = new SimpleFloatModel(0.0001f, 100, 1);
		ysAdjuster = new ValueAdjusterFloat(ysChanger);
        Label ysLabel = new Label("Y Scale: ");
        ysLabel.setLabelFor(ysAdjuster);
        
		SimpleFloatModel zsChanger = new SimpleFloatModel(0.0001f, 100, 1);
		zsAdjuster = new ValueAdjusterFloat(zsChanger);
        Label zsLabel = new Label("Z Scale: ");
        zsLabel.setLabelFor(zsAdjuster);
        
		SimpleFloatModel xrChanger = new SimpleFloatModel(-360, 360, 0);
		xrAdjuster = new ValueAdjusterFloat(xrChanger);
        Label xrLabel = new Label("X Rot: ");
        xrLabel.setLabelFor(xrAdjuster);
        
		SimpleFloatModel yrChanger = new SimpleFloatModel(-360, 360, 0);
		yrAdjuster = new ValueAdjusterFloat(yrChanger);
        Label yrLabel = new Label("Y Rot: ");
        yrLabel.setLabelFor(yrAdjuster);
        
		SimpleFloatModel zrChanger = new SimpleFloatModel(-360, 360, 0);
		zrAdjuster = new ValueAdjusterFloat(zrChanger);
        Label zrLabel = new Label("Z Rot: ");
        zrLabel.setLabelFor(zrAdjuster);
        
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
        			addGroup(layout.createParallelGroup(cbLabel, cb))
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
        			addGroup(layout.createSequentialGroup(cbLabel, cb))
        		)
        	).
        	addGroup(layout.createParallelGroup(createButton))
        );
		

		add(frame);
	}
	
	private int lastSelected = -1;
	public void update(){
		createEntity();
		
		int selected = list.getSelected();
		if(lastSelected != selected){
			lastSelected = selected;
			changeCreatingEntity(model.getEntry(selected));
		}
		
		if(creatingEntity != null){
			checkAdjustChanges();
		}
		
		handleSelectedEntity();
	}
	
	private void createEntity(){
		if(entityToBeCreated){
			System.out.println("creating");
			ModeledBodyEntity entity = null;
			if(cb.getSelected() == 1){
				entity = new DynamicEntity(state.getEntityManager());
			}else{
				entity = new StaticEntity(state.getEntityManager());
			}
			
			
			entity.setPosition(creatingEntity.getPosition(RenderState.updating()));
			entity.getScaleRotationMatrix().set(creatingEntity.getScaleRotationMatrix());
			entity.getScaleRotationMatrix().mul(creatingEntity.rotationMatrix);
			
			entity.createBody(creatingEntity.getModel());
			
			state.getEntityManager().removeEntity(creatingEntity);
			creatingEntity = null;
			entityToBeCreated = false;
			list.setSelected(-1);
			lastSelected = -1;
		}
	}
	
	boolean entityToBeCreated;
	private void requestEntityCreate(){
		if(creatingEntity != null){
			System.out.println("request received");
			entityToBeCreated = true;
		}
	}
	
	private void checkAdjustChanges(){
		//Position
		float ex = creatingEntity.getTransform(RenderState.updating()).origin.x;
		float ey = creatingEntity.getTransform(RenderState.updating()).origin.y;
		float ez = creatingEntity.getTransform(RenderState.updating()).origin.z;
		
		/*Vector3f finalPos = new Vector3f(ex, ey, ez);
		boolean posChanged = false;
		if(ex != xAdjuster.getValue()){
			finalPos.x = xAdjuster.getValue();
			posChanged = true;
		}
		if(ey != yAdjuster.getValue()){
			finalPos.y = yAdjuster.getValue();
			posChanged = true;
		}
		if(ez != zAdjuster.getValue()){
			finalPos.z = zAdjuster.getValue();
			posChanged = true;
		}
		if(posChanged){
			creatingEntity.setPosition(finalPos);
		}*/
		
		//Scale
		float exs = creatingEntity.getScaleRotationMatrix().m00;
		float eys = creatingEntity.getScaleRotationMatrix().m11;
		float ezs = creatingEntity.getScaleRotationMatrix().m22;
		
		Vector3f finalScale = new Vector3f(exs, eys, ezs);
		boolean scaleChanged = false;
		if(exs != xsAdjuster.getValue()){
			finalScale.x = xsAdjuster.getValue();
			scaleChanged = true;
		}
		if(eys != ysAdjuster.getValue()){
			finalScale.y = ysAdjuster.getValue();
			scaleChanged = true;
		}
		if(ezs != zsAdjuster.getValue()){
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
		if(exr != xrAdjuster.getValue()){
			exr = xrAdjuster.getValue();
			creatingEntity.rotX = xrAdjuster.getValue();
			rot = true;
		}

		if(eyr != yrAdjuster.getValue()){
			eyr = yrAdjuster.getValue();
			creatingEntity.rotY = yrAdjuster.getValue();
			rot = true;
		}
		if(ezr != zrAdjuster.getValue()){
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
	
	private ModeledEntity creatingEntity;
	private void changeCreatingEntity(String modelPath){
		if(creatingEntity != null){
			state.getEntityManager().removeEntity(creatingEntity);
			creatingEntity = null;
		}
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
		
		creatingEntity = new ModeledEntity(state.getEntityManager());
		creatingEntity.setModel(entityModel);
		creatingEntity.setPosition(xAdjuster.getValue(), yAdjuster.getValue(), zAdjuster.getValue());
	}
	
    @Override
    protected void layout() {
    	super.layout();
    	
    	if(frame.getRight() > this.getRight()){
    		frame.setPosition(this.getRight()-frame.getWidth(), frame.getY());
    	}
    	if(frame.getBottom() > this.getBottom()){
    		frame.setPosition(frame.getX(), this.getBottom()-frame.getHeight());
    	}
    	
    	if(!positionAdjusted){
    		positionAdjusted = true;
    				
    		float relX = 1f;
        	float relY = 0f;

        	Widget p = frame.getParent();
        	frame.setPosition(
        			p.getInnerX() + (int)((p.getInnerWidth() - frame.getWidth()) * relX),
        			p.getInnerY() + (int)((p.getInnerHeight() - frame.getHeight()) * relY));
    	}


    }
    
    private void requestObjectSelection(float screenX, float screenY){
    	if(!Mouse.isGrabbed()){
    		ObjectSelectionRequest req = new ObjectSelectionRequest(screenX, screenY);
    		req.addCallback(this);

    		state.addObjectSelectionRequest(req);
    	}
    }
    
    Vector3f pickedWorldPosition = null;
    Vector3f pickedModelPosition = null;
    public void handleSelectedEntity(){
    	if(selectionReq != null){
    		if(Mouse.isButtonDown(0)){
    			if(pickedModelPosition == null){
            		pickedWorldPosition = new Vector3f(selectionReq.getScreenX(), 
            				selectionReq.getScreenY() , 
            				selectionReq.getDepth());
            		
            		state.getCamera().unproject(pickedWorldPosition);
            		VisualEntity selectedEntity = selectionReq.getEntity();
            		
            		if(selectedEntity instanceof DynamicEntity){
            			RigidBody body = ((DynamicEntity) selectedEntity).getBody();
            			
                		Transform t = new Transform();
                		body.getWorldTransform(t);
                		Matrix4f model = new Matrix4f();
                		t.getMatrix(model);
                		model.invert();
                		
                		pickedModelPosition = pickedWorldPosition.copy().prj(model);
                		//System.out.println("model pos " + pickedModelPosition);
                		((DynamicEntity) selectedEntity).drawLine1 = pickedWorldPosition;
                		//((DynamicEntity) selectedEntity).drawLine2 = new Vector3f(0,0,0);
            		}else if(selectedEntity instanceof ModeledEntity){
            			ModeledEntity entity = ((ModeledEntity) selectedEntity);
            			Matrix4f m2 = entity.getTransformMatrix(RenderState.updating()).copy();
            			m2.invert();
            			
            			pickedModelPosition = pickedWorldPosition.copy().prj(m2);
            		}
    			}else{
    	       		Vector3f pickedMouseWorld = new Vector3f(Mouse.getX(), Mouse.getY(), selectionReq.getDepth());
            		state.getCamera().unproject(pickedMouseWorld);
    				VisualEntity selectedEntity = selectionReq.getEntity();
    				
            		if(selectedEntity instanceof DynamicEntity){
            			RigidBody body = ((DynamicEntity) selectedEntity).getBody();
            			
                		Vector3f modelChange = pickedModelPosition.copy();
                  		Vector3f modelChangeRotation = pickedModelPosition.copy();
                		
                   		Transform t = new Transform();
                		body.getWorldTransform(t);
                		Matrix4f model = new Matrix4f();
                		t.getMatrix(model);
                		
                		modelChange.prj(model); //pick position to world position
                		model.invert();
                		modelChangeRotation.mulTra(model); //rotate pick position for new pick
 
                   		((DynamicEntity) selectedEntity).drawLine1 = modelChange;
                		((DynamicEntity) selectedEntity).drawLine2 = pickedMouseWorld;
            			
            			
            			Vector3f force = pickedMouseWorld.copy().add(modelChange.copy().negater());
            			float x = force.length();
            			if(x > 20)
            				x = 20;
            			
            			float k = x/5+x*x/100;
            			
            			force.scl(k);

 
            			//body.appl
            			//body.applyCentralForce(force);
            			body.applyForce(force, modelChangeRotation);
            			
            			body.activate();
            			//body.setGravity(new Vector3f(0,0,0));
            		}else{
            			ModeledEntity entity = ((ModeledEntity) selectedEntity);
            			Matrix4f m2 = entity.getTransformMatrix(RenderState.updating()).copy();
  
            			
            			Vector3f pickedWorldPosition = pickedModelPosition.copy().prj(m2);
            			
            			Vector3f delta = pickedMouseWorld.sub(pickedWorldPosition);
            			System.out.println(delta);
            			
            			entity.setPosition(entity.getPosition(RenderState.updating()).add(delta));
            			//pickedModelPosition = entity.getPosition(RenderState.updating());
            		}

    			}
    		}else{
				/*VisualEntity selectedEntity = selectionReq.getEntity();
				if(selectedEntity instanceof DynamicEntity){
	    			RigidBody body = ((DynamicEntity) selectedEntity).getBody();
	    			body.setGravity(new Vector3f(0,-10,0));
				}*/
    			
    			selectionReq = null;
    			pickedModelPosition = null;

    		}
    	}
    }

    ObjectSelectionRequest selectionReq;
    //private VisualEntity selectedEntity;
    @Override
    protected boolean handleEvent(Event evt) {
        if(super.handleEvent(evt)) {
            return true;
        }
        switch (evt.getType()) {
            case KEY_PRESSED:
                switch (evt.getKeyCode()) {
                    case Event.KEY_ESCAPE:
                        return true;
                }
                break;
            case MOUSE_BTNDOWN:
                if(evt.getMouseButton() == Event.MOUSE_LBUTTON) {

                	requestObjectSelection(Mouse.getX(), Mouse.getY());
                }
                break;
            default: break;
        }
        return evt.isMouseEventNoWheel();
    }

	@Override
	public void selectionRequests(ObjectSelectionRequest req) {
		selectionReq = req;
		//selectedEntity = req.getEntity();
	}

}
