package game.world.entities;

import game.Game;
import game.world.World;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import main.Main;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Box implements Entity{
	
	private float w, h;
	
	int vboVertexID;
	
	private int id;
	
	FloatBuffer vertices;
	
	EntityVariables[] variables = {new EntityVariables(), new EntityVariables(), new EntityVariables()};
	
	private World world;
	
	public Box(float x, float y, float w, float h){
		for(EntityVariables variable: variables){
			variable.setPos(new Vector2f(x, y));
		}
		this.w = w;
		this.h = h;
		
		vertices = BufferUtils.createFloatBuffer(2 * 4);
		
		vertices.put(new float[]
				{0,0, w,0, w,h, 0,h});
		vertices.rewind();
	}
	
	public boolean isVBOGenerated(){
		if(vboVertexID == 0)
			return false;
		return true;
	}
	
	public Vector2f getPos(){
		int variableId = getWorld().getLatestState().getId();
		Vector2f pos = variables[variableId].getPos();
		Vector2f realPos = new Vector2f(pos.x, pos.y);
		return realPos;
	}
	
	@Override
	public void setX(float x){
		int variableId = getWorld().getLatestState().getId();
		variables[variableId].getPos().x = x;
	}
	
	@Override
	public void setY(float y){
		int variableId = getWorld().getLatestState().getId();
		variables[variableId].getPos().y = y;
	}
	
	public float getWidth(){
		return w;
	}
	
	public float getHeight(){
		return h;
	}
	
	
	@Override
	public void render(){
		if(!isVBOGenerated())
			generateVBO();
		glTranslatef(getPos().x, getPos().y, 0);
		
		// Bind the vertex buffer
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(2, GL_FLOAT, 0, 0);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    
		glDrawArrays(GL_QUADS, 0, 4);
	    
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
		glTranslatef(-getPos().x, -getPos().y, 0);
	}

	@Override
	public Entity getCopy() {
		return null;
	}
	
	public int getVboID(){
		return vboVertexID;
	}
	
	@Override
	public void dispose(){
	    // Dispose the buffers
	    glDeleteBuffers(vboVertexID);
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
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
	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public EntityVariables getVariables(int i) {
		return variables[i];
	}

	@Override
	public void generateVBO() {
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);	
	}


}
