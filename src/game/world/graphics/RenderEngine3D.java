package game.world.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import game.Game;
import game.world.World;
import game.world.entities.Entity;
import game.world.entities.LightSource;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Set;

import main.PlayState;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import controller.Camera;
import blender.model.Material;
import blender.model.Model;
import blender.model.SubModel;
import blender.model.Texture;
import blender.model.custom.Cuboid;
import shader.Shader;
import shader.Shader;
import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Vector2f;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class RenderEngine3D {
	

	//Store materials, bind textures, normal mapping
    public Shader preprocess = new Shader();
    
    //Lighting and shadows
    private Shader lightingAndShadow = new Shader(); 
    
    //Texture indicies
    private int colorBuffer, normalBuffer, depthBuffer; //for deferred shading
    
    //Rendering settings
    public boolean renderGrid = false;
    
	//Stores rendering width and height
    private int width, height;

    private FloatBuffer projectionBiasInverse = BufferUtils.createFloatBuffer(16);
    private Matrix4f viewInverse = new Matrix4f();
    
    private int FBO;


    //View, Projections
    public Camera camera = new Camera();
    public FloatBuffer cameraProjectionMatrix = BufferUtils.createFloatBuffer(16);
    public FloatBuffer cameraViewMatrix = BufferUtils.createFloatBuffer(16);
    public Set<LightSource> lightSources;
    
    private Camera cam = null;
	public void updateMatrices(Camera cam){
		this.cam = cam;
		//camera = cam;
        cameraProjectionMatrix.rewind();
        cameraViewMatrix.rewind();
        
    	//Calculate & save matrices
        glPushMatrix();

        //Camera
        glLoadIdentity();
        gluPerspective(Game.fov, (float) Game.width / (float) Game.height, Game.zNear, Game.zFar);
        glGetFloat(GL_MODELVIEW_MATRIX, cameraProjectionMatrix);
        
        if(cam != null){
            glLoadIdentity();
            cam.lookAt();
            glGetFloat(GL_MODELVIEW_MATRIX, cameraViewMatrix);
        }

        glPopMatrix();
	}
	
	public void init(){
		// check OpenGL version ---------------------------------------------------------------------------------------------------
        if(!GLContext.getCapabilities().OpenGL21){
            System.err.println("OpenGL 2.1 not supported!");
            System.exit(0);
        }
        boolean error = false;
        // check OpenGL extensions ------------------------------------------------------------------------------------------------
        if(!GLContext.getCapabilities().GL_ARB_texture_non_power_of_two){
        	System.err.println("GL_ARB_texture_non_power_of_two not supported!");
            error = true;
        }
        if(!GLContext.getCapabilities().GL_ARB_depth_texture){
            System.err.println("GL_ARB_depth_texture not supported!");
            error = true;
        }
        if(!GLContext.getCapabilities().GL_EXT_texture_array){
            System.err.println("GL_EXT_texture_array not supported!");
            error = true;
        }
        if(!GLContext.getCapabilities().GL_EXT_framebuffer_object){
        	System.err.println("GL_EXT_fragebuffer_object not supperted");
            error = true;
        }
        
        
        // load shaders -----------------------------------------------------------------------------------------------------------
    	error = !preprocess.load("renderengine//preprocess.vs", "renderengine//preprocess.fs");
    	error = !lightingAndShadow.load("renderengine//deferredlighting.vs", "renderengine//deferredlighting.fs");
    	
    	if(error){
    		System.err.println("Error occoured!");
            System.exit(0);
    	}
    	
		// Uniform stuff --------------------------------------------------------------------------------------------------
    	preprocess.attribLocations = new int[1];
        preprocess.attribLocations[0] = glGetAttribLocation(preprocess.i(), "vert_Tangent");
        
    	preprocess.uniformLocations = new int[3];
        preprocess.uniformLocations[0] = glGetUniformLocation(preprocess.i(), "Texturing");
        preprocess.uniformLocations[1] = glGetUniformLocation(preprocess.i(), "NormalMapping");
        preprocess.uniformLocations[2] = glGetUniformLocation(preprocess.i(), "GodRays");
        
        lightingAndShadow.bind();
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "ColorBuffer"), 0);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "NormalBuffer"), 1);

        lightingAndShadow.uniformLocations = new int[23];
        lightingAndShadow.uniformLocations[0] = glGetUniformLocation(lightingAndShadow.i(), "ProjectionBiasInverse");

        lightingAndShadow.uniformLocations[2] = glGetUniformLocation(lightingAndShadow.i(), "ViewInverse");
        lightingAndShadow.uniformLocations[3] = glGetUniformLocation(lightingAndShadow.i(), "LightTexture");
        lightingAndShadow.uniformLocations[4] = glGetUniformLocation(lightingAndShadow.i(), "Shadows");
        lightingAndShadow.uniformLocations[5] = glGetUniformLocation(lightingAndShadow.i(), "Filtering");
        lightingAndShadow.uniformLocations[6] = glGetUniformLocation(lightingAndShadow.i(), "Occlusion");

        lightingAndShadow.uniformLocations[8] = glGetUniformLocation(lightingAndShadow.i(), "CubeLight");
        
        //Light describing
        lightingAndShadow.uniformLocations[1] = glGetUniformLocation(lightingAndShadow.i(), "LightSourcePosition");
        lightingAndShadow.uniformLocations[7] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceNormal");
        
        lightingAndShadow.uniformLocations[9] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceAmbient");
        lightingAndShadow.uniformLocations[10] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceDiffuse");
        lightingAndShadow.uniformLocations[11] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceSpecular");
        lightingAndShadow.uniformLocations[12] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceConstantAttenuation");
        lightingAndShadow.uniformLocations[13] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceLinearAttenuation");
        lightingAndShadow.uniformLocations[14] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceQuadricAttenuation");
        lightingAndShadow.uniformLocations[15] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceSpotCutoff");
        lightingAndShadow.uniformLocations[16] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceSpotLightDirection");
        lightingAndShadow.uniformLocations[17] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceSpotExponent");
        lightingAndShadow.uniformLocations[18] = glGetUniformLocation(lightingAndShadow.i(), "LightSourceType");
        
        lightingAndShadow.uniformLocations[19] = glGetUniformLocation(lightingAndShadow.i(), "NormalMatrix");
        lightingAndShadow.uniformLocations[20] = glGetUniformLocation(lightingAndShadow.i(), "ModelViewMatrix");
        lightingAndShadow.uniformLocations[21] = glGetUniformLocation(lightingAndShadow.i(), "ProjectionMatrix");
        lightingAndShadow.uniformLocations[22] = glGetUniformLocation(lightingAndShadow.i(), "sxy");
        
        // set texture indices in shaders -----------------------------------------------------------------------------------------
        
    	preprocess.bind();
    	glUniform1i(glGetUniformLocation(preprocess.i(), "Texture"), 0);
    	glUniform1i(glGetUniformLocation(preprocess.i(), "NormalMap"), 1);
    	glUseProgram(0);

        lightingAndShadow.bind();
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "ColorBuffer"), 0);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "NormalBuffer"), 1);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "DepthBuffer"), 2);
        Shader.unbind();   
        
    	preprocess.validate();
    	lightingAndShadow.validate();
        
        // generate framebuffer textures ------------------------------------------------------------------------------------------
        colorBuffer = glGenTextures();
        normalBuffer = glGenTextures();
        depthBuffer = glGenTextures();

        // generate framebuffer object --------------------------------------------------------------------------------------------
        FBO = glGenFramebuffersEXT();
	}
	
	public void checkInput(int i){
		switch(i){
		case Keyboard.KEY_F1:
			renderGrid = !renderGrid; 
			System.out.println("Changed renderGrid to " + renderGrid);
			break;
		}
	}
	

	
	boolean firstRun = true;
    public void render(List<Entity> entities, Camera cam, World world){
    	lightSources = world.lightSources;
    	if(cam == null) //wait for camera to be ready
    		return;
    	if(firstRun){
    		resize();
    		firstRun = false;
    	}
    	
    	IntBuffer buffers12 = BufferUtils.createIntBuffer(8);
		buffers12.put(GL_COLOR_ATTACHMENT0).put(GL_COLOR_ATTACHMENT1);
		buffers12.rewind();
    	
	    glMatrixMode(GL_PROJECTION);
	    glLoadMatrix(cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(cameraViewMatrix);
        
        glViewport(0, 0, width, height);
        // 1st pass - render scene to textures ------------------------------------------------------------------------------------
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
        glDrawBuffers(buffers12);glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorBuffer, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normalBuffer, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);
        
        //glClearColor(0.0f, 0.1f, 0.1f, 1.0f);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    	preprocess.bind();

        renderObjects(entities, false);
        
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, 0, 0);
        Shader.unbind();

	    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

	    // 2nd pass lighting
	    deferredLightingStuff();
    }
    
    //public void 
    
    public void cameraViewPerspective(){
	    glMatrixMode(GL_PROJECTION);
	    glLoadMatrix(cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(cameraViewMatrix);
    }
        
    

    public void deferredLightingStuff(){
    	//calculate matrices required for lighting
    	Matrix4f modelMatrix = new Matrix4f();
    	modelMatrix.setIdentity();
    	modelMatrix.set(15, 0);
    	Matrix4f modelViewMatrix = new Matrix4f(cameraViewMatrix).mul(modelMatrix);
    	Matrix3f normalMatrix = new Matrix3f(modelViewMatrix).getInvert().getTranspose();
    	//System.out.println(normalMatrix);
    	
    	boolean useMultiShaderPass = true;
    	if(useMultiShaderPass){
    		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
    		glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
    		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorBuffer, 0);
    		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
    		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);
    	}
		lightingAndShadow.bind();
    	
    	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, normalBuffer);
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, depthBuffer);
		
		
        glUniformMatrix4(lightingAndShadow.uniformLocations[2], false, viewInverse.asFlippedFloatBuffer());
        
        //other
        glUniformMatrix4(lightingAndShadow.uniformLocations[19], false, normalMatrix.asFlippedFloatBuffer()); //normal
        glUniformMatrix4(lightingAndShadow.uniformLocations[20], false, modelViewMatrix.asFlippedFloatBuffer()); //view
        glUniformMatrix4(lightingAndShadow.uniformLocations[21], false, cameraProjectionMatrix); //projection
        
       	for(LightSource ls: lightSources){
       		glUniform1i(lightingAndShadow.uniformLocations[4], 0); //no shadows
    		Vector4f pos =  new Vector4f(ls.getPos(), 1.0f);
    		Matrix4f viewMatrix = new Matrix4f(cameraViewMatrix);
    		pos = viewMatrix.mul(pos);
    		Vector3f normal = new Vector3f(-viewMatrix.get(4), -viewMatrix.get(5), -viewMatrix.get(6));
    		glUniform3(lightingAndShadow.uniformLocations[1], pos.asFlippedFloatBuffer());
    		glUniform3(lightingAndShadow.uniformLocations[7], normal.asFlippedFloatBuffer());
    		glUniform4(lightingAndShadow.uniformLocations[9], ls.getAmbient().asFlippedFloatBuffer());
    		glUniform4(lightingAndShadow.uniformLocations[10], ls.getDiffuse().asFlippedFloatBuffer());
    		glUniform4(lightingAndShadow.uniformLocations[11], ls.getSpecular().asFlippedFloatBuffer());
    		glUniform1f(lightingAndShadow.uniformLocations[12], ls.getConstantAttenuation());
    		glUniform1f(lightingAndShadow.uniformLocations[13], ls.getLinearAttenuation());
    		glUniform1f(lightingAndShadow.uniformLocations[14], ls.getQuadricAttenuation());
    		glUniform1f(lightingAndShadow.uniformLocations[15], ls.getSpotCutoff());
    		glUniform3(lightingAndShadow.uniformLocations[16], ls.getSpotLightDirection().asFlippedFloatBuffer());
    		glUniform1f(lightingAndShadow.uniformLocations[17], ls.getSpotExponent());
    		glUniform1i(lightingAndShadow.uniformLocations[18], ls.getLightType());


    		glBegin(GL_QUADS);
    			glVertex2f(0.0f, 0.0f);
    			glVertex2f(1.0f, 0.0f);
    			glVertex2f(1.0f, 1.0f);
    			glVertex2f(0.0f, 1.0f);
    		glEnd();	
       	}
       	Shader.unbind();
       	
       	
       	glActiveTexture(GL_TEXTURE4); glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        glActiveTexture(GL_TEXTURE5); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE3); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
        
    	
      	if(useMultiShaderPass){
      		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    		
    		glMatrixMode(GL_PROJECTION);
    		glLoadIdentity();

    		glMatrixMode(GL_MODELVIEW);
    		glLoadIdentity();

    		glEnable(GL_TEXTURE_2D);
    		
    		glBindTexture(GL_TEXTURE_2D, colorBuffer);

    		glBegin(GL_QUADS);
    			glTexCoord2f(0.0f, 0.0f); glVertex2f(-1.0f, -1.0f);
    			glTexCoord2f(1.0f, 0.0f); glVertex2f(1.0f, -1.0f);
    			glTexCoord2f(1.0f, 1.0f); glVertex2f(1.0f, 1.0f);
    			glTexCoord2f(0.0f, 1.0f); glVertex2f(-1.0f, 1.0f);
    		glEnd();

    		
    		glBindTexture(GL_TEXTURE_2D, 0);

    		glDisable(GL_TEXTURE_2D);
			
      	}
    }
    
        

	public void resize(){
		updateMatrices((Camera)null);
		width = Game.width;
		height = Game.height;
		
		System.out.println("Resized to: " + width + " " + height);
		
		glViewport(0, 0, Display.getWidth(), Display.getHeight());

        Matrix4f camInverse = new Matrix4f(cameraProjectionMatrix);
        camInverse.invert();
        camInverse.mul(Matrix4f.biasMatrixInverse);

        projectionBiasInverse = camInverse.asFlippedFloatBuffer();

		glBindTexture(GL_TEXTURE_2D, colorBuffer);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

		glBindTexture(GL_TEXTURE_2D, normalBuffer);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

		glBindTexture(GL_TEXTURE_2D, depthBuffer);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

	    lightingAndShadow.bind();
		glUniform2f(lightingAndShadow.uniformLocations[22], (float)width / 64.0f, (float)height / 64.0f);
	    glUniformMatrix4(lightingAndShadow.uniformLocations[0], false, projectionBiasInverse);
	    Shader.unbind();

	}
	
    
    public void dispose() {
        if(GLContext.getCapabilities().OpenGL21){
            preprocess.destroy();
            lightingAndShadow.destroy();
        }
        
        glDeleteTextures(colorBuffer);
        glDeleteTextures(normalBuffer);
        glDeleteTextures(depthBuffer);
    	
        if(GLContext.getCapabilities().GL_EXT_framebuffer_object){
        	glDeleteFramebuffers(FBO);
        }
    }
    
    
    public void renderObjects(List<Entity> entities, boolean depthOnly){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    	if(depthOnly){ //shadows
	        glCullFace(GL_FRONT);
	        Model.setRenderMode(true, false, false, false, false, false); //only vertices
	        for(Entity e: entities){
	        	e.render();
	        }
	        Model.resetRenderMode();
	        glCullFace(GL_BACK);
    	}else{ //normal
    		Model.setRenderMode(true, true, true, true, true, true);
	        for(Entity e: entities){
    			e.render();
	        }
	        Model.resetRenderMode();
    	}
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }

}
