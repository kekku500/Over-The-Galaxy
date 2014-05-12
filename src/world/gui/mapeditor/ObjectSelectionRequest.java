package world.gui.mapeditor;

import org.lwjgl.opengl.Display;

import world.entity.VisualEntity;

public class ObjectSelectionRequest {
	
	private VisualEntity entity;
	private final float screenX, screenY;
	private float depth;
	
	private EditorRequest handler;
	
	public ObjectSelectionRequest(float screenX, float screenY){
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
	
	public EditorRequest getCallback(){
		return handler;
	}
	
	public void addCallback(EditorRequest handler){
		this.handler = handler;
	}

}
