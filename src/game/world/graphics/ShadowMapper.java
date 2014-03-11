package game.world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glFramebufferTextureLayer;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

import utils.math.Matrix4f;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class ShadowMapper {
	
	private final boolean cube;
	
	public Matrix4f projection = new Matrix4f();
	public Matrix4f[] view;
	private Matrix4f[] lightTexture;
	
	private int shadowMap;
	private int shadowMapSize = 512;
	
	private Vector3f sceneOrigin = new Vector3f();
	private float sceneBoundingRadius = 75;
	
	private boolean shadowEnabled = true;
	
	private Vector3f position = new Vector3f();

	public ShadowMapper(boolean cube){
		this.cube = cube;
		if(cube){
			view = new Matrix4f[6];
			lightTexture = new Matrix4f[6];
		}else{
			view = new Matrix4f[1];
			view[0] = new Matrix4f();
			lightTexture = new Matrix4f[1];		
		}
	}
	
	public void init(){
        if(cube){
            glLoadIdentity();
            gluPerspective(90, 1, 0.125f, 256.0f);
            FloatBuffer temp = BufferUtils.createFloatBuffer(16);
            glGetFloat(GL_MODELVIEW_MATRIX, temp);
            projection.set(temp);
        }
    	
    	shadowMap = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, shadowMap);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
        if(cube)
        	glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_DEPTH_COMPONENT32, shadowMapSize, shadowMapSize, 6, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
        else
        	glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_DEPTH_COMPONENT32, shadowMapSize, shadowMapSize, 1, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
	}
	
	public void update(Matrix4f cameraView, Vector3f pos){
		setPos(pos);
		if(cube){
	        view[0] = Matrix4f.viewMatrix(new Vector3f( 0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), getPos());
	    	view[1] = Matrix4f.viewMatrix(new Vector3f( 0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), getPos());
	    	view[2] = Matrix4f.viewMatrix(new Vector3f( 1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, -1.0f, 0.0f), getPos());
	    	view[3] = Matrix4f.viewMatrix(new Vector3f( 1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), getPos());
	    	view[4] = Matrix4f.viewMatrix(new Vector3f( -1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f), getPos());
	    	view[5] = Matrix4f.viewMatrix(new Vector3f( 1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), getPos());
	    	
	    	Matrix4f cameraViewInverse = cameraView.copy().getInvert();
	    	Matrix4f biasMatrix = Matrix4f.biasMatrix.copy();
	    	lightTexture[0] = biasMatrix.mul(projection).mul(view[0]).mul(cameraViewInverse);
	    	biasMatrix = Matrix4f.biasMatrix.copy();
	    	lightTexture[1] = biasMatrix.mul(projection).mul(view[1]).mul(cameraViewInverse);
	    	biasMatrix = Matrix4f.biasMatrix.copy();
	    	lightTexture[2] = biasMatrix.mul(projection).mul(view[2]).mul(cameraViewInverse);
	    	biasMatrix = Matrix4f.biasMatrix.copy();
	    	lightTexture[3] = biasMatrix.mul(projection).mul(view[3]).mul(cameraViewInverse);
	    	biasMatrix = Matrix4f.biasMatrix.copy();
	    	lightTexture[4] = biasMatrix.mul(projection).mul(view[4]).mul(cameraViewInverse);
	    	biasMatrix = Matrix4f.biasMatrix.copy();
	    	lightTexture[5] = biasMatrix.mul(projection).mul(view[5]).mul(cameraViewInverse);
		}else{
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
	        view[0].set(fb);
	        
	        glPopMatrix();
	        
	    	Matrix4f cameraViewInverse = cameraView.copy().getInvert();
	    	Matrix4f biasMatrix = Matrix4f.biasMatrix.copy();
	    	lightTexture[0] = biasMatrix.mul(projection).mul(view[0]).mul(cameraViewInverse);
		}
	}
	
	public void renderToTexture(int i){
        glMatrixMode(GL_MODELVIEW);
        glFramebufferTextureLayer(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, shadowMap, 0, i);
        glClear(GL_DEPTH_BUFFER_BIT);
        glLoadMatrix(view[i].asFlippedFloatBuffer());
	}
	
	public void setShadowProjection(){
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection.asFlippedFloatBuffer());
	}
	
	public FloatBuffer[] getLightTexture(){
		FloatBuffer[] fbs = null;
		if(cube){
			fbs = new FloatBuffer[6];
		}else{
			fbs = new FloatBuffer[1];
		}
		
		for(int i = 0;i<lightTexture.length;i++){
			fbs[i] = lightTexture[i].asFlippedFloatBuffer();
		}
		return fbs;
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
		return shadowEnabled;
	}

	public void setShadowEnabled(boolean shadowEnabled) {
		this.shadowEnabled = shadowEnabled;
	}

	public boolean isCube() {
		return cube;
	}

}
