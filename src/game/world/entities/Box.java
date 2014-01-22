package game.world.entities;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
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

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

public class Box implements Entity{
	
	EntityVariables[] variables = {new EntityVariables(), new EntityVariables(), new EntityVariables()};
	private World world;
	
	private float w, h;
	private int id;
	
	int vboVertexID;
	FloatBuffer vertices;

	public Box(float x, float y, float w, float h){
		for(EntityVariables variable: variables){
			variable.setPos(new Vector2f(x, y));
		}
		this.w = w;
		this.h = h;
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(2 * 4);
		vertices.put(new float[]
				{0,0, w,0, w,h, 0,h});
		vertices.rewind();
	}
	
	@Override
	public void render(){
		if(!isVBOGenerated())
			generateVBO();
		EntityVariables vars = variables[EntityVariables.getRendering()];
		Vector2f pos = vars.getPos();
		glTranslatef(pos.x, pos.y, 0);
		
		// Bind the vertex buffer
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(2, GL_FLOAT, 0, 0);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    
		glDrawArrays(GL_QUADS, 0, 4);
	    
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
		glTranslatef(-pos.x, -pos.y, 0);
	}
	
	private boolean isVBOGenerated(){
		if(vboVertexID == 0)
			return false;
		return true;
	}
	
	@Override
	public void dispose(){
	    // Dispose the buffers
	    glDeleteBuffers(vboVertexID);
	}
	
	@Override
	public void generateVBO() {
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);	
	}
	
	@Override
	public void setWorld(World world) {
		this.world = world;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getVboID(){
		return vboVertexID;
	}
	
	@Override
	public float getWidth(){
		return w;
	}
	
	@Override
	public float getHeight(){
		return h;
	}
	

	

	










	@Override
	public EntityVariables getVariables(int i) {
		return variables[i];
	}




}
