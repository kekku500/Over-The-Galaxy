package game.world.entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class EntityVariables {
	
	private Vector2f pos;
	
	public EntityVariables(){
		pos = new Vector2f();
	}
	
	public Vector2f getPos(){
		return pos;
	}
	
	public void setPos(Vector2f p){
		pos = p;
	}

}
