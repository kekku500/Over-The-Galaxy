package game.world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_MAX_RENDERBUFFER_SIZE_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_COMPARE_R_TO_TEXTURE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL14.GL_DEPTH_TEXTURE_MODE;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTextureLayer;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import game.world.entities.Entity;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;

import shader.GLSLProgram;
import shader.GLSLProgram2;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class CubeShadows {
	
	private static GLSLProgram2 shadowMapShader = new GLSLProgram2();
	
    private static int capTextureSize = 2048;

    private static int shadowTextureSize;

    private static int FBO;
    private static int shadowCubeMap;
	
    private static FloatBuffer[] lightView = {BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16),
		  BufferUtils.createFloatBuffer(16)};
    private static FloatBuffer[] lightTexture = {BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16),
			BufferUtils.createFloatBuffer(16)};
    
    public static FloatBuffer lightProjection  = BufferUtils.createFloatBuffer(16);
    
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
	    if(!GLContext.getCapabilities().OpenGL21){
	    	System.err.println("OpenGL 2.1 not supported!");
	            System.exit(0);
	    }
        //Shaders
    	if(!shadowMapShader.load("cubeshadow.vs", "cubeshadow.fs"))
    		System.exit(0);
		
		// get uniform locations --------------------------------------------------------------------------------------------------

    	shadowMapShader.uniformLocations = new int[3];
    	shadowMapShader.uniformLocations[0] = glGetUniformLocation(shadowMapShader.i(), "Model");
    	shadowMapShader.uniformLocations[1] = glGetUniformLocation(shadowMapShader.i(), "Texturing");
        shadowMapShader.uniformLocations[2] = glGetUniformLocation(shadowMapShader.i(), "LightTexture");
		
		// set constant uniforms --------------------------------------------------------------------------------------------------
	    shadowMapShader.bind();
	    glUniform1i(glGetUniformLocation(shadowMapShader.i(), "Texture"), 0);
	    glUniform1i(glGetUniformLocation(shadowMapShader.i(), "ShadowCubeMap"), 1);
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
        shadowCubeMap = glGenTextures();

    	glBindTexture(GL_TEXTURE_2D_ARRAY, shadowCubeMap);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_DEPTH_COMPONENT32, shadowTextureSize, shadowTextureSize, 6, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer) null);
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);

        
        // generate FBO -----------------------------------------------------------------------------------------------------------
        FBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glDrawBuffers(0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        glLoadIdentity();
        gluPerspective(90, 1, 0.125f, 512.0f);
        glGetFloat(GL_MODELVIEW_MATRIX, lightProjection);
        
        Vector3f lightColor = new Vector3f(1.0f, 1.0f, 1.0f);
        glLight(GL_LIGHT0, GL_AMBIENT, lightColor.mul(0.20f).asFlippedFloatBuffer(1.0f));
        glLight(GL_LIGHT0, GL_DIFFUSE, lightColor.mul(5f).asFlippedFloatBuffer(1.0f));
        glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, .01f / 128.0f);
        glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, .01f / 256.0f);
        
        glPolygonOffset(.96f, 4.0F);
    }
    
    public static void render(List<Entity> entities){
    	renderShadowCubeMap(entities);
    	
    	renderShadowedScene(entities);
    	
    }
    
    private static void renderShadowCubeMap(List<Entity> entities){
    	// calculate light matrices -----------------------------------------------------------------------------------------------
    	lightView[0] = Matrix4f.viewMatrix(new Vector3f( 0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), Graphics3D.lightPos).asFlippedFloatBuffer();
    	lightView[1] = Matrix4f.viewMatrix(new Vector3f( 0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), Graphics3D.lightPos).asFlippedFloatBuffer();
    	lightView[2] = Matrix4f.viewMatrix(new Vector3f( 1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, -1.0f, 0.0f), Graphics3D.lightPos).asFlippedFloatBuffer();
    	lightView[3] = Matrix4f.viewMatrix(new Vector3f( 1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f), new Vector3f(0.0f, 1.0f, 0.0f), Graphics3D.lightPos).asFlippedFloatBuffer();
    	lightView[4] = Matrix4f.viewMatrix(new Vector3f( -1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f), Graphics3D.lightPos).asFlippedFloatBuffer();
    	lightView[5] = Matrix4f.viewMatrix(new Vector3f( 1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), Graphics3D.lightPos).asFlippedFloatBuffer();
    	 
    	Matrix4f biasMatrix = Matrix4f.biasMatrix.copy();
    	Matrix4f lightProjectionMatrix = new Matrix4f(lightProjection);
    	lightTexture[0] = biasMatrix.mul(lightProjectionMatrix).mul(new Matrix4f(lightView[0])).asFlippedFloatBuffer();
    	biasMatrix = Matrix4f.biasMatrix.copy();
    	lightTexture[1] = biasMatrix.mul(lightProjectionMatrix).mul(new Matrix4f(lightView[1])).asFlippedFloatBuffer();
    	biasMatrix = Matrix4f.biasMatrix.copy();
    	lightTexture[2] = biasMatrix.mul(lightProjectionMatrix).mul(new Matrix4f(lightView[2])).asFlippedFloatBuffer();
    	biasMatrix = Matrix4f.biasMatrix.copy();
    	lightTexture[3] = biasMatrix.mul(lightProjectionMatrix).mul(new Matrix4f(lightView[3])).asFlippedFloatBuffer();
    	biasMatrix = Matrix4f.biasMatrix.copy();
    	lightTexture[4] = biasMatrix.mul(lightProjectionMatrix).mul(new Matrix4f(lightView[4])).asFlippedFloatBuffer();
    	biasMatrix = Matrix4f.biasMatrix.copy();
    	lightTexture[5] = biasMatrix.mul(lightProjectionMatrix).mul(new Matrix4f(lightView[5])).asFlippedFloatBuffer();
    	
    	shadowMapShader.bind();
        glUniformMatrix4(shadowMapShader.uniformLocations[2], false, Utils.combineFloatBuffers(lightTexture));
    	GLSLProgram.unbind();
    	
    	// render shadow cube map -------------------------------------------------------------------------------------------------
    	glViewport(0, 0, shadowTextureSize, shadowTextureSize);
    	
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(lightProjection);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        glCullFace(GL_FRONT);
        glBindFramebuffer(GL_FRAMEBUFFER, FBO);
        glEnable(GL_POLYGON_OFFSET_FILL);
        
        for(int i =0;i<6;i++){
        	glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowCubeMap, 0, i);
        	glClear(GL_DEPTH_BUFFER_BIT);
            glMatrixMode(GL_MODELVIEW);
            glLoadMatrix(lightView[i]);
        	renderObjects(entities, true, false);
        }
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        glDisable(GL_POLYGON_OFFSET_FILL);
        
        glCullFace(GL_BACK);

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
    }
    
    private static void renderShadowedScene(List<Entity> entities){
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(Graphics3D.cameraProjectionMatrix);
        //glLoadMatrix(lightProjection);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
     
        Vector3f lightPos = Graphics3D.lightPos;
        glLight(GL_LIGHT0, GL_POSITION, lightPos.asFlippedFloatBuffer(1.0f));
        
        glLoadMatrix(Graphics3D.cameraViewMatrix);
        //glLoadMatrix(lightView[5]);
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
        glActiveTexture(GL_TEXTURE1);  glBindTexture(GL_TEXTURE_2D_ARRAY, shadowCubeMap);
        
        glActiveTexture(GL_TEXTURE0);

	    shadowMapShader.bind();
	    
    	for(Entity e: entities){
    		e.drawTexture(true);
    		float[] n = new float[16];
    		e.getMotionState().getOpenGLMatrix(n);
    		Matrix4f m = new Matrix4f(n);
    		if(e.getModel() != null){
    			if(e.getModel().offset != null){
    				Matrix4f offset = new Matrix4f();
    				e.getModel().offset.getMatrix(offset);
    				m.mul(offset, m);
    			}
    		}
            glUniformMatrix4(shadowMapShader.uniformLocations[0], false, m.asFlippedFloatBuffer());
    		if(e.getModel() != null)
	    		if(e.getModel().isTextured){
	    			glUniform1i(shadowMapShader.uniformLocations[1], 1);
	    		}else{
	    			glUniform1i(shadowMapShader.uniformLocations[1], 0);
	    		}
    		e.render(false); 
    	}
    	
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
    		e.drawTexture(drawTexture);
    		e.render(true);   
    	}
    }
	
    public static void dispose() {
        glDeleteFramebuffersEXT(FBO);
        shadowMapShader.destroy();
        glDeleteTextures(shadowCubeMap);
    }

}
