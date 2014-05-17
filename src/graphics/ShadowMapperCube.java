package graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INTENSITY;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
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
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL14.GL_COMPARE_R_TO_TEXTURE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL14.GL_DEPTH_TEXTURE_MODE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glFramebufferTextureLayer;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import entity.creation.Camera;
import entity.sheet.Lighting;
import entity.sheet.VisualEntity;
import graphics.culling.ViewFrustum;

import java.nio.FloatBuffer;

import math.Matrix4f;
import math.Vector3f;

import org.lwjgl.BufferUtils;

import utils.ArrayList;
import utils.Utils;

public class ShadowMapperCube implements ShadowMapping{
	
	private Matrix4f[] view;
	private Matrix4f projection;
	private Matrix4f[] lightTexture;
	private ViewFrustum[] frustum;
	
	private int shadowMap;
	private int shadowMapSize = 1024;
	
	private float zNear = 0.125f;
	private float zFar = 256f;
	
	private boolean enabled = true;
	
	private Lighting parent;
	
	public ShadowMapperCube(){
		view = new Matrix4f[]{new Matrix4f(),new Matrix4f(),new Matrix4f(),
				new Matrix4f(),new Matrix4f(),new Matrix4f()};
		projection = new Matrix4f();
		lightTexture = new Matrix4f[]{new Matrix4f(),new Matrix4f(),new Matrix4f(),
				new Matrix4f(),new Matrix4f(),new Matrix4f()};	
		frustum = new ViewFrustum[]{new ViewFrustum(),new ViewFrustum(),new ViewFrustum(),
			new ViewFrustum(),new ViewFrustum(),new ViewFrustum()};
	}
	
	@Override
	public void setParent(Lighting parent){
		this.parent = parent;
	}
	
	@Override
	public Lighting getParent(){
		return parent;
	}
	
	@Override
	public void init() {
        glLoadIdentity();
        gluPerspective(90, 1, zNear, zFar);
        FloatBuffer temp = BufferUtils.createFloatBuffer(16);
        glGetFloat(GL_MODELVIEW_MATRIX, temp);
        projection.set(temp);
        for(int i=0;i<6;i++){
        	frustum[i].setProjection(90, 1, zNear, zFar);	
        }
        
    	shadowMap = glGenTextures();
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
	}

	@Override
	public void render(Camera camera, ArrayList<VisualEntity> entities, int fbo) {
		Vector3f pos = new Vector3f(parent.getTransform().rendering().origin);//new Vector3f(lightSource.getTransform().rendering().origin);
		
		view[0] = Matrix4f.viewMatrixDirectional(pos, new Vector3f(1,0,0), new Vector3f(0,1,0));
		view[1] = Matrix4f.viewMatrixDirectional(pos, new Vector3f(-1,0,0), new Vector3f(0,1,0));
		view[2] = Matrix4f.viewMatrixDirectional(pos, new Vector3f(0,1,0), new Vector3f(0,0,1));
		view[3] = Matrix4f.viewMatrixDirectional(pos, new Vector3f(0,-1,0), new Vector3f(0,0,-1));
		view[4] = Matrix4f.viewMatrixDirectional(pos, new Vector3f(0,0,1), new Vector3f(0,1,0));
		view[5] = Matrix4f.viewMatrixDirectional(pos, new Vector3f(0,0,-1), new Vector3f(0,1,0));

    	Matrix4f cameraViewInverse = camera.getTransform().rendering().getOpenGLViewMatrix().copy().inv();
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
			
	    	frustum[i].setView(viewRay, upVector, rightVector);
	    	frustum[i].setPos(pos);
    	}

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);
        glDrawBuffers(0); glReadBuffer(GL_NONE);

	
        glViewport(0, 0, shadowMapSize, shadowMapSize);
		
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection.fb());
        
	    for(int i =0;i<6;i++){
	        glMatrixMode(GL_MODELVIEW);
	        glFramebufferTextureLayer(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, shadowMap, 0, i);
	        glClear(GL_DEPTH_BUFFER_BIT);
	        glLoadMatrix(view[i].fb());
	    	
			frustum[i].cullEntities(entities);
	        Graphics3D.renderObjects(frustum[i].getInsideFrustumEntities(), true);
	    }

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0); 
	}

	@Override
	public int getShadowMap() {
		return shadowMap;
	}

	@Override
	public FloatBuffer/*[]*/ getLightTexture() {
		return Utils.combineFloatBuffers(lightTexture);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	@Override
	public void dispose() {
        glDeleteTextures(shadowMap);	
	}

}
