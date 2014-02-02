package game.world.entities;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import game.world.World;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Action;

import java.nio.FloatBuffer;

import math.BoundingAxis;
import math.BoundingSphere;
import math.Vector3fc;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public abstract class AbstractEntity implements Entity{
	
	protected Vector3fc pos;
	protected Vector3fc toCenter;
	protected float pitch, yaw, roll;
	protected boolean visible = true; //in camera
	protected boolean sleeping = false; //object has moved
	
	protected Motion motion = Motion.STATIC;
	
	protected World world;
	protected int id;
	
	protected int vboVertexID;
	protected FloatBuffer vertices;
	
	protected BoundingAxis boundingAxis;
	protected BoundingSphere boundingSphere;
	protected float radius;
	
	private float old_pitch = Float.NaN, old_yaw = Float.NaN, old_roll = Float.NaN;
	private Vector3fc oldPos = new Vector3fc(Float.NaN, Float.NaN, Float.NaN);
	@Override
	public void update(float dt){
		firstUpdate(dt);
		midUpdate(dt);
		lastUpdate(dt);
		sleeping = true;
		if(!oldPos.equals(pos) || old_pitch != pitch || old_yaw != yaw || old_roll != roll){ 
			oldPos = pos.copy();
			old_pitch = pitch;
			old_yaw = yaw;
			old_roll = roll;
			sleeping = false;
		}
	}
	
	public abstract void firstUpdate(float dt);
	
	public abstract void midUpdate(float dt);
	
	public abstract void lastUpdate(float dt);
	
	@Override
	public void render(){
		if(!isVisible())
			return;
		//Get object center
		Vector3f toMidPoint = getToMidPoint();
		
		glPushMatrix(); //save current transformations
		
		glTranslatef(pos.x, pos.y, pos.z);
		glTranslatef(toMidPoint.x, toMidPoint.y, toMidPoint.z); //midpoint rotate
    	glRotatef(roll, 0.0f,0.0f,1.0f);
    	glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		glTranslatef(-toMidPoint.x, -toMidPoint.y, -toMidPoint.z); //back to start pos
        
		// Bind the vertex buffer
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glVertexPointer(3, GL_FLOAT, 0, 0);
	    
	    glEnableClientState(GL_VERTEX_ARRAY);
	    
		renderDraw();
		
	    glDisableClientState(GL_VERTEX_ARRAY);
	    
	    glPopMatrix(); //reset transformations
	}
	
	public abstract void renderDraw();
	
	@Override
	public void dispose(){
	    glDeleteBuffers(vboVertexID);
	}
	
	@Override
	public void createVBO() {
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void addRoll(float i) {
		roll += i;
	}
	
	@Override
	public void addYaw(float i) {
		yaw += i;
	}

	@Override
	public void addPitch(float i) {
		pitch += i;
	}
	
	@Override
	public void setMotion(Motion m){
		motion = m;
	}
	
	@Override
	public Motion getMotion(){
		return motion;
	}


	@Override
	public void setVBOVertexId(int i){
		vboVertexID = i;
	}
	
	@Override
	public int getVBOVertexId(){
		return vboVertexID;
	}
	
	@Override
	public void setPos(Vector3fc v){
		pos = v;
	}
	
	@Override
	public Vector3fc getPos() {
		return pos;
	}
	
	@Override
	public void setPitch(float i) {
		pitch = i;
	}
	
	@Override
	public float getPitch() {
		return pitch;
	}
	
	@Override
	public void setYaw(float i) {
		yaw = i;
	}
	
	@Override
	public float getYaw() {
		return yaw;
	}

	@Override
	public void setRoll(float i) {
		roll = i;
	}
	
	@Override
	public float getRoll() {
		return roll;
	}
	
	@Override
	public void setVisible(boolean b){
		visible = b;
	}
	
	@Override
	public boolean isVisible(){
		return visible;
	}
	
	@Override
	public void setWorld(World world) {
		this.world = world;
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public BoundingSphere getBoundingSphere(){
		return boundingSphere;
	}
	
	@Override
	public BoundingAxis getBoundingAxis(){
		return boundingAxis;
	}
	
	@Override
	public Vector3fc getToMidPoint(){
		return toCenter;
	}
	
	@Override
	public boolean isSleeping(){
		return sleeping;
	}

	public Entity copy2(Entity e){
		e.setPos(pos.copy());
		e.setPitch(getPitch());
		e.setYaw(getYaw());
		e.setRoll(getRoll());
		e.setVisible(isVisible());
		e.setWorld(getWorld());
		e.setId(getId());
		e.setVBOVertexId(getVBOVertexId());
		e.setMotion(getMotion());
		return e;
	}

}
