package game.world.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 *	EntityVariables are used to store copies of entity variables. 
 *	Copies are required to use multithreading efficiently.
 *	For example. One EntityVariable class is used to render entity position. The same
 *	EntityVariable can't be used for collision detection because rendered position must
 *	not change suddenly in the frame or it will create flickering. Rendered variables must be read-only.
 */

public class EntityVariables {
	
	private Vector3f pos;
	
	//Keeps track which entity variables are in which state.
	public static int updating = 0; //These variables are changed and used for calcluations.
	public static int upToDate = 1; //The most up to date variables, used for copying upToDate variables to updating.
	public static int rendering = 1; //Used for rendering
	
	public EntityVariables(){
		pos = new Vector3f();
	}
	
	//SET
	public void setPos(Vector3f p){
		pos = p;
	}
	
	//GET
	public Vector3f getPos(){
		return pos;
	}
	
	//STATIC
	public static void setUpdating(int i){
		updating = i;
	}
	
	public static void setUpToDate(int i){
		upToDate = i;
	}
	
	public static void setRendering(int i){
		rendering = i;
	}
	
	public static int getUpToDate(){
		return upToDate;
	}
	
	public static int getRendering(){
		return rendering;
	}
	
	public static int getUpdating(){
		return updating;
	}
	
}
