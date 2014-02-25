package game.world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import game.Game;
import game.world.entities.Entity;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import blender.model.Material;
import blender.model.Model;
import blender.model.SubModel;
import blender.model.custom.Cuboid;
import blender.model.custom.Sphere;
import shader.GLSLProgram;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Vector3f;

public class DeferredLightPoints {
	
	private static boolean showLights = false;
	
	public static GLSLProgram coloring = new GLSLProgram();
	public static GLSLProgram texturing = new GLSLProgram();
	private static GLSLProgram deferredLighting = new GLSLProgram();
	private static GLSLProgram blurH = new GLSLProgram();
	private static GLSLProgram blurV = new GLSLProgram();
    
    private static int colorBuffer, normalBuffer, depthBuffer;
    private static IntBuffer lightsTexture;
    private static int FBO;
    
    private static Sphere lightBall = new Sphere(1, 30, 30);
    
    //lights
    private static Vector3f[] lightColor = new Vector3f[4];
    private static Vector3f[] lightPosition = new Vector3f[4];
    
    private static IntBuffer buffers;
    
    private static FloatBuffer projectionBiasInverse = BufferUtils.createFloatBuffer(16);
    
    private static int shadowMap;
	
	public static void init(){
		boolean error = false;
        if(!GLContext.getCapabilities().OpenGL21){
            System.err.println("OpenGL 2.1 not supported!");
            error = true;
        }
        if(!GLContext.getCapabilities().GL_ARB_texture_non_power_of_two){
        	System.err.println("GL_ARB_texture_non_power_of_two not supported!");
            error = true;
        }
        if(!GLContext.getCapabilities().GL_ARB_depth_texture){
            System.err.println("GL_ARB_depth_texture not supported!");
            error = true;
        }
        if(!GLContext.getCapabilities().GL_EXT_framebuffer_object){
        	System.err.println("GL_EXT_fragebuffer_object not supperted");
            error = true;
        }
        //Load Shaders
    	error = !coloring.load("coloring.vs", "coloring.fs");
    	error = !texturing.load("texturing.vs", "texturing.fs");
    	error = !deferredLighting.load("deferredlighting.vs", "deferredlighting.fs");
    	error = !blurH.load("blur.vs", "blurh.fs");
    	error = !blurV.load("blur.vs", "blurv.fs");
    	
    	if(error)
            System.exit(0);
    	
    	
		// get uniform locations --------------------------------------------------------------------------------------------------
    	deferredLighting.uniformLocations = new int[1];
    	deferredLighting.uniformLocations[0] = glGetUniformLocation(deferredLighting.i(), "ProjectionBiasInverse");
    	
    	blurH.uniformLocations = new int[1];
    	blurH.uniformLocations[0] = glGetUniformLocation(blurH.i(), "odw");
    	
    	blurV.uniformLocations = new int[1];
    	blurV.uniformLocations[0] = glGetUniformLocation(blurH.i(), "odh");
    	
    	// set constant uniforms --------------------------------------------------------------------------------------------------
    	deferredLighting.bind();
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "ColorBuffer"), 0);
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "NormalBuffer"), 1);
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "DepthBuffer"), 2);
        GLSLProgram.unbind();   
        
        int width = 3;
        
    	blurH.bind();
        glUniform1i(glGetUniformLocation(blurH.i(), "Width"), width);
    	blurV.bind();
        glUniform1i(glGetUniformLocation(blurV.i(), "Width"), width);
        GLSLProgram.unbind();   
    	
        coloring.validate();
        texturing.validate();
        deferredLighting.validate();
        blurH.validate();
        blurV.validate();
        
        //Other textures
        colorBuffer = glGenTextures();
        normalBuffer = glGenTextures();
        depthBuffer = glGenTextures();
        
        lightsTexture = BufferUtils.createIntBuffer(2);
        glGenTextures(lightsTexture);
        
        FBO = glGenFramebuffers();
        
        
        lightColor[0] = new Vector3f(1.0f, 0.0f, 0.0f);
        lightPosition[0] = new Vector3f(80.0f, 30f, 80f);
        lightColor[1] = new Vector3f(0.0f, 1.0f, 0.0f);
        lightPosition[1] = Utils.rotate(lightPosition[0], 120.0f, new Vector3f(0.0f, 1.0f, 0.0f));
        lightColor[2] = new Vector3f(0.0f, 0.0f, 1.0f);
        lightPosition[2] = Utils.rotate(lightPosition[1], 120.0f, new Vector3f(0.0f, 1.0f, 0.0f));
        lightColor[3] = new Vector3f(1.0f, 1.0f, 1.0f);
        lightPosition[3] = new Vector3f(0, 30, 0);

        for(int i = 0; i < 3; i++){
            glLight(GL_LIGHT0 + i, GL_AMBIENT, lightColor[i].mul(1f).asFlippedFloatBuffer(1.0f));
            glLight(GL_LIGHT0 + i, GL_DIFFUSE, lightColor[i].mul(30f).asFlippedFloatBuffer(1.0f));
            glLightf(GL_LIGHT0 + i, GL_LINEAR_ATTENUATION, .01f);
            glLightf(GL_LIGHT0 + i, GL_QUADRATIC_ATTENUATION, .01f);
        }

        glLight(GL_LIGHT3, GL_AMBIENT, lightColor[3].mul(1).asFlippedFloatBuffer(1.0f));
        glLight(GL_LIGHT3, GL_DIFFUSE, lightColor[3].mul(10).asFlippedFloatBuffer(1.0f));
        glLightf(GL_LIGHT3, GL_LINEAR_ATTENUATION, 0.01f);
        glLightf(GL_LIGHT3, GL_QUADRATIC_ATTENUATION, .01f);
        
	}
	
	private static void resize(){
		int width = Display.getWidth();
		int height = Display.getHeight();
	    glViewport(0, 0, width, height);

	    
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(Graphics3D.cameraProjectionMatrix);

        Matrix4f camInverse = new Matrix4f(Graphics3D.cameraProjectionMatrix);
        camInverse.invert();
        camInverse.mul(Matrix4f.biasMatrixInverse);

        projectionBiasInverse = camInverse.asFlippedFloatBuffer();

        
	    glBindTexture(GL_TEXTURE_2D, colorBuffer);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
	    glBindTexture(GL_TEXTURE_2D, 0);

	    glBindTexture(GL_TEXTURE_2D, normalBuffer);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
	    glBindTexture(GL_TEXTURE_2D, 0);

	    glBindTexture(GL_TEXTURE_2D, depthBuffer);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
	    glBindTexture(GL_TEXTURE_2D, 0);

	    for(int i = 0; i < 2; i++)
	    {
	        glBindTexture(GL_TEXTURE_2D, lightsTexture.get(0));
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (FloatBuffer)null);
	        glBindTexture(GL_TEXTURE_2D, 0);
	    }

	    deferredLighting.bind();
	    glUniformMatrix4(deferredLighting.uniformLocations[0], false, projectionBiasInverse);
	    GLSLProgram.unbind();

	    blurH.bind();
	    glUniform1f(blurH.uniformLocations[0], 1.0f / (float)width);
	    blurV.bind();
	    glUniform1f(blurV.uniformLocations[0], 1.0f / (float)height);
	    GLSLProgram.unbind();
	}
	
	private static boolean firstRun = true;
    public static void render(List<Entity> entities){
    	if(firstRun){
            resize();
            firstRun = false;
    	}
    	sceneToTextures(entities);
    	
    	drawDeferredTextures(entities);
    }
	
	public static void sceneToTextures(List<Entity> entities){
		buffers = BufferUtils.createIntBuffer(2);
		buffers.put(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1});
		buffers.rewind();
		// 1st pass - render scene to textures ------------------------------------------------------------------------------------
		glBindFramebuffer(GL_FRAMEBUFFER, FBO);
		glDrawBuffers(buffers);
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorBuffer, 0);
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normalBuffer, 0);
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);
	    
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    
        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(Graphics3D.cameraViewMatrix);
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        for(Entity e: entities){
        	Model m = e.getModel();
        	if(m != null){
	    		glPushMatrix(); //save current transformations
	    		
				float[] f = new float[16];
				e.getMotionState().getOpenGLMatrix(f);
				FloatBuffer fb = BufferUtils.createFloatBuffer(16);
				fb.put(f);
				fb.rewind();
				glMultMatrix(fb);
        	
        		if(m.offset != null){
        			f = new float[16];
        			m.offset.getOpenGLMatrix(f);
        			fb = BufferUtils.createFloatBuffer(16);
        			fb.put(f);
        			fb.rewind();
        			glMultMatrix(fb);
        		}
        		//Texturing and coloring
        		for(SubModel sM : m.submodels){
        			sM.material.apply();
           	        if (sM.isTextured){
        	            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        	            glBindBuffer(GL_ARRAY_BUFFER, sM.vboTexVertexID);
        	            glTexCoordPointer(2, GL_FLOAT, 0, 0);
        	        }else{
        	        	glEnableClientState(GL_COLOR_ARRAY);
            	        glBindBuffer(GL_ARRAY_BUFFER, sM.vboColorID);
            	        glColorPointer(3, GL_FLOAT, 0, 0);
        	        }

        		    glEnableClientState(GL_NORMAL_ARRAY);
        	        glBindBuffer(GL_ARRAY_BUFFER, sM.vboNormalID);
        	        glNormalPointer(GL_FLOAT, 0, 0);

        		    glEnableClientState(GL_VERTEX_ARRAY);
        	        glBindBuffer(GL_ARRAY_BUFFER, sM.vboVertexID);
        	        glVertexPointer(3, GL_FLOAT, 0, 0);
        	        
        	        if(sM.isTextured){
        	        	texturing.bind();
        	        	glBindTexture(GL_TEXTURE_2D, sM.material.textureHandle);
        	        }else{
         	        	coloring.bind();
        	        }

        	        glDrawArrays(GL_TRIANGLES, 0, 9 * sM.faces.size());  

        	        if(sM.isTextured){
        	        	glBindTexture(GL_TEXTURE_2D, 0);
        	        }
        	        GLSLProgram.unbind();
        	        
        	        glBindBuffer(GL_ARRAY_BUFFER, 0);
        	        glDisableClientState(GL_VERTEX_ARRAY);
        	        glDisableClientState(GL_NORMAL_ARRAY);
        	        if(sM.isTextured){
        	            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        	        }else{
        	        	glDisableClientState(GL_COLOR_ARRAY);
        	        }
        	        Material.clear();
        		}
        	}
        	glPopMatrix(); //reset transformations
        }

        
	    glDisable(GL_CULL_FACE);
	    glDisable(GL_DEPTH_TEST);

	    glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	
	public static void drawDeferredTextures(List<Entity> entities){
		// 2nd pass - calculate lighting - render fullscreen quad with deferred lighting shader applied ---------------------------
        //glMatrixMode(GL_PROJECTION);
        //glLoadMatrix(Graphics3D.cameraProjectionMatrix);
		
		glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(Graphics3D.cameraViewMatrix);
        
        for(int i = 0; i < 4; i++){
            glLight(GL_LIGHT0 + i, GL_POSITION, lightPosition[i].asFlippedFloatBuffer(1.0f));
        }
        
        glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, normalBuffer);
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, depthBuffer);
        deferredLighting.bind();
        glBegin(GL_QUADS);
            glVertex2f(0.0f, 0.0f);
            glVertex2f(1.0f, 0.0f);
            glVertex2f(1.0f, 1.0f);
            glVertex2f(0.0f, 1.0f);
        glEnd();
        GLSLProgram.unbind();
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
        
        if(showLights){
            // render lights cubes

            /*glBindFramebuffer(GL_FRAMEBUFFER, FBO);
            glDrawBuffers(buffers);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, lightsTexture.get(0), 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);

            glClear(GL_COLOR_BUFFER_BIT);

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);

            for(int i = 0; i < 4; i++){
        		glMatrixMode(GL_MODELVIEW);
                glLoadMatrix(Graphics3D.cameraViewMatrix);
                glMultMatrix(Matrix4f.translationMatrix(lightPosition[i]).asFlippedFloatBuffer());
                glColor3f(lightColor[i].x, lightColor[i].y, lightColor[i].z);
                lightBall.render();
            }

            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);

            glBindFramebuffer(GL_FRAMEBUFFER, 0);*/

            // blur horizontally

            /*glBindFramebuffer(GL_FRAMEBUFFER, FBO);
            glDrawBuffers(GL_COLOR_ATTACHMENT0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, lightsTexture.get(1), 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, 0, 0);

            glBindTexture(GL_TEXTURE_2D, lightsTexture.get(0));
            blurH.bind();

            glBegin(GL_QUADS);
                glVertex2f(0.0f, 0.0f);
                glVertex2f(1.0f, 0.0f);
                glVertex2f(1.0f, 1.0f);
                glVertex2f(0.0f, 1.0f);
            glEnd();
            GLSLProgram.unbind();
            glBindTexture(GL_TEXTURE_2D, 0);

            glBindFramebuffer(GL_FRAMEBUFFER, 0);

            // blur vertically and blend over the screen

            glBindTexture(GL_TEXTURE_2D, lightsTexture.get(1));
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
            blurV.bind();
            glBegin(GL_QUADS);
                glVertex2f(0.0f, 0.0f);
                glVertex2f(1.0f, 0.0f);
                glVertex2f(1.0f, 1.0f);
                glVertex2f(0.0f, 1.0f);
            glEnd();
            GLSLProgram.unbind();
            glDisable(GL_BLEND);
            glBindTexture(GL_TEXTURE_2D, 0);*/
        }
	}
	
    public static void dispose() {
    	coloring.destroy();
    	texturing.destroy();
    	deferredLighting.destroy();
    	blurH.destroy();
    	blurV.destroy();
        glDeleteTextures(colorBuffer);
        glDeleteTextures(normalBuffer);
        glDeleteTextures(depthBuffer);
        glDeleteTextures(lightsTexture);
        glDeleteFramebuffers(FBO);
        glDeleteTextures(shadowMap);
    }

}
