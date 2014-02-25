package game.world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_MAX_RENDERBUFFER_SIZE_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LINEAR_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADRATIC_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLightf;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import game.world.entities.Entity;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;

import shader.GLSLProgram;
import shader.GLSLProgram2;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class HardShadows {
	
	private static GLSLProgram2 shadowMapShader = new GLSLProgram2();
	
    private static int capTextureSize = 1024;
    private static int shadowTextureSize;
    
    private static int FBO;
    private static int shadowMap;
	
	public static void init(){
        if(!GLContext.getCapabilities().GL_ARB_depth_texture || !GLContext.getCapabilities().GL_ARB_shadow){
            System.err.println("I require ARB_depth_texture and ARB_shadow extensionsn.");
            System.exit(0);
        }
        if(!GLContext.getCapabilities().GL_EXT_framebuffer_object){
        	System.err.println("GL_EXT_fragebuffer_object not supperted");
            System.exit(0);
        }
        //Shaders
    	if(!shadowMapShader.load("hard_shadowmap.vs", "hard_shadowmap.fs"))
    		System.exit(0);
        
		// get uniform locations --------------------------------------------------------------------------------------------------
    	shadowMapShader.uniformLocations = new int[2];
    	shadowMapShader.uniformLocations[0] = glGetUniformLocation(shadowMapShader.i(), "Texturing");
    	shadowMapShader.uniformLocations[1] = glGetUniformLocation(shadowMapShader.i(), "ShadowMatrix");
    	
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
        
        shadowMap = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, shadowMap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, shadowTextureSize, shadowTextureSize, 0,GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        // generate FBO -----------------------------------------------------------------------------------------------------------
        FBO = glGenFramebuffersEXT();
        
        //light parameters
        glLight(GL_LIGHT0, GL_AMBIENT, Utils.asFlippedFloatBuffer(0.25f,0.25f,0.25f, 1.0F));
        glLight(GL_LIGHT0, GL_DIFFUSE, Utils.asFlippedFloatBuffer(0.75f,0.75f,0.75f, 1.0F));
        glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0);
        glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0);
	}
	
    public static void render(List<Entity> entities){
    	renderShadowMap(entities);
    	
    	renderShadowedScene(entities);
    }
	
	public static void renderShadowMap(List<Entity> entities){
		// render shadow map ------------------------------------------------------------------------------------------------------
        glViewport(0, 0, shadowTextureSize, shadowTextureSize);
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(Graphics3D.lightProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(Graphics3D.lightViewMatrix);
        
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
        glDrawBuffers(0); glReadBuffer(GL_NONE);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, shadowMap, 0);
 
        glClear(GL_DEPTH_BUFFER_BIT);
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        
        renderObjects(entities, true, false);
        
        glCullFace(GL_BACK);
        
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
	public static void renderShadowedScene(List<Entity> entities){
    	//2nd pass - Draw from camera's point of view
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(Graphics3D.cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(Graphics3D.cameraViewMatrix);

        Vector3f lightPos = Graphics3D.lightPos;
        glLight(GL_LIGHT0, GL_POSITION, lightPos.asFlippedFloatBuffer(1.0f));
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //SHADOW
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
	    shadowMapShader.bind();
        
        Matrix4f lightProjectionMatrixM = new Matrix4f();
        Matrix4f lightViewMatrixM = new Matrix4f();
        lightProjectionMatrixM.set(Graphics3D.lightProjectionMatrix);
        lightViewMatrixM.set(Graphics3D.lightViewMatrix);
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.set(Graphics3D.cameraViewMatrix);

        Matrix4f shadowMatrix = new Matrix4f();
        Matrix4f biasMatrix = Matrix4f.biasMatrix.copy();
        shadowMatrix = biasMatrix.mul(lightProjectionMatrixM).mul(lightViewMatrixM).mul(viewMatrix.getInvert());

        glUniformMatrix4(shadowMapShader.uniformLocations[1], false, shadowMatrix.asFlippedFloatBuffer());
        
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, shadowMap);
        
        //glUniform1i(uniformLocations[0], 1); //only when texutring
        
        renderObjects(entities, true, true);
        
        //glUniform1i(uniformLocations[0], 0); //only when truly texturing
        
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
        
        glDisable(GL_CULL_FACE);
        GLSLProgram.unbind();
        glDisable(GL_DEPTH_TEST);
	}
	
	private static void renderObjects(List<Entity> entities, boolean drawGround, boolean drawTexture) {
    	for(Entity e: entities){
    		if(!drawGround)
    			if(e.isGround())
    				continue;
    		e.drawTexture(drawTexture);
    		if(drawTexture){
    			if(e.getModel() != null)
    				if(e.getModel().isTextured)
    					glUniform1i(shadowMapShader.uniformLocations[0], 1); //only when texutring
    		}
    		e.render();
    		if(drawTexture){
    			if(e.getModel() != null)
    				if(e.getModel().isTextured)
    					glUniform1i(shadowMapShader.uniformLocations[0], 0); //only when truly texturing
    		}
           
    	}
    }
	
    public static void dispose() {
        glDeleteFramebuffersEXT(FBO);
        shadowMapShader.destroy();
        glDeleteTextures(shadowMap);
    }

}
