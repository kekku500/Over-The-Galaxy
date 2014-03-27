package world.entity.gui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glMultMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glUniform1i;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import resources.model.Model;
import world.World;

public abstract class AbstractComponent implements Component{
	
	//Read EntityVariable class for info.
	protected Vector2f position;
	protected int width, height;
	protected float angle;
	protected Component master;
	protected int id;
	
	protected int vboVertexID;
	protected FloatBuffer vertices;
	
	//Texturing
	protected int vboTexVertexID;
	protected FloatBuffer texVertices;
	protected int texture;
	protected boolean isTextured;
	
	@Override
	public void render(){
		glPushMatrix(); //save current transformations
		
		Vector2f pos = getPosition();
		float angle = getAngle();
		
		glTranslatef(pos.x, pos.y, 0);
		
    	glRotatef(-angle, 0.0f,0.0f,1.0f);
        
		// Bind the vertex buffer
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(2, GL_FLOAT, 0, 0);
		
		//Texture
		if(isTextured){
		    glEnable(GL_TEXTURE_2D);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
            glTexCoordPointer(2, GL_FLOAT, 0, 0);
        	glBindTexture(GL_TEXTURE_2D, texture);
		}
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    
		renderDraw();
		
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
        if(isTextured){
        	glBindTexture(GL_TEXTURE_2D, 0);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    	    glDisable(GL_TEXTURE_2D);
        }

	    glPopMatrix(); //reset transformations
	}
	
	public abstract void renderInitStart();
	
	@Override
	public void renderInit() {
		renderInitStart();
		
		
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void dispose(){
	    // Dispose the buffers
	    glDeleteBuffers(vboVertexID);
	    if(isTextured){
		    glDeleteBuffers(vboTexVertexID);
	    	glDeleteTextures(texture);
	    }
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
	public void setID(int id) {
		this.id = id;
	}
	
	@Override
	public void setAngle(float a){
		angle = a;
	}
	
	//GET
	@Override
	public int getID() {
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
