package game.world.entities;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import game.world.World;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Vector3f;

public abstract class AbstractEntity implements Entity{
	
	//Read EntityVariable class for info.
	EntityVariables[] variables = {new EntityVariables(), new EntityVariables(), new EntityVariables()};
	protected World world;
	protected int id;
	
	protected int vboVertexID;
	protected FloatBuffer vertices;
	
	public AbstractEntity(Vector3f pos){
		for(EntityVariables variable: variables){
			variable.setPos(pos);
		}
	}
	
	@Override
	public void render(){
		if(!isVBOGenerated())
			generateVBO();
		
		EntityVariables vars = variables[EntityVariables.getRendering()];
		Vector3f pos = vars.getPos();
		
		glTranslatef(pos.x, pos.y, pos.z);
		// Bind the vertex buffer
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(3, GL_FLOAT, 0, 0);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    
		render2();
		
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
		glTranslatef(-pos.x, -pos.y, -pos.z);
	}
	
	@Override
	public void generateVBO() {
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void dispose(){
	    // Dispose the buffers
	    glDeleteBuffers(vboVertexID);
	}
	
	//ABSTRACT
	public abstract void render2();
	
	//SET
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void setWorld(World world) {
		this.world = world;
	}
	
	//GET
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public Vector3f getPos() {
		Vector3f pos = getVariables(EntityVariables.getUpdating()).getPos();
		return pos;
	}
	
	@Override
	public EntityVariables getVariables(int i) {
		return variables[i];
	}
	
	@Override
	public boolean isVBOGenerated(){
		if(vboVertexID == 0)
			return false;
		return true;
	}
	
	@Override
	public int getVboID(){
		return vboVertexID;
	}

}
