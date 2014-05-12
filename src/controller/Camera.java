package controller;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import state.Game;
import state.RenderState;
import state.StateVariable;
import state.threading.RenderThread;
import utils.math.Matrix4f;
import utils.math.Ray;
import utils.math.Vector2f;
import utils.math.Vector3f;
import utils.math.Vector4f;
import world.EntityManager;
import world.culling.ViewFrustum;
import world.entity.AbstractEntity;

public class Camera extends AbstractEntity{
	
	//public final Matrix4f view;
	public final Matrix4f projection;

	public float fov, zNear, zFar, aspect;
	public float viewportWidth, viewportHeight;
    
	public StateVariable<ViewFrustum> cameraFrustum;
	
	Ray ray = new Ray();
    
    public Camera(EntityManager world, float x, float y, float z){
    	super(world);
    	setPosition(x, y, z);
    	
		projection = new Matrix4f();
		cameraFrustum = new StateVariable<ViewFrustum>(new ViewFrustum());
    }

     
    @Override
    public void update(float dt){
		cameraFrustum.updating().setView(getViewRay(RenderState.updating()), getRightVector(RenderState.updating()), getUpVector(RenderState.updating()));
		cameraFrustum.updating().setPos(getPosition(RenderState.getUpdatingId()));
		cameraFrustum.updating().cullEntities(getEntityManager().getVisualEntities(RenderState.getUpdatingId()));
    }
    
    public void setViewport(float viewportWidth, float viewportHeight){
    	this.viewportWidth = viewportWidth;
    	this.viewportHeight = viewportHeight;
    }
    
    public void setProjection(float fov, float aspect, float zNear, float zFar){
    	this.fov = fov;
    	this.aspect = aspect;
    	this.zNear = zNear;
    	this.zFar = zFar;
    	for(ViewFrustum view : cameraFrustum.vars)
    		view.setProjection(fov, aspect, zNear, zFar);
    	projection.set(Matrix4f.perspectiveMatrix(fov, aspect, zNear, zFar));
    }
    
    public void resized(float width, float height){
		setProjection(fov, width / height, zNear, zFar);
    }
    
    public Vector3f getUpVector(int state){
    	Matrix4f viewMatrix = new Matrix4f();
		getTransform(state).getMatrix(viewMatrix);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
    	return new Vector3f(up[0], up[1], up[2]);
    }
    
    public Vector3f getRightVector(int state){
    	Matrix4f viewMatrix = new Matrix4f();
		getTransform(state).getMatrix(viewMatrix);
		float[] right = new float[4];
		viewMatrix.getRow(0, right);
    	return new Vector3f(right[0], right[1], right[2]);
    }
    
    public Vector3f getViewRay(int state){
    	Matrix4f viewMatrix = new Matrix4f();
		getTransform(state).getMatrix(viewMatrix);
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
    	return new Vector3f(ray[0], ray[1], ray[2]);
    }
    
	@Override
	public void render() {}
	
	public Vector3f unproject(Vector3f screenCoords, float viewportX, float viewPortY, float viewPortWidth, float viewPortHeight){
		float x = screenCoords.x, y = screenCoords.y;
		x = x - viewportX;
		y = y - viewPortY;
		screenCoords.x = (2 * x) / viewPortWidth - 1;
		screenCoords.y = (2 * y) / viewPortHeight - 1;
		screenCoords.z = 2 * screenCoords.z - 1;
		
		Matrix4f invProjView = projection.copy();
		invProjView.mulLeft(getTransform(RenderState.updating()).getOpenGLViewMatrix());
		invProjView.inv();
		invProjView.trans();
		screenCoords.prj(invProjView);
		
		return screenCoords;
	}
	
	public Vector3f unproject(Vector3f screenCoords){
		return unproject(screenCoords, 0, 0, Display.getWidth(), Display.getHeight());
	}
	
	public Vector3f project(Vector3f objCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight){
		Matrix4f combined = getTransform(RenderState.updating()).getOpenGLViewMatrix().mul(projection);
		combined.trans();
		
		objCoords.prj(combined);
		objCoords.x = viewportWidth * (objCoords.x + 1) / 2 + viewportX;
		objCoords.y = viewportHeight * (objCoords.y + 1) / 2 + viewportY;
		objCoords.z = (objCoords.z + 1) / 2;
		return objCoords;
	}
	
	public Vector3f project(Vector3f objCoords){
		return project(objCoords, 0, 0, Display.getWidth(), Display.getHeight());
	}
	
	public Ray getPickRay (float screenX, float screenY, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
			ray.origin.set(screenX, screenY, 0);
			ray.direction.set(screenX, screenY, 1f);
			unproject(ray.origin, viewportX, viewportY, viewportWidth, viewportHeight);
			unproject(ray.direction, viewportX, viewportY, viewportWidth, viewportHeight);
			
			ray.direction.sub(ray.origin.copy());
			ray.direction.nor();
			
			return ray;
	}
	
	public Ray getPickRay (float screenX, float screenY) {
		return getPickRay(screenX, screenY, 0, 0, Display.getWidth(), Display.getHeight());
	}
    
}
