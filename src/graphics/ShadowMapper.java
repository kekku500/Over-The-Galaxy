package graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
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
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameter;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_COMPARE_R_TO_TEXTURE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14.GL_DEPTH_TEXTURE_MODE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.util.glu.GLU.gluLookAt;
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

public class ShadowMapper implements ShadowMapping{
	
	private Matrix4f view;
	private Matrix4f projection;
	private Matrix4f lightTexture;
	private ViewFrustum frustum;
	
	private int shadowMap;
	private int shadowMapSize = 2048;
	
	private float sceneBoundingRadius = 2000;
	
	private boolean enabled = true;
	
	private Lighting parent;
	
	public ShadowMapper(){
		view = new Matrix4f();
		projection = new Matrix4f();
		lightTexture = new Matrix4f();
		frustum = new ViewFrustum();
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
		shadowMap = glGenTextures();
		
    	glBindTexture(GL_TEXTURE_2D, shadowMap);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    	
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

	@Override
	public void render(Camera camera, ArrayList<VisualEntity> entities, int fbo) {
		//Where shadow frustum looks at
		//shadowMapperNormal.setSceneOrigin(world.getState().getCamera().getPosition(RenderState.rendering()));
		
		Vector3f pos = new Vector3f(parent.getTransform().rendering().origin);
		Vector3f origin = new Vector3f(camera.getTransform().rendering().origin);
    	//Calculate & save matrices
        glPushMatrix();

        //Projection
        Vector3f tempSceneOrigin = origin.copy();
        tempSceneOrigin.negate();
        tempSceneOrigin.add(pos);
        float lightToSceneDistance = tempSceneOrigin.length();
        
        float nearPlane = lightToSceneDistance - sceneBoundingRadius;
        if (nearPlane < 0) {
            System.err.println("Camera is too close to scene. A valid shadow map cannot be generated.");
        }
        float fieldOfView = (float) Math.toDegrees(2.0F * Math.atan(sceneBoundingRadius / lightToSceneDistance));
        glLoadIdentity();
        gluPerspective(fieldOfView, 1.0f, nearPlane, nearPlane + (2.0F * sceneBoundingRadius));
        frustum.setProjection(fieldOfView, 1, nearPlane, nearPlane + (2.0F * sceneBoundingRadius));	
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        glGetFloat(GL_MODELVIEW_MATRIX, fb);
        projection.set(fb);

        //View
        glLoadIdentity();
        gluLookAt(pos.x, pos.y, pos.z,
        		origin.x, origin.y, origin.z,
        0.0f, 1.0f, 0.0f);
        fb = BufferUtils.createFloatBuffer(16);
        glGetFloat(GL_MODELVIEW_MATRIX, fb);
        view.set(fb);
        
        glPopMatrix();
        
        //Shadow view culling
		Matrix4f viewMatrix = view.copy().trans();
		float[] ray = new float[4];
		viewMatrix.getRow(2, ray);
		Vector3f viewRay = new Vector3f(-ray[0], -ray[1], -ray[2]);
		float[] up = new float[4];
		viewMatrix.getRow(1, up);
		Vector3f upVector = new Vector3f(up[0], up[1], up[2]);
		float[] right = new float[4];
		viewMatrix.getRow(0, right);
		Vector3f rightVector = new Vector3f(right[0], right[1],right[2]);
		
    	frustum.setView(viewRay, upVector, rightVector);
    	frustum.setPos(pos);
        
    	Matrix4f cameraViewInverse = camera.getTransform().rendering().getOpenGLViewMatrix().copy().inv();
    	Matrix4f biasMatrix = Matrix4f.BIASMATRIX.copy();
    	lightTexture = cameraViewInverse.mul(view).mul(projection).mul(biasMatrix);
    	
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);
        glDrawBuffers(0); glReadBuffer(GL_NONE);
        
        
		glViewport(0, 0, shadowMapSize, shadowMapSize);
    	
		
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection.fb());
    	
        glMatrixMode(GL_MODELVIEW);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowMap, 0);
        glClear(GL_DEPTH_BUFFER_BIT);
        glLoadMatrix(view.fb());
        
		frustum.cullEntities(entities);
        Graphics3D.renderObjects(frustum.getInsideFrustumEntities(), true);
        
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	@Override
	public int getShadowMap() {
		return shadowMap;
	}

	@Override
	public FloatBuffer getLightTexture() {
		return lightTexture.fb();
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
