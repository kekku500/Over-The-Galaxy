package entitymanager;

import entity.sheet.VisualEntity;

public class EntitySelectionRequest {
	
	private VisualEntity entity;
	private final float screenX, screenY;
	private float depth;
	
	private EntitySelectionHandler handler;
	
	public EntitySelectionRequest(float screenX, float screenY){
		this.screenX = screenX;
		this.screenY = screenY;
		
	}
	
	public float getScreenX(){
		return screenX;
	}
	
	public float getScreenY(){
		return screenY;
	}
	
	public void setDepth(float depth){
		this.depth = depth;
	}
	
	public float getDepth(){
		return depth;
	}
	
	public void setEntity(VisualEntity entity){
		this.entity = entity;
	}
	
	public VisualEntity getEntity(){
		return entity;
	}
	
	public EntitySelectionHandler getSelectionHandler(){
		return handler;
	}
	
	public void addSelectionHandler(EntitySelectionHandler handler){
		this.handler = handler;
	}

}
