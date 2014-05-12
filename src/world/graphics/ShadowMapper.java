package world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INTENSITY;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BORDER_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameter;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_COMPARE_R_TO_TEXTURE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL14.GL_DEPTH_TEXTURE_MODE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glFramebufferTextureLayer;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import state.RenderState;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import world.culling.ViewFrustum;
import world.entity.Entity;
import world.entity.lighting.Lighting;

public class ShadowMapper {
	
	private final boolean CUBE;
	private float cubeZNear = 0.125f;
	private float cubeZFar = 256f;
	
	public Matrix4f projection = new Matrix4f();
	public Matrix4f[] view;
	private Matrix4f[] lightTexture;
	
	private int shadowMap;
	private int shadowMapSize = 2048;
	
	private Vector3f sceneOrigin = new Vector3f();
	private float sceneBoundingRadius = 250;
	
	private boolean shadowEnabled = true;
	
	private Vector3f position = new Vector3f();
	
	private ViewFrustum[] shadowFrustum;
	
	private Lighting parent;

	public ShadowMapper(boolean cube){
		CUBE = cube;
		
		if(CUBE){
			view = new Matrix4f[]{new Matrix4f(),new Matrix4f(),new Matrix4f(),
					new Matrix4f(),new Matrix4f(),new Matrix4f()};
			lightTexture = new Matrix4f[]{new Matrix4f(),new Matrix4f(),new Matrix4f(),
					new Matrix4f(),new Matrix4f(),new Matrix4f()};	
			shadowFrustum = new ViewFrustum[]{new ViewFrustum(),new ViewFrustum(),new ViewFrustum(),
				new ViewFrustum(),new ViewFrustum(),new ViewFrustum()};
		}else{
			view = new Matrix4f[]{new Matrix4f()};
			lightTexture = new Matrix4f[]{new Matrix4f()};	
			shadowFrustum = new ViewFrustum[]{new ViewFrustum()};
		}
	
	}
	
