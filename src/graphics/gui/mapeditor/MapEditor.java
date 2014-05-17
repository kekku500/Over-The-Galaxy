package graphics.gui.mapeditor;

import main.PlayState;
import main.state.RenderState;
import math.Matrix4f;
import math.Quat4f;
import math.Transform;
import math.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import resources.Resources;
import resources.model.Model;
import utils.Utils;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.QuaternionUtil;

import de.matthiasmann.twl.Alignment;
import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.DesktopArea;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ValueAdjusterFloat;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.model.SimpleFloatModel;
import entity.blueprint.AbstractVisualPhysicsEntity;
import entity.creation.DynamicEntity;
import entity.creation.ModeledEntity;
import entity.creation.StaticEntity;
import entity.sheet.Entity;
import entity.sheet.VisualEntity;
import entitymanager.EntitySelectionHandler;
import entitymanager.EntitySelectionRequest;

public class MapEditor extends Widget implements EntitySelectionHandler{
	
	public DesktopArea desktop;
	
	BoxLayout buttonsTab;
	
	private boolean positionAdjusted;
	
	public EntityCreator entityCreator;
	public LevelLoader levelLoader;
	public SettingsFrame settingsFrame;
	public EntityInfoWindow entityInfoWindow;
	
	PlayState state;
	
	//Entity selecting
    EntitySelectionRequest selectionReq;
    private Entity selectedEntity;
	boolean movingEntity;
    Vector3f pickedWorldPosition = null;
    Vector3f pickedModelPosition = null;
    Vector3f lastRotationVector = null;
    Quat4f curQuat = new Quat4f();
	
	public MapEditor(PlayState state){
		setTheme("-defaults");
		this.state = state;
		
		desktop = new DesktopArea();
		desktop.setTheme("-defaults");
		add(desktop);
		
		entityCreator = new EntityCreator(state, this);
		this.
		entityCreator.hide();
		
		desktop.add(entityCreator);
		
		levelLoader = new LevelLoader(state);
		levelLoader.hide();
		desktop.add(levelLoader);
		
		settingsFrame = new SettingsFrame(state);
		settingsFrame.hide();
		desktop.add(settingsFrame);
		
		entityInfoWindow = new EntityInfoWindow();
		desktop.add(entityInfoWindow);
		
		buttonsTab = new BoxLayout(BoxLayout.Direction.HORIZONTAL);

		buttonsTab.setTheme("buttonBox");
		add(buttonsTab);
		
		addButton("Entity Creator",  new ToggleFrame(entityCreator));
		
		addButton("Level Loader", new ToggleFrame(levelLoader));
		
		addButton("Settings", new ToggleFrame(settingsFrame));
		
		addButton("Entity Info", new ToggleFrame(entityInfoWindow));
	}
	
    public Button addButton(String text, Runnable cb) {
        Button btn = new Button(text);
        btn.addCallback(cb);
        buttonsTab.add(btn);
        invalidateLayout();
        return btn;
    }
	
	public void update(){
		entityCreator.update();
		
		handleSelectedEntity();
		
		if(levelLoader.requestingSave){
			levelLoader.saveLevel();
		}
		if(levelLoader.requestingLoad){
			levelLoader.loadLevel();
		}
	}
	
    @Override
    protected void layout() {
    	super.layout();
    	
    	desktop.setSize(getParent().getWidth(), getParent().getHeight());
    	
    	buttonsTab.adjustSize();
    	buttonsTab.setPosition(getParent().getWidth() - buttonsTab.getWidth(), 0);

    	
    	if(!positionAdjusted){
    		positionAdjusted = true;
    				
    		float relX = 1f;
        	float relY = .1f;

        	Widget p = entityCreator.getParent();
        	entityCreator.setPosition(
        			p.getInnerX() + (int)((p.getInnerWidth() - entityCreator.getWidth()) * relX),
        			p.getInnerY() + (int)((p.getInnerHeight() - entityCreator.getHeight()) * relY));
    	}

    }
    
    private void requestObjectSelection(float screenX, float screenY){
    	if(!Mouse.isGrabbed()){
    		EntitySelectionRequest req = new EntitySelectionRequest(screenX, screenY);
    		req.addSelectionHandler(this);

    		state.getEntityManager().addEntitySelectionRequest(req);
    	}
    }
    
    public void handleSelectedEntity(){
    	if(selectionReq != null){
    		if(Mouse.isButtonDown(0)){
    			movingEntity = true;
    			if(pickedModelPosition == null){
            		pickedWorldPosition = new Vector3f(selectionReq.getScreenX(), 
            				selectionReq.getScreenY() , 
            				selectionReq.getDepth());
            		
            		state.getCamera().unproject(pickedWorldPosition, RenderState.updating());
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
            		state.getCamera().unproject(pickedMouseWorld, RenderState.updating());
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

 
            			body.applyForce(force, modelChangeRotation);
            			
            			body.activate();
            		}else if(!(selectedEntity instanceof StaticEntity)){
            			ModeledEntity entity = ((ModeledEntity) selectedEntity);
            			Matrix4f m2 = entity.getTransformMatrix(RenderState.updating()).copy();
            			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            				
            				m2.inv();

            				Vector3f mouseAtModel = pickedMouseWorld.copy();
            				mouseAtModel.prj(m2).nor();
            				if(lastRotationVector == null){
            					lastRotationVector = mouseAtModel.copy();
            				}else{
            					QuaternionUtil.shortestArcQuat(mouseAtModel, lastRotationVector, curQuat);
            					
                				Matrix4f newRotation = new Matrix4f();
                				newRotation.set(curQuat);
                				
                				entity.rotationMatrix.mul(newRotation);
                				
            					lastRotationVector = mouseAtModel.copy();
            				}
            			}else{
            				

                			Vector3f pickedWorldPosition = pickedModelPosition.copy().prj(m2);
                			
                			Vector3f delta = pickedMouseWorld.sub(pickedWorldPosition);
                			
                			entity.setPosition(entity.getPosition(RenderState.updating()).add(delta));
            			}

            		}

    			}
    		}else{
    			movingEntity = false;
    			
    			selectionReq = null;
    			pickedModelPosition = null;
    			lastRotationVector = null;

    		}
    	}
    }

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
                	this.focusFirstChild();
                	requestObjectSelection(Mouse.getX(), Mouse.getY());
                }
                break;
            default: break;
        }
        return evt.isMouseEventNoWheel();
    }

	@Override
	public void requestDone(EntitySelectionRequest req) {
		selectionReq = req;
		setSelectedEntity(selectionReq.getEntity());
		if(getSelectedEntity() instanceof ModeledEntity && !(getSelectedEntity() instanceof StaticEntity) &&
				!(getSelectedEntity() instanceof DynamicEntity))
			entityCreator.setCreatingEntity((ModeledEntity)getSelectedEntity());
		else
			entityCreator.setCreatingEntity(null);
	}
	
	public boolean isEntitySelected(){
		return movingEntity;
	}

	public Entity getSelectedEntity(){
		return selectedEntity;
	}
	
	public void setSelectedEntity(Entity e){
		selectedEntity = e;
		entityInfoWindow.showInfoOn(e);
	}
	
}
