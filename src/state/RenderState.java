package state;

import utils.R;

public class RenderState {
	
	//STATUSES
	private boolean rendering = false;
	private boolean updating = false; 

	public static int updatingId = -1; 
	public static int upToDateId = State.FIRST_STATE;
	public static int renderingId = -1;
	
	private int frame; 
	
	private int id;
	

	
	public RenderState(int id, int frame){
		this.id = id;
		this.frame = frame;
	}

	
	
	/**
	 * @return Id of updating state. If no state is being updated, uptodate id is returned instead
	 */
	public static int getUpdatingId(){
		if(updatingId != -1)
			return updatingId;
		return upToDateId;
	}
	
	/**
	 * @return Id of state that is currently being rendered.
	 */
	public static int getRenderingId(){
		return renderingId;
	}
	
	public static int getUpToDateId(){
		return upToDateId;
	}
	
	public static int updating(){
		return getUpdatingId();
	}
	
	public static int rendering(){
		return getRenderingId();
	}
	
	public static int uptodate(){
		return getUpToDateId();
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
	
	//GET
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
