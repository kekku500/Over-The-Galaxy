package main.state;

/**
 * This class is used to tell RenderThread and UpdateThread which StateVariable variable to use.
 * StateVariable is a container for a variable of 3 different states (updating, uptodate, rendering).
 * Single variable is copied 2 times to get 3 objects. 
 *
 * Main purpose is to make sure that update and render thread know which variables to use.
 * If entities are currently being rendered, they can't be updated (read only variables).
 * If entities are being updated, they can't be use for rendering.
 */
public class RenderState {
	
	//STATUSES
	private boolean rendering = false;
	private boolean updating = false; 

	private static int updatingId = -1; 
	private static int upToDateId = State.FIRST_STATE;
	private static int renderingId = -1;
	
	private int frame; 
	
	private int id;
	
	public RenderState(int id, int frame){
		this.id = id;
		this.frame = frame;
	}
	
	public static int updating(){
		if(updatingId != -1)
			return updatingId;
		return upToDateId;
	}
	
	public static int rendering(){
		return renderingId;
	}
	
	public static int uptodate(){
		return upToDateId;
	}
	
	public void setRendering(boolean b){
		if(b)
			renderingId = id;
		else
			renderingId = -1;
		rendering = b;
	}
	
	public void setFrameCount(int f){
		frame = f;
	}
	
	public void setUpdating(boolean b){
		if(b){ //true
			updatingId = id;
		}else{ //false
			updatingId = -1;
			upToDateId = id;
		}
		updating = b;
	}
	
	public void setId(int i){
		id = i;
	}

	public boolean isRendering(){
		return rendering;
	}
	
	public boolean isUpdating(){
		return updating;
	}
	
	public int getFrameCount(){
		return frame;
	}
	
	public int getId(){
		return id;
	}
	

}
