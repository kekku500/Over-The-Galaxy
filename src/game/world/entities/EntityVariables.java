package game.world.entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class EntityVariables {
	
	private Vector2f pos;
	
	//Keeps track which entity variables are in which state
	public static int updating = -1;
	public static int upToDate = 0;
	public static int rendering = -1;
	
	public EntityVariables(){
		pos = new Vector2f();
	}
	
	//SET
	public void setPos(Vector2f p){
		pos = p;
	}
	
	//GET
	public Vector2f getPos(){
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
