package game.world.entities;


import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INTENSITY;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
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
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL14.GL_COMPARE_R_TO_TEXTURE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL14.GL_DEPTH_TEXTURE_MODE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import game.Game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureArray;
import org.lwjgl.opengl.GLContext;

import utils.math.Matrix4f;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class LightSource extends AbstractEntity {
	
	public static Vector4f lightModelAmbient = new Vector4f(0.2f,0.2f,0.2f,1.0f);
	
	//General light
	private Vector4f ambient, diffuse, specular;
	
	//Spotlight
	private float spotCutoff = (float)Math.cos(Math.toRadians(30)); //GL_SPOT_CUTOFF
	private Vector3f spotLightDirection = new Vector3f(-1,-2, -1); //GL_SPOT_DIRECTION 
	private float spotExponent = 0.1f; //GL_SPOT_EXPONENT
	
	private enum LightType{DIRECTIONAL, POINT, SPOT};
	private LightType lightType = LightType.SPOT;
	
	//Point fade
	private float constantAttenuation = 1.0f, linearAttenuation = .001f, quadricAttenuation = .001f;
	
	//Shadow stuff
	private Matrix4f projection = new Matrix4f();
	private Matrix4f[] view;
	private Matrix4f[] lightTexture;
	
	private int shadowMap;
	private int shadowMapSize = 1024;
	
	private Vector3f sceneOrigin = new Vector3f();
	private float sceneBoundingRadius = 75;
	
	private final boolean cube;
	private boolean castShadows = true;
	
	public LightSource(boolean cubeLight){
		cube = cubeLight;
		ambient = new Vector4f(.25f, .25f, .25f, 1.0f);
		diffuse = new Vector4f(1.7f, 1.7f, 1.7f, 1.0f);
		specular = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);
		if(cube){
			view = new Matrix4f[6];
			lightTexture = new Matrix4f[6];
		}else{
			view = new Matrix4f[1];
			view[0] = new Matrix4f();
			lightTexture = new Matrix4f[1];		
		}
		lightType = LightType.POINT;
	}
	
	/**
	 * @return the constantAttenuation
	 */
	public float getConstantAttenuation() {
		return constantAttenuation;
	}

	/**
	 * @param constantAttenuation the constantAttenuation to set
	 */
	public void setConstantAttenuation(float constantAttenuation) {
		this.constantAttenuation = constantAttenuation;
	}

	
	public int getLightType() {
		if(lightType == LightType.DIRECTIONAL)
			return 0;
		else if(lightType == LightType.POINT)
			return 1;
		else //SPOT
			return 2;
	}

	public void setLightType(LightType lightType) {
		this.lightType = lightType;
	}


	public boolean castShadows(){
		return castShadows;
	}
	
	public boolean isCubeLight(){
		return cube;
	}
	
	@Override
	public void dispose(){
        glDeleteTextures(shadowMap);
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
	
	public int getShadowMap(){
		return shadowMap;
	}
	
	public int getShadowMapSize(){
		return shadowMapSize;
	}
	
	public void setProjection(){
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection.asFlippedFloatBuffer());
	}

	
	public float getSpotExponent() {
		return spotExponent;
	}

	public void setSpotExponent(float spotExponent) {
		if(spotExponent >= 0 && spotExponent <= 128)
			this.spotExponent = spotExponent;
		else
			spotExponent = 0;
	}

	
	public void init(){
        if(!GLContext.getCapabilities().GL_EXT_texture_array){
            System.err.println("GL_EXT_texture_array not supported!");
            System.exit(0);
        }
        if(cube){
            glLoadIdentity();
            gluPerspective(90, 1, 0.125f, 512.0f);
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
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
        if(cube)
        	glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_DEPTH_COMPONENT32, shadowMapSize, shadowMapSize, 6, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
        else
        	glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_DEPTH_COMPONENT32, shadowMapSize, shadowMapSize, 1, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
	}
	
	public void update(Matrix4f cameraView){
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
	
	public void renderToTexture(){
		if(shadowMap != 0){
	        glMatrixMode(GL_MODELVIEW);
	        glFramebufferTextureLayer(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, shadowMap, 0, 0);
	        glClear(GL_DEPTH_BUFFER_BIT);
	        glLoadMatrix(view[0].asFlippedFloatBuffer());
		}
	}
	
	public void renderToTexture(int i){
		if(shadowMap != 0){
	        glMatrixMode(GL_MODELVIEW);
	        glFramebufferTextureLayer(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, shadowMap, 0, i);
	        glClear(GL_DEPTH_BUFFER_BIT);
	        glLoadMatrix(view[i].asFlippedFloatBuffer());
		}
	}
	
	public Vector3f getSpotLightDirection() {
		return spotLightDirection;
	}
	
	public void setSpotLightDirection(Vector3f spotLightDirection) {
		this.spotLightDirection = spotLightDirection;
	}

	public float getSpotCutoff() {
		return spotCutoff;
	}

	public void setSpotCutoff(float spotLight) {
		if(spotLight <= 90 && spotLight >= 0)
			this.spotCutoff = spotLight;
		else
			this.spotCutoff = 180;
	}
	
	public Vector4f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector4f ambient) {
		ambient.setPositive();
		this.ambient = ambient;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		ambient.setPositive();
		this.diffuse = diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}

	public void setSpecular(Vector4f specular) {
		ambient.setPositive();
		this.specular = specular;
	}

	public float getLinearAttenuation() {
		return linearAttenuation;
	}

	public void setLinearAttenuation(float linearAttenuation) {
		if(linearAttenuation < 0)
			linearAttenuation = 0;
		this.linearAttenuation = linearAttenuation;
	}

	public float getQuadricAttenuation() {
		return quadricAttenuation;
	}

	public void setQuadricAttenuation(float quadricAttenuation) {
		if(quadricAttenuation < 0)
			quadricAttenuation = 0;
		this.quadricAttenuation = quadricAttenuation;
	}

	@Override
	public Vector3f getPosToMid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lastUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void firstUpdate(float dt) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void startRender() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endRender() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void calcBoundingSphere() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void calcBoundingAxis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Entity copy() {
		// TODO Auto-generated method stub
		return null;
	};

}