	public void init(){
		if(CUBE){
            glLoadIdentity();
            gluPerspective(90, 1, cubeZNear, cubeZFar);
            FloatBuffer temp = BufferUtils.createFloatBuffer(16);
            glGetFloat(GL_MODELVIEW_MATRIX, temp);
            projection.set(temp);
            for(int i=0;i<6;i++){
            	shadowFrustum[i].setProjection(90, 1, cubeZNear, cubeZFar);	
            }
		}
		
    	shadowMap = glGenTextures();
    	if(CUBE){
            glBindTexture(GL_TEXTURE_2D_ARRAY, shadowMap);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
            glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_DEPTH_COMPONENT32, shadowMapSize, shadowMapSize, 6, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    	}else{
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

	}
	
	public void setParent(Lighting parent){
		this.parent = parent;
	}
	
	public Lighting getParent(){
		return parent;
	}
	
	public void update(Matrix4f cameraView){
		setPos(parent.getPosition(RenderState.getUpdatingId()));
		if(CUBE){
			view[0] = Matrix4f.viewMatrixDirectional(getPos(), new Vector3f(1,0,0), new Vector3f(0,1,0));
			view[1] = Matrix4f.viewMatrixDirectional(getPos(), new Vector3f(-1,0,0), new Vector3f(0,1,0));
			view[2] = Matrix4f.viewMatrixDirectional(getPos(), new Vector3f(0,1,0), new Vector3f(0,0,1));
			view[3] = Matrix4f.viewMatrixDirectional(getPos(), new Vector3f(0,-1,0), new Vector3f(0,0,-1));
			view[4] = Matrix4f.viewMatrixDirectional(getPos(), new Vector3f(0,0,1), new Vector3f(0,1,0));
			view[5] = Matrix4f.viewMatrixDirectional(getPos(), new Vector3f(0,0,-1), new Vector3f(0,1,0));

	    	Matrix4f cameraViewInverse = cameraView.copy().inv();
	    	Matrix4f biasMatrix = Matrix4f.BIASMATRIX;

	    	lightTexture[0] = cameraViewInverse.copy().mul(view[0]).mul(projection).mul(biasMatrix);
	    	lightTexture[1] = cameraViewInverse.copy().mul(view[1]).mul(projection).mul(biasMatrix);
	    	lightTexture[2] = cameraViewInverse.copy().mul(view[2]).mul(projection).mul(biasMatrix);
	    	lightTexture[3] = cameraViewInverse.copy().mul(view[3]).mul(projection).mul(biasMatrix);
	    	lightTexture[4] = cameraViewInverse.copy().mul(view[4]).mul(projection).mul(biasMatrix);
	    	lightTexture[5] = cameraViewInverse.copy().mul(view[5]).mul(projection).mul(biasMatrix);
	    	
	    	//Shadow view culling preprocess
	    	for(int i=0;i<6;i++){
				Matrix4f viewMatrix = view[i].copy().trans();
				float[] ray = new float[4];
				viewMatrix.getRow(2, ray);
				Vector3f viewRay = new Vector3f(-ray[0], -ray[1], -ray[2]);
				float[] up = new float[4];
				viewMatrix.getRow(1, up);
				Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
				float[] right = new float[4];
				viewMatrix.getRow(0, right);
				Vector3f rightVector = new Vector3f(right[0], right[1],right[2]);
				
		    	shadowFrustum[i].setView(viewRay, upVector, rightVector);
		    	shadowFrustum[i].setPos(getPos());
	    	}
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
	        shadowFrustum[0].setProjection(fieldOfView, 1, nearPlane, nearPlane + (2.0F * sceneBoundingRadius));	
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
	        
	        //Shadow view culling
			Matrix4f viewMatrix = view[0].copy().trans();
			float[] ray = new float[4];
			viewMatrix.getRow(2, ray);
			Vector3f viewRay = new Vector3f(-ray[0], -ray[1], -ray[2]);
			float[] up = new float[4];
			viewMatrix.getRow(1, up);
			Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
			float[] right = new float[4];
			viewMatrix.getRow(0, right);
			Vector3f rightVector = new Vector3f(right[0], right[1],right[2]);
			
	    	shadowFrustum[0].setView(viewRay, upVector, rightVector);
	    	shadowFrustum[0].setPos(getPos());
	        
	    	Matrix4f cameraViewInverse = cameraView.copy().inv();
	    	Matrix4f biasMatrix = Matrix4f.BIASMATRIX.copy();
	    	lightTexture[0] = cameraViewInverse.mul(view[0]).mul(projection).mul(biasMatrix);
		}

	}
	
	public void renderToTexture(){
        glMatrixMode(GL_MODELVIEW);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowMap, 0);
        glClear(GL_DEPTH_BUFFER_BIT);
        glLoadMatrix(view[0].fb());
	}
	
	public void renderToTexture(int i){
        glMatrixMode(GL_MODELVIEW);
        glFramebufferTextureLayer(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, shadowMap, 0, i);
        glClear(GL_DEPTH_BUFFER_BIT);
        glLoadMatrix(view[i].fb());
	}
	
	public void setShadowProjection(){
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection.fb());
	}
	
	public FloatBuffer[] getLightTexture(){
		FloatBuffer[] fbs = null;
		if(CUBE){
			fbs = new FloatBuffer[6];
		}else{
			fbs = new FloatBuffer[1];
		}
		
		for(int i = 0;i<lightTexture.length;i++){
			fbs[i] = lightTexture[i].fb();
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
		if(parent != null)
			return shadowEnabled;
		return false;
	}

	public void setShadowEnabled(boolean shadowEnabled) {
		this.shadowEnabled = shadowEnabled;
	}
	
	public ViewFrustum[] getViewFrustum(){
		return shadowFrustum;
	}
	
	public boolean isCube(){
		return CUBE;
	}

}
