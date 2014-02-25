package game.world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_MAX_RENDERBUFFER_SIZE_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_INTENSITY;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
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
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_COMPARE_R_TO_TEXTURE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14.GL_DEPTH_TEXTURE_MODE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import game.world.entities.Entity;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTTextureArray;
import org.lwjgl.opengl.GLContext;

import shader.GLSLProgram;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class SoftPenumbraShadows {
	
    private static float radius = 0.4f;
    
	private static GLSLProgram shadowMapShader = new GLSLProgram();
	
    private static int capTextureSize = 1024;

    private static int shadowTextureSize;

    private static int FBO;
    private static int shadowMap;
	
    private static FloatBuffer[] lightViewMatrices = {BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16)};
    private static FloatBuffer[] lightTextureMatrices = {BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16)};
    
    public static void init(){
    	//Extensions check
        if(!GLContext.getCapabilities().GL_ARB_depth_texture || !GLContext.getCapabilities().GL_ARB_shadow){
            System.err.println("I require ARB_depth_texture and ARB_shadow extensionsn.");
            System.exit(0);
        }
        if(!GLContext.getCapabilities().GL_EXT_framebuffer_object){
        	System.err.println("GL_EXT_fragebuffer_object not supperted");
            System.exit(0);
        }
	    if(!GLContext.getCapabilities().GL_EXT_texture_array){
	    	System.err.println("GL_EXT_texture_array not supported!");
	            System.exit(0);
	    }
        //Shaders
    	shadowMapShader.load("penumbra_shadow_mapping.vs", "penumbra_shadow_mapping.fs");
    	shadowMapShader.createShader();
		
		// get uniform locations --------------------------------------------------------------------------------------------------
    	shadowMapShader.uniformLocations = new int[4];
    	shadowMapShader.uniformLocations[0] = glGetUniformLocation(shadowMapShader.i(), "LightPosition");
    	shadowMapShader.uniformLocations[1] = glGetUniformLocation(shadowMapShader.i(), "CameraPosition");
    	shadowMapShader.uniformLocations[2] = glGetUniformLocation(shadowMapShader.i(), "LightTextureMatrices");
    	shadowMapShader.uniformLocations[3] = glGetUniformLocation(shadowMapShader.i(), "Texturing");

		
		// set constant uniforms --------------------------------------------------------------------------------------------------
	    shadowMapShader.bind();
	    glUniform1i(glGetUniformLocation(shadowMapShader.i(), "Texture"), 0);
	    glUniform1i(glGetUniformLocation(shadowMapShader.i(), "ShadowMap"), 1);
	    GLSLProgram.unbind();
	    
    	shadowMapShader.validate();

    	//get max texture size
        int maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);
        int maxRenderbufferSize = glGetInteger(GL_MAX_RENDERBUFFER_SIZE_EXT);
        System.out.println("Maximum texture size: " + maxTextureSize);
        System.out.println("Maximum renderbuffer size: " + maxRenderbufferSize);

        if (maxTextureSize > capTextureSize) {
            maxTextureSize = capTextureSize;
            if (maxRenderbufferSize < maxTextureSize) {
                maxTextureSize = maxRenderbufferSize;
            }
        }
        shadowTextureSize = maxTextureSize;  
        
    	// generate shadow map texture --------------------------------------------------------------------------------------------
        shadowMap = glGenTextures();

    	glBindTexture(GL_TEXTURE_2D_ARRAY, shadowMap);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_DEPTH_COMPONENT24, shadowTextureSize, shadowTextureSize, 5, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer) null);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);


        // generate FBO -----------------------------------------------------------------------------------------------------------
        FBO = glGenFramebuffersEXT();
    }
    
    public static void render(List<Entity> entities){
    	renderShadowMap(entities);
    	
    	renderShadowedScene(entities);
    }
    
    private static void renderShadowMap(List<Entity> entities){
    	// calculate light matrices -----------------------------------------------------------------------------------------------
    	lightViewMatrices[0] = Graphics3D.lightViewMatrix;
        Matrix4f biasMatrix = Matrix4f.biasMatrix.copy();
        Matrix4f lightProjectionMatrixM = new Matrix4f().set(Graphics3D.lightProjectionMatrix);
        Matrix4f lightViewMatrixM = new Matrix4f().set(lightViewMatrices[0]);
        
        lightTextureMatrices[0] = biasMatrix.mul(lightProjectionMatrixM).mul(lightViewMatrixM).asFlippedFloatBuffer();
    	
        Vector3f lp = new Vector3f();
        lp.add(new Vector3f(lightViewMatrices[0].get(0), lightViewMatrices[0].get(4),lightViewMatrices[0].get(8)));
        lp.add(new Vector3f(lightViewMatrices[0].get(1), lightViewMatrices[0].get(5),lightViewMatrices[0].get(9)));
    	lp.normalize();
    	lp.scale(radius);
    	Vector3f lightPos = Graphics3D.lightPos;
    	lp.add(lightPos);
    	Vector3f sceneOrigin = new Vector3f();
        for(int i = 1; i < 5; i++){
        	glLoadIdentity();
        	gluLookAt(lp.x, lp.y, lp.z,sceneOrigin.x, sceneOrigin.y, sceneOrigin.z,0.0f, 1.0f, 0.0f);
            glGetFloat(GL_MODELVIEW_MATRIX, lightViewMatrices[i]);
        	
            biasMatrix = Matrix4f.biasMatrix.copy();
            lightViewMatrixM.set(lightViewMatrices[i]);
            
        	lightTextureMatrices[i] = biasMatrix.mul(lightProjectionMatrixM).mul(lightViewMatrixM).asFlippedFloatBuffer();
        	
        	lp = Utils.rotate(lp, 90.0f, new Vector3f(lightViewMatrices[0].get(2),lightViewMatrices[0].get(6),lightViewMatrices[0].get(10))); 

        }
        // render shadow map ------------------------------------------------------------------------------------------------------
        glViewport(0, 0, shadowTextureSize, shadowTextureSize);
        for(int i=0;i<5;i++){
        	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
        	glDrawBuffers(0); glReadBuffer(GL_NONE);
            EXTTextureArray.glFramebufferTextureLayerEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, shadowMap, 0, i);
        	
            glClear(GL_DEPTH_BUFFER_BIT);
            
            glMatrixMode(GL_PROJECTION);
            glLoadMatrix(Graphics3D.lightProjectionMatrix);

            glMatrixMode(GL_MODELVIEW);
            glLoadMatrix(lightViewMatrices[i]);
            
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_FRONT);
            
            renderObjects(entities, true, false);
            
            glCullFace(GL_BACK);
            
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		}   
    }
    
    private static void renderShadowedScene(List<Entity> entities){
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(Graphics3D.cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(Graphics3D.cameraViewMatrix);
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        
        glActiveTexture(GL_TEXTURE1);  glBindTexture(GL_TEXTURE_2D_ARRAY, shadowMap);
        
        glActiveTexture(GL_TEXTURE0);
        
	    shadowMapShader.bind();
        Vector3f lightPos = Graphics3D.lightPos;
        glUniform3f(shadowMapShader.uniformLocations[0], lightPos.x, lightPos.y, lightPos.z);
        glUniform3f(shadowMapShader.uniformLocations[1], Graphics3D.camera.getPos().x, Graphics3D.camera.getPos().y, Graphics3D.camera.getPos().z);
        //combine floatbuffers
        FloatBuffer fb = BufferUtils.createFloatBuffer(16*5);
        for(int i=0;i<5;i++){
        	FloatBuffer add = lightTextureMatrices[i];
        	fb.put(add);
        }
    	fb.flip();

        glUniformMatrix4(shadowMapShader.uniformLocations[2], false, fb);
        renderObjects(entities, true, true);
        
        GLSLProgram.unbind();
        
        glActiveTexture(GL_TEXTURE1);  glBindTexture(GL_TEXTURE_2D_ARRAY, 0);

        glActiveTexture(GL_TEXTURE0);
        
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
    }
    
	private static void renderObjects(List<Entity> entities, boolean drawGround, boolean drawTexture) {
    	for(Entity e: entities){
    		if(!drawGround)
    			if(e.isGround())
    				continue;
    		if(e.getModel() != null)
	    		if(e.getModel().isTextured){
	    			glUniform1i(shadowMapShader.uniformLocations[3], 1);
	    		}else{
	    			glUniform1i(shadowMapShader.uniformLocations[3], 0);
	    		}
    		e.drawTexture(drawTexture);
    		e.render();   
    	}
    }
	
    public static void dispose() {
        glDeleteFramebuffersEXT(FBO);
        shadowMapShader.destroy();
        glDeleteTextures(shadowMap);
    }

}
