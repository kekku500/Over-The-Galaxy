package world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.GL_CLAMP_FRAGMENT_COLOR;
import static org.lwjgl.opengl.GL30.GL_CLAMP_READ_COLOR;
import static org.lwjgl.opengl.GL30.GL_CLAMP_VERTEX_COLOR;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT7;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glFramebufferTextureLayer;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

import state.Game;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import utils.math.Vector4f;
import world.culling.ViewFrustum;
import world.entity.Entity;
import world.entity.WorldEntity;

public class ShadowMapper {
	
	public Matrix4f projection = new Matrix4f();
	public Matrix4f view;
	private Matrix4f lightTexture;
	
	private int shadowMap;
	private int shadowMapSize = 1024;
	
	private Vector3f sceneOrigin = new Vector3f();
	private float sceneBoundingRadius = 100;
	
	private boolean shadowEnabled = true;
	
	private Vector3f position = new Vector3f();
	
	private ViewFrustum shadowFrustum;
	private WorldEntity shadowCaster;

	public ShadowMapper(){
		shadowFrustum = new ViewFrustum();
		view = new Matrix4f();
		lightTexture = new Matrix4f();		
	}
	
	public void setCaster(WorldEntity e){
		shadowCaster = e;
	}
	
	public void init(){
    	shadowMap = glGenTextures();
    	glBindTexture(GL_TEXTURE_2D, shadowMap);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    	
    	//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        FloatBuffer fb = BufferUtils.createFloatBuffer(4);
        fb.put(1).put(1).put(1).put(1);
        fb.flip();
        glTexParameter(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, fb);
    	
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
    	glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
    	glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, shadowMapSize, shadowMapSize, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
    	glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void update(Matrix4f cameraView){
		setPos(shadowCaster.getPosition());

    	//Calculate & save matrices
        glPushMatrix();

        //Projection
        Vector3f tempSceneOrigin = sceneOrigin.copy();
        tempSceneOrigin.negate();
        tempSceneOrigin.add(getPos());
        float lightToSceneDistance = tempSceneOrigin.length();
        
        float nearPlane = lightToSceneDistance - sceneBoundingRadius;
        if (nearPlane < 0) {
            System.err.println("Camera is too close to scene. A valid shadow map cannot be generated.");
        }
        float fieldOfView = (float) Math.toDegrees(2.0F * Math.atan(sceneBoundingRadius / lightToSceneDistance));
        glLoadIdentity();
        gluPerspective(fieldOfView, 1.0f, nearPlane, nearPlane + (2.0F * sceneBoundingRadius));
        shadowFrustum.setProjection(fieldOfView, 1, 1, nearPlane, nearPlane + (2.0F * sceneBoundingRadius));	
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        glGetFloat(GL_MODELVIEW_MATRIX, fb);
        projection.set(fb);

        //View
        glLoadIdentity();
        gluLookAt(getPos().x, getPos().y, getPos().z,
        sceneOrigin.x, sceneOrigin.y, sceneOrigin.z,
        0.0f, 1.0f, 0.0f);
        fb = BufferUtils.createFloatBuffer(16);
        glGetFloat(GL_MODELVIEW_MATRIX, fb);
        view.set(fb);
        
        glPopMatrix();
        
        //Shadow view culling
		Matrix4f viewMatrix = view.copy().transposeGet();
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		Vector3f viewRay = new Vector3f(-ray[0], -ray[1], -ray[2]);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
		float[] right = new float[4];
		viewMatrix.getRow(0, right);
		Vector3f rightVector = new Vector3f(right[0], right[1],right[2]);
		
    	shadowFrustum.setView(viewRay, upVector, rightVector);
    	shadowFrustum.setPos(getPos());
        
    	Matrix4f cameraViewInverse = cameraView.copy().invertGet();
    	Matrix4f biasMatrix = Matrix4f.biasMatrix.copy();
    	lightTexture = cameraViewInverse.mul(view).mul(projection).mul(biasMatrix);
	}
	
	public void renderToTexture(){
        glMatrixMode(GL_MODELVIEW);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowMap, 0);
        glClear(GL_DEPTH_BUFFER_BIT);
        glLoadMatrix(view.asFlippedFloatBuffer());
	}
	
	public void setShadowProjection(){
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection.asFlippedFloatBuffer());
	}
	
	public FloatBuffer getLightTexture(){
		return lightTexture.asFlippedFloatBuffer();
	}
	
	public void dispose(){
        glDeleteTextures(shadowMap);
	}
	
	public Vector3f getPos() {
		return position;
	}
	
	public void setPos(Vector3f position) {
		this.position = position;
	}
	
	public int getShadowMap() {
		return shadowMap;
	}

	public int getShadowMapSize() {
		return shadowMapSize;
	}

	public Vector3f getSceneOrigin() {
		return sceneOrigin;
	}

	public void setSceneOrigin(Vector3f sceneOrigin) {
		this.sceneOrigin = sceneOrigin;
	}

	public boolean isShadowEnabled() {
		if(shadowCaster != null)
			return shadowEnabled;
		return false;
	}

	public void setShadowEnabled(boolean shadowEnabled) {
		this.shadowEnabled = shadowEnabled;
	}
	
	public ViewFrustum getViewFrustum(){
		return shadowFrustum;
	}

}
