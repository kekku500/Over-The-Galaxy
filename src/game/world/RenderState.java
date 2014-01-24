package game.world;

import game.world.entities.EntityVariables;

public class RenderState {

	
	private boolean readOnly = false; //while rendering
	private boolean updating = false; //while being updated, not usable for rendering
	
	private int frame;
	
	private int id;
	
	public RenderState(int id){
		this.id = id;
	}
	
	//SET
	public void setReadOnly(boolean b){
		if(b)
			EntityVariables.setRendering(id);
		else
			EntityVariables.setRendering(-1);
		readOnly = b;
	}
	
	public void setFrameCount(int f){
		frame = f;
	}
	
	//GET
	public void setUpdating(boolean b){
		if(b){ //true
			EntityVariables.setUpdating(id); //starting update
		}else{ //false
			EntityVariables.setUpdating(-1); //update done
			EntityVariables.setUpToDate(id);
		}
		updating = b;
	}

	public boolean isReadOnly(){
		return readOnly;
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
