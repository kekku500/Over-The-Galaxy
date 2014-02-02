package game.world.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import game.world.World;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class AbstractComponent implements Component{
	
	//Read EntityVariable class for info.
	protected Vector2f position;
	protected int width, height;
	protected float angle;
	protected Component master;
	protected int id;
	
	protected int vboVertexID;
	protected FloatBuffer vertices;
	
	@Override
	public void render(){
		Vector2f pos = getPosition();
		float angle = getAngle();
		
		glTranslatef(pos.x, pos.y, 0);
		
    	glRotatef(-angle, 0.0f,0.0f,1.0f);
        
		// Bind the vertex buffer
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(2, GL_FLOAT, 0, 0);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    
		renderDraw();
		
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
	    glRotatef(-angle, 0.0f, 1.0f, 0.0f);
        
		glTranslatef(-pos.x, -pos.y, 0);
	}
	
	@Override
	public void createVBO() {
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
	public abstract void renderDraw();
	
	public abstract void update();
	
	//SET
	@Override
	public void setMaster(Component m) {
		master = m;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void setAngle(float a){
		angle = a;
	}
	
	//GET
	@Override
	public int getId() {
		return id;
	}
	
	public float getAngle(){
		return angle;
	}
	
	@Override
	public Component getMaster() {
		return master;
	}
	
	@Override
	public Vector2f getPosition(){
		return position;
	}

	
	private boolean isVBOGenerated(){
		if(vboVertexID == 0)
			return false;
		return true;
	}

}
