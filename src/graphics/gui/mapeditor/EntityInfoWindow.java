package graphics.gui.mapeditor;

import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import entity.blueprint.AbstractVisualPhysicsEntity;
import entity.creation.ModeledEntity;
import entity.sheet.Entity;

public class EntityInfoWindow extends CloseableFrame{
	
	private Entity currentlyShowingEntity;
	
	private Label entityClass;
	Button removeEntityBut;
	Button removeBodyBut;
	final String[] strs = {"No entity selected"};
	
	public EntityInfoWindow(){
		setTitle("Entity Info");
		setTheme("resizableframe-title");
		hide();
		
		entityClass = new Label();
		
		BoxLayout infos = new BoxLayout(BoxLayout.Direction.VERTICAL);
		infos.setTheme("");
		add(infos);
		
		removeEntityBut = new Button("Remove");
		
		removeEntityBut.addCallback(new Runnable(){

			@Override
			public void run() {
				if(currentlyShowingEntity != null){
					currentlyShowingEntity.requestRemoval();
					showInfoOn(null);
				}		
			}});
		
		removeBodyBut = new Button("Remove Body");
		
		removeBodyBut.addCallback(new Runnable(){

			@Override
			public void run() {
				if(currentlyShowingEntity != null && currentlyShowingEntity instanceof AbstractVisualPhysicsEntity){
					AbstractVisualPhysicsEntity vd = (AbstractVisualPhysicsEntity)currentlyShowingEntity;
					//create bodyless entity
					ModeledEntity newEntity = new ModeledEntity(currentlyShowingEntity.getEntityManager());
					//copy model
					newEntity.setModel(vd.getModel());
					//copy matrix
					newEntity.setStateTransform(vd.getTransform());
					newEntity.rotationMatrix = vd.rotationMatrix;
					newEntity.scaleRotationMatrix = vd.scaleRotationMatrix;
					//copy children
					for(Entity child: vd.getChildren()){
						newEntity.addChild(child);
					}
					
					//remove bodied entity
					currentlyShowingEntity.getEntityManager().removeEntity(currentlyShowingEntity);
					
					//new reference to selected
					showInfoOn(newEntity);
				}
				
			}
			
		});
		
		infos.add(entityClass);
		infos.add(removeEntityBut);
		infos.add(removeBodyBut);
		
		showInfoOn(null);
	}
	
	public void showInfoOn(Entity e){
		if(e != null){
			currentlyShowingEntity = e;
			
			entityClass.setText(e.getClass().getName());
			
			removeEntityBut.setVisible(true);
			removeBodyBut.setVisible(true);
			/*if(e instanceof DynamicEntity){
				
			}*/
		}else{
			entityClass.setText(strs[0]);
			removeEntityBut.setVisible(false);
			removeBodyBut.setVisible(false);
		}

	}

}
