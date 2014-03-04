package game.world.graphics;

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
import game.Game;
import game.world.entities.Entity;
import game.world.entities.LightSource;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Set;

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
import blender.model.custom.Cuboid;
import blender.model.custom.Sphere;
import shader.Shader;
import shader.Shader;
import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class RenderEngine3D {
	
    private int width, height;
    private FloatBuffer lightProjection, projectionBiasInverse;
    private Matrix4f viewInverse = new Matrix4f();

    private  Shader preprocess = new Shader();
    private Shader SSAO = new Shader();
    private Shader SSAOFilterH = new Shader();
    private Shader SSAOFilterV = new Shader();
    private Shader deferredLighting = new Shader();

    private int rotationTexture, colorBuffer, normalBuffer, depthBuffer, SSAOTexture, FBO, finalTexture;
    private IntBuffer blurTextures;
    private int materialAmbient, materialDiffuse, materialSpecular, materialEmission, materialShininess;
    
    private boolean  texturing, normalMapping, shadows, filtering, occlusion, fullSSAO, showSSAO;
	
    //View, Projections
    public Camera camera = new Camera();
    public FloatBuffer cameraProjectionMatrix = BufferUtils.createFloatBuffer(16);
    public FloatBuffer cameraViewMatrix = BufferUtils.createFloatBuffer(16);
    private Set<LightSource> lightSources;
      
    private void initVariables(){
    	projectionBiasInverse = BufferUtils.createFloatBuffer(16); 
    	lightProjection = BufferUtils.createFloatBuffer(16);
    	
    	texturing = true;
    	normalMapping = true;
    	shadows = true;
    	filtering = true;
    	occlusion = false;
    	fullSSAO = true;
    	showSSAO = false;
    }
    
    public void update(float dt){
    	
    }
    
    public void updateLightSources(Set<LightSource> lightSources){
    	this.lightSources = lightSources;
    }
    
	public void updateMatrices(Camera cam){
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
		initVariables();
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
    	error = !SSAO.load("renderengine//ssaoshadow.vs", "renderengine//ssaoshadow.fs");
    	error = !SSAOFilterH.load("renderengine//ssaoshadowfilter.vs", "renderengine//ssaoshadowfilterh.fs");
    	error = !SSAOFilterV.load("renderengine//ssaoshadowfilter.vs", "renderengine//ssaoshadowfilterv.fs");
    	error = !deferredLighting.load("renderengine//deferredlighting.vs", "renderengine//deferredlighting.fs");
    	
    	if(error){
    		System.err.println("Error occoured!");
            System.exit(0);
    	}
    	
		// Uniform stuff --------------------------------------------------------------------------------------------------
		preprocess.attribLocations = new int[1];
        preprocess.attribLocations[0] = glGetAttribLocation(preprocess.i(), "vert_Tangent");
    	
    	preprocess.uniformLocations = new int[2];
        preprocess.uniformLocations[0] = glGetUniformLocation(preprocess.i(), "Texturing");
        preprocess.uniformLocations[1] = glGetUniformLocation(preprocess.i(), "NormalMapping");
        
        deferredLighting.bind();
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "ColorBuffer"), 0);
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "NormalBuffer"), 1);

        SSAO.uniformLocations = new int[2];
        SSAO.uniformLocations[0] = glGetUniformLocation(SSAO.i(), "sxy");
        SSAO.uniformLocations[1] = glGetUniformLocation(SSAO.i(), "ProjectionBiasInverse");

        SSAOFilterH.uniformLocations = new int[3];
        SSAOFilterH.uniformLocations[0] = glGetUniformLocation(SSAOFilterH.i(), "sx");
        SSAOFilterH.uniformLocations[1] = glGetUniformLocation(SSAOFilterH.i(), "sx2");
        SSAOFilterH.uniformLocations[2] = glGetUniformLocation(SSAOFilterH.i(), "sx3");

        SSAOFilterV.uniformLocations = new int[3];
        SSAOFilterV.uniformLocations[0] = glGetUniformLocation(SSAOFilterV.i(), "sy");
        SSAOFilterV.uniformLocations[1] = glGetUniformLocation(SSAOFilterV.i(), "sy2");
        SSAOFilterV.uniformLocations[2] = glGetUniformLocation(SSAOFilterV.i(), "sy3");

        deferredLighting.uniformLocations = new int[22];
        deferredLighting.uniformLocations[0] = glGetUniformLocation(deferredLighting.i(), "ProjectionBiasInverse");

        deferredLighting.uniformLocations[2] = glGetUniformLocation(deferredLighting.i(), "ViewInverse");
        deferredLighting.uniformLocations[3] = glGetUniformLocation(deferredLighting.i(), "LightTexture");
        deferredLighting.uniformLocations[4] = glGetUniformLocation(deferredLighting.i(), "Shadows");
        deferredLighting.uniformLocations[5] = glGetUniformLocation(deferredLighting.i(), "Filtering");
        deferredLighting.uniformLocations[6] = glGetUniformLocation(deferredLighting.i(), "Occlusion");

        deferredLighting.uniformLocations[8] = glGetUniformLocation(deferredLighting.i(), "CubeLight");
        
        //Light describing
        deferredLighting.uniformLocations[1] = glGetUniformLocation(deferredLighting.i(), "LightSourcePosition");
        deferredLighting.uniformLocations[7] = glGetUniformLocation(deferredLighting.i(), "LightSourceNormal");
        
        deferredLighting.uniformLocations[9] = glGetUniformLocation(deferredLighting.i(), "LightSourceAmbient");
        deferredLighting.uniformLocations[10] = glGetUniformLocation(deferredLighting.i(), "LightSourceDiffuse");
        deferredLighting.uniformLocations[11] = glGetUniformLocation(deferredLighting.i(), "LightSourceSpecular");
        deferredLighting.uniformLocations[12] = glGetUniformLocation(deferredLighting.i(), "LightSourceConstantAttenuation");
        deferredLighting.uniformLocations[13] = glGetUniformLocation(deferredLighting.i(), "LightSourceLinearAttenuation");
        deferredLighting.uniformLocations[14] = glGetUniformLocation(deferredLighting.i(), "LightSourceQuadricAttenuation");
        deferredLighting.uniformLocations[15] = glGetUniformLocation(deferredLighting.i(), "LightSourceSpotCutoff");
        deferredLighting.uniformLocations[16] = glGetUniformLocation(deferredLighting.i(), "LightSourceSpotLightDirection");
        deferredLighting.uniformLocations[17] = glGetUniformLocation(deferredLighting.i(), "LightSourceSpotExponent");
        deferredLighting.uniformLocations[18] = glGetUniformLocation(deferredLighting.i(), "LightSourceType");
        
        deferredLighting.uniformLocations[19] = glGetUniformLocation(deferredLighting.i(), "NormalMatrix");
        deferredLighting.uniformLocations[20] = glGetUniformLocation(deferredLighting.i(), "ModelViewMatrix");
        deferredLighting.uniformLocations[21] = glGetUniformLocation(deferredLighting.i(), "ProjectionMatrix");
        
        // set texture indices in shaders -----------------------------------------------------------------------------------------
        
    	preprocess.bind();
    	glUniform1i(glGetUniformLocation(preprocess.i(), "Texture"), 0);
    	glUniform1i(glGetUniformLocation(preprocess.i(), "NormalMap"), 1);
    	glUseProgram(0);
        
        SSAO.bind();
        glUniform1i(glGetUniformLocation(SSAO.i(), "NormalBuffer"), 0);
        glUniform1i(glGetUniformLocation(SSAO.i(), "DepthBuffer"), 1);
        glUniform1i(glGetUniformLocation(SSAO.i(), "RotationTexture"), 2);
        Shader.unbind();
        
        float s = 128.0f, e = 131070.0f, fs = 1.0f / s, fe = 1.0f / e, fd = fs - fe;
        
    	SSAOFilterH.bind();
    	glUniform1i(glGetUniformLocation(SSAOFilterH.i(), "SSAOTexture"), 0);
    	glUniform1i(glGetUniformLocation(SSAOFilterH.i(), "DepthBuffer"), 1);
    	glUniform1f(glGetUniformLocation(SSAOFilterH.i(), "fs"), fs);
    	glUniform1f(glGetUniformLocation(SSAOFilterH.i(), "fd"), fd);
        Shader.unbind();   

    	SSAOFilterV.bind();
    	glUniform1i(glGetUniformLocation(SSAOFilterV.i(), "SSAOTexture"), 0);
    	glUniform1i(glGetUniformLocation(SSAOFilterV.i(), "DepthBuffer"), 1);
    	glUniform1f(glGetUniformLocation(SSAOFilterV.i(), "fs"), fs);
    	glUniform1f(glGetUniformLocation(SSAOFilterV.i(), "fd"), fd);
        Shader.unbind();   

        deferredLighting.bind();
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "ColorBuffer"), 0);
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "NormalBuffer"), 1);
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "DepthBuffer"), 2);
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "SSAOTexture"), 3);
        glUniform1i(glGetUniformLocation(deferredLighting.i(), "ShadowCubeMap"), 4);
    	glUniform1i(glGetUniformLocation(deferredLighting.i(), "RotationTexture"), 5);
        Shader.unbind();   
        
    	preprocess.validate();
    	SSAO.validate();
    	SSAOFilterH.validate();
    	SSAOFilterV.validate();
    	deferredLighting.validate();
        
        // generate 16 2D vectors used for sampling the depth buffer (SSAOShadow) -------------------------------------------------
        FloatBuffer samples = BufferUtils.createFloatBuffer(16*2);
        float randomAngle = (float)Math.PI/4, radius = 0.415f;

        for(int i = 0; i < 16; i++){
        	samples.put((float)Math.cos(randomAngle) * (float)(i + 1) / 16.0f * radius);
        	samples.put((float)Math.sin(randomAngle) * (float)(i + 1) / 16.0f * radius);

            randomAngle += (float)Math.PI/2;

            if(((i + 1) % 4) == 0) randomAngle += (float)Math.PI/4;
        }
        samples.flip();

        SSAO.bind();
        glUniform2(glGetUniformLocation(SSAO.i(), "Samples"), samples);
        Shader.unbind();  
        
        samples = BufferUtils.createFloatBuffer(16*2);
    	float angle = 0.0f;

        for(int i = 0; i < 16; i++){
        	samples.put((float)Math.sin(angle) * ((float)i + 1.0f) / 16.0f / 1024.0f * 2.0f);
        	samples.put((float)Math.cos(angle) * ((float)i + 1.0f) / 16.0f / 1024.0f * 2.0f);

        	angle += (float)Math.PI/2;

            if(((i + 1) % 4) == 0) angle += (float)Math.PI/4;
        }
        samples.flip();

        deferredLighting.bind();
        glUniform2(glGetUniformLocation(deferredLighting.i(), "Samples"), samples);
        Shader.unbind();  

        // generate 64x64 rotation texture used for rotating the sampling 2D vectors (SSAOShadow) ---------------------------------
        FloatBuffer rotationTextureData = BufferUtils.createFloatBuffer(64*64*4);

        randomAngle = (float)Math.random() * (float)Math.PI * 2.0f;
        
        for(int i = 0; i < 64 * 64; i++){
        	rotationTextureData.put((float)Math.cos(randomAngle) * 0.5f + 0.5f);
        	rotationTextureData.put((float)Math.sin(randomAngle) * 0.5f + 0.5f);
        	rotationTextureData.put((float)-Math.sin(randomAngle) * 0.5f + 0.5f);
        	rotationTextureData.put((float)Math.cos(randomAngle) * 0.5f + 0.5f);

            randomAngle += (float)Math.random() * (float)Math.PI * 2.0f;
        }
        rotationTextureData.flip();
        
        rotationTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, rotationTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 64, 64, 0, GL_RGBA, GL_FLOAT, rotationTextureData);

        // set light projection matrix --------------------------------------------------------------------------------------------
        glLoadIdentity();
        gluPerspective(90, 1, 0.125f, 512.0f);
        glGetFloat(GL_MODELVIEW_MATRIX, lightProjection);
        
        // generate framebuffer textures ------------------------------------------------------------------------------------------
        colorBuffer = glGenTextures();
        normalBuffer = glGenTextures();
        depthBuffer = glGenTextures();
        blurTextures = BufferUtils.createIntBuffer(2);
        glGenTextures(blurTextures);
        SSAOTexture = glGenTextures();
        finalTexture = glGenTextures();
        
        materialAmbient = glGenTextures();
        materialDiffuse = glGenTextures();
        materialSpecular = glGenTextures();
        materialEmission = glGenTextures();
        materialShininess = glGenTextures();

        // generate framebuffer object --------------------------------------------------------------------------------------------
        FBO = glGenFramebuffersEXT();
	}
	
	public void checkInput(int i){
		switch(i){
		case Keyboard.KEY_F1:
			texturing = !texturing; 
			System.out.println("Changed texturing to " + texturing);
			break;
		case Keyboard.KEY_F2:
			normalMapping = !normalMapping;
			System.out.println("Changed normalMapping to " + normalMapping);
			break;
		case Keyboard.KEY_F3:
			shadows = !shadows;
			System.out.println("Changed shadows to " + shadows);
			break;
		case Keyboard.KEY_F4:
			filtering = !filtering;
			System.out.println("Changed filtering to " + filtering);
			break;
		case Keyboard.KEY_F5:
			occlusion = !occlusion;
			System.out.println("Changed occlusion to " + occlusion);
			break;
		case Keyboard.KEY_F6:
			fullSSAO = !fullSSAO;
			System.out.println("Changed fullSAO to " + fullSSAO);
			firstRun = true;
			break;
		case Keyboard.KEY_F7:
			showSSAO = !showSSAO;
			System.out.println("Changed showSSAO to " + showSSAO);
			break;
		}
	}
	
	private void moveLight(){
		for(LightSource ls: lightSources){
			int dx = 0, dy = 0, dz = 0;
	    	if(Keyboard.isKeyDown(Keyboard.KEY_UP))
	    		dx -= 1;
	    	if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
	    		dx += 1;
	    	if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
	    		dz -= 1;
	    	if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
	    		dz += 1;
	    	if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4))
	    		dy += 1;
	    	if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1))
	    		dy -= 1;
	    	Vector3f pos = ls.getPos();
	    	ls.getMotionState().origin.set(pos.x+dx, pos.y+dy, pos.z+dz);
		}

	}
	
	boolean firstRun = true;
    public void render(List<Entity> entities){
    	if(firstRun){
    		resize();
    		firstRun = false;
    	}
    	moveLight();
    	
    	IntBuffer buffers12 = BufferUtils.createIntBuffer(7);
		buffers12.put(GL_COLOR_ATTACHMENT0).put(GL_COLOR_ATTACHMENT1);/*.put(GL_COLOR_ATTACHMENT2).
		put(GL_COLOR_ATTACHMENT3).put(GL_COLOR_ATTACHMENT4).put(GL_COLOR_ATTACHMENT5).put(GL_COLOR_ATTACHMENT6);*/
		buffers12.rewind();
    	IntBuffer buffers1 = BufferUtils.createIntBuffer(1);
		buffers1.put(GL_COLOR_ATTACHMENT0);
		buffers1.rewind();
    	
    	// set viewport, perspective projection, modelview matrix and light position-----------------------------------------------
		//glViewport(0, 0, superSamplingBufferWidth, superSamplingBufferHeight);
    	
	    glMatrixMode(GL_PROJECTION);
	    glLoadMatrix(cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(cameraViewMatrix);
        
        // 1st pass - render scene to textures ------------------------------------------------------------------------------------
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
        glDrawBuffers(buffers12);glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorBuffer, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normalBuffer, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);
        
       /* glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, materialAmbient, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, materialDiffuse, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, materialSpecular, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT5, GL_TEXTURE_2D, materialEmission, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, materialShininess, 0);*/
        
        
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    
    	preprocess.bind();

        renderObjects(entities, false);
        
        Shader.unbind();

	    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	   
	    // render shadow cube map -------------------------------------------------------------------------------------------------
	    if(shadows && !showSSAO){
	    	viewInverse = new Matrix4f(cameraViewMatrix).getInvert();
	    	//glPolygonOffset(1.1f, 4.0f); 
	    	//glEnable(GL_POLYGON_OFFSET_FILL);
	    	
	    	for(LightSource ls: lightSources){
	    		ls.update(new Matrix4f(cameraViewMatrix));
	    		
		        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		        glDrawBuffers(0); glReadBuffer(GL_NONE);
		        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, 0, 0);
		        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, 0, 0);
	    	
	    		glViewport(0, 0, ls.getShadowMapSize(), ls.getShadowMapSize());
		        ls.setProjection();
		        if(ls.isCubeLight()){
			        for(int i =0;i<6;i++){
			        	ls.renderToTexture(i);
			        	renderObjects(entities, true);
			        }
		        }else{
		        	ls.renderToTexture();
		        	renderObjects(entities, true);
		        }

		        
		        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		        
	    	}
	    	//glDisable(GL_POLYGON_OFFSET_FILL);
	    	
	    }
	    glViewport(0, 0, width, height);	
	    // calculate screen space ambient occlusion -------------------------------------------------------------------------------
	    
	    if(occlusion || showSSAO){
    		if(!fullSSAO){
    			glViewport(0, 0, width / 2, height / 2);
    		}
    		
	    	SSAOStuff();
	    	
    		if(!fullSSAO){
    			glViewport(0, 0, width, height);
    		}


	        filterStuff();
	    }
    	
	    // 2nd pass - calculate lighting - render fullscreen quad with deferred lighting shader applied ---------------------------
	    if(showSSAO){
	    	showSSAOStuff();
	    }else{
	    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    	glEnable(GL_BLEND);
	    	deferredLightingStuff();
	    	glDisable(GL_BLEND);
	    }
	   
    }
    
    public void showSSAOStuff(){
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glEnable(GL_TEXTURE_2D);

		glBindTexture(GL_TEXTURE_2D, blurTextures.get(1));

		glBegin(GL_QUADS);
			glTexCoord2f(0.0f, 0.0f); glVertex2f(-1.0f, -1.0f);
			glTexCoord2f(1.0f, 0.0f); glVertex2f(1.0f, -1.0f);
			glTexCoord2f(1.0f, 1.0f); glVertex2f(1.0f, 1.0f);
			glTexCoord2f(0.0f, 1.0f); glVertex2f(-1.0f, 1.0f);
		glEnd();

		glBindTexture(GL_TEXTURE_2D, 0);

		glDisable(GL_TEXTURE_2D);
    }
    
    public void SSAOStuff(){
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, SSAOTexture, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);
    	
        glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, normalBuffer);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, depthBuffer);
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, rotationTexture);
        SSAO.bind();
        glBegin(GL_QUADS);
            glVertex2f(0.0f, 0.0f);
            glVertex2f(1.0f, 0.0f);
            glVertex2f(1.0f, 1.0f);
            glVertex2f(0.0f, 1.0f);
        glEnd();
        Shader.unbind();
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
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
		deferredLighting.bind();
    	
    	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, normalBuffer);
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, depthBuffer);
        glActiveTexture(GL_TEXTURE3); glBindTexture(GL_TEXTURE_2D, blurTextures.get(1));
		glActiveTexture(GL_TEXTURE5); glBindTexture(GL_TEXTURE_2D, rotationTexture);
		
		//glUniform1i(deferredLighting.uniformLocations[4], shadows ? 1 : 0);
		glUniform1i(deferredLighting.uniformLocations[5], filtering ? 1 : 0);
		glUniform1i(deferredLighting.uniformLocations[6], occlusion ? 1 : 0);
        glUniformMatrix4(deferredLighting.uniformLocations[2], false, viewInverse.asFlippedFloatBuffer());
        
        //other
        glUniformMatrix4(deferredLighting.uniformLocations[19], false, normalMatrix.asFlippedFloatBuffer()); //normal
        glUniformMatrix4(deferredLighting.uniformLocations[20], false, modelViewMatrix.asFlippedFloatBuffer()); //view
        glUniformMatrix4(deferredLighting.uniformLocations[21], false, cameraProjectionMatrix); //projection
        
		//int c = 0;
       	for(LightSource ls: lightSources){
       		if(ls.castShadows() && shadows){
       			glUniform1i(deferredLighting.uniformLocations[4], 1);
       			glUniform1i(deferredLighting.uniformLocations[8], ls.isCubeLight() ? 1 : 0);
       			glActiveTexture(GL_TEXTURE4); glBindTexture(GL_TEXTURE_2D_ARRAY, ls.getShadowMap());
        		glUniformMatrix4(deferredLighting.uniformLocations[3], false, Utils.combineFloatBuffers(ls.getLightTexture()));
       		}else{
       			glUniform1i(deferredLighting.uniformLocations[4], 0); //no shadows
       		}
    		Vector4f pos =  new Vector4f(ls.getPos(), 1.0f);
    		Matrix4f viewMatrix = new Matrix4f(cameraViewMatrix);
    		pos = viewMatrix.mul(pos);
    		Vector3f normal = new Vector3f(-viewMatrix.get(4), -viewMatrix.get(5), -viewMatrix.get(6));
    		glUniform3(deferredLighting.uniformLocations[1], pos.asFlippedFloatBuffer());
    		glUniform3(deferredLighting.uniformLocations[7], normal.asFlippedFloatBuffer());
    		glUniform4(deferredLighting.uniformLocations[9], ls.getAmbient().asFlippedFloatBuffer());
    		glUniform4(deferredLighting.uniformLocations[10], ls.getDiffuse().asFlippedFloatBuffer());
    		glUniform4(deferredLighting.uniformLocations[11], ls.getSpecular().asFlippedFloatBuffer());
    		glUniform1f(deferredLighting.uniformLocations[12], ls.getConstantAttenuation());
    		glUniform1f(deferredLighting.uniformLocations[13], ls.getLinearAttenuation());
    		glUniform1f(deferredLighting.uniformLocations[14], ls.getQuadricAttenuation());
    		glUniform1f(deferredLighting.uniformLocations[15], ls.getSpotCutoff());
    		glUniform3(deferredLighting.uniformLocations[16], ls.getSpotLightDirection().asFlippedFloatBuffer());
    		glUniform1f(deferredLighting.uniformLocations[17], ls.getSpotExponent());
    		glUniform1i(deferredLighting.uniformLocations[18], ls.getLightType());


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

	        //Final result from all lights
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
    
    public void filterStuff(){
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, blurTextures.get(0), 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);

		glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, SSAOTexture);
		glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, depthBuffer);
		
		SSAOFilterH.bind();

		glBegin(GL_QUADS);
			glVertex2f(0.0f, 0.0f);
			glVertex2f(1.0f, 0.0f);
			glVertex2f(1.0f, 1.0f);
			glVertex2f(0.0f, 1.0f);
		glEnd();

		Shader.unbind();

		glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		// --------------------------------------------------------------------------------------------------------------------

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, blurTextures.get(1), 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);

		glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, blurTextures.get(0));
		glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, depthBuffer);

 		SSAOFilterV.bind();

		glBegin(GL_QUADS);
			glVertex2f(0.0f, 0.0f);
			glVertex2f(1.0f, 0.0f);
			glVertex2f(1.0f, 1.0f);
			glVertex2f(0.0f, 1.0f);
		glEnd();

		Shader.unbind();

		glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }
	
	public void resize(){
		updateMatrices((Camera)null);
		width = Display.getWidth();
		height = Display.getHeight();
		
		glViewport(0, 0, Display.getWidth(), Display.getHeight());

        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(cameraProjectionMatrix);

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
		
		glBindTexture(GL_TEXTURE_2D, SSAOTexture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, fullSSAO ? GL_NEAREST : GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, fullSSAO ? GL_NEAREST : GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, fullSSAO ? GL_CLAMP : GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, fullSSAO ? GL_CLAMP : GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width / (fullSSAO ? 1 : 2), height / (fullSSAO ? 1 : 2), 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

		for(int i = 0; i < 2; i++){
			glBindTexture(GL_TEXTURE_2D, blurTextures.get(i));
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		
		glBindTexture(GL_TEXTURE_2D, finalTexture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		
		
		glBindTexture(GL_TEXTURE_2D, materialAmbient);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glBindTexture(GL_TEXTURE_2D, materialDiffuse);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glBindTexture(GL_TEXTURE_2D, materialSpecular);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		glBindTexture(GL_TEXTURE_2D, materialEmission);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

		glBindTexture(GL_TEXTURE_2D, materialShininess);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

        SSAO.bind();
        //glUniform2f(SSAO.uniformLocations[0], (float)SSAOShadowBufferWidth / 64.0f, (float)SSAOShadowBufferHeight / 64.0f);
        glUniform2f(SSAO.uniformLocations[0], (float)(width / (fullSSAO ? 1 : 2)) / 64.0f, (float)(height / (fullSSAO ? 1 : 2)) / 64.0f);
        glUniformMatrix4(SSAO.uniformLocations[1], false, projectionBiasInverse);
        Shader.unbind();
        
        SSAOFilterH.bind();
		glUniform1f(SSAOFilterH.uniformLocations[0], 1.0f / (float)width);
		glUniform1f(SSAOFilterH.uniformLocations[1], 2.0f / (float)width);
		glUniform1f(SSAOFilterH.uniformLocations[2], 3.0f / (float)width);
        Shader.unbind();

        SSAOFilterV.bind();
		glUniform1f(SSAOFilterV.uniformLocations[0], 1.0f / (float)height);
		glUniform1f(SSAOFilterV.uniformLocations[1], 2.0f / (float)height);
		glUniform1f(SSAOFilterV.uniformLocations[2], 3.0f / (float)height);
        Shader.unbind();

	    deferredLighting.bind();
	    glUniformMatrix4(deferredLighting.uniformLocations[0], false, projectionBiasInverse);
 
	    Shader.unbind();
	}
    
    public void dispose() {
        if(GLContext.getCapabilities().OpenGL21){
            preprocess.destroy();
            SSAO.destroy();
            SSAOFilterH.destroy();
            SSAOFilterV.destroy();
            deferredLighting.destroy();
        }
        
        glDeleteTextures(rotationTexture);
        glDeleteTextures(colorBuffer);
        glDeleteTextures(normalBuffer);
        glDeleteTextures(depthBuffer);
        glDeleteTextures(blurTextures);
        glDeleteTextures(SSAOTexture);
    	
        if(GLContext.getCapabilities().GL_EXT_framebuffer_object){
        	glDeleteFramebuffers(FBO);
        }
    }
    
    private void renderFull(List<Entity> entities){
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

        		    glEnableClientState(GL_NORMAL_ARRAY);
        	        glBindBuffer(GL_ARRAY_BUFFER, sM.vboNormalID);
        	        glNormalPointer(GL_FLOAT, 0, 0);

        		    glEnableClientState(GL_VERTEX_ARRAY);
        	        glBindBuffer(GL_ARRAY_BUFFER, sM.vboVertexID);
        	        glVertexPointer(3, GL_FLOAT, 0, 0);
        	        
           	        if (sM.isTextured){
        	            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        	            glBindBuffer(GL_ARRAY_BUFFER, sM.vboTexVertexID);
        	            glTexCoordPointer(2, GL_FLOAT, 0, 0);
        	        	if(texturing)
        	        		glUniform1i(preprocess.uniformLocations[0], 1); //Enable texturing
        	        	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, sM.material.textureHandle);
        	        	//glBindTexture(GL_TEXTURE_2D, sM.material.textureHandle);
        	        }else{
        	        	glEnableClientState(GL_COLOR_ARRAY);
            	        glBindBuffer(GL_ARRAY_BUFFER, sM.vboColorID);
            	        glColorPointer(3, GL_FLOAT, 0, 0);
            	        glUniform1i(preprocess.uniformLocations[0], 0);
        	        }
    				if(sM.isNormalMapped){
    					
    					glEnableVertexAttribArray(preprocess.attribLocations[0]);
    					ARBVertexShader.glVertexAttribPointerARB(preprocess.attribLocations[0], 3, GL_FLOAT, false, 0, 0);
						//glVertexAttribPointer(preprocess.attribLocations[0], 3, false, false, 0, (IntBuffer)null);
    					if(normalMapping){
    						glUniform1i(preprocess.uniformLocations[1], 1); //Enable normalMapping
    					}
    					glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, sM.material.normalHandle); //normalmap texture
    				}else{
    					glUniform1i(preprocess.uniformLocations[1], 0);
    				}

        	        glDrawArrays(GL_TRIANGLES, 0, 9 * sM.faces.size());  

        	        glBindBuffer(GL_ARRAY_BUFFER, 0);
        	        glDisableClientState(GL_VERTEX_ARRAY);
        	        glDisableClientState(GL_NORMAL_ARRAY);
        	        if(sM.isNormalMapped){
        	        	glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        	        	glDisableVertexAttribArray(preprocess.attribLocations[0]);
        	        }
        	        if(sM.isTextured){
        	        	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
        	            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        	        }else{
        	        	glDisableClientState(GL_COLOR_ARRAY);
        	        }
        	        Material.clear();
        		}
        	}
        	glPopMatrix(); //reset transformations
        }
    }
    
    private void renderDepth(List<Entity> entities){
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
        		    glEnableClientState(GL_VERTEX_ARRAY);
        	        glBindBuffer(GL_ARRAY_BUFFER, sM.vboVertexID);
        	        glVertexPointer(3, GL_FLOAT, 0, 0);

        	        glDrawArrays(GL_TRIANGLES, 0, 9 * sM.faces.size());  

        	        glBindBuffer(GL_ARRAY_BUFFER, 0);
        	        glDisableClientState(GL_VERTEX_ARRAY);
        		}
        	}
        	glPopMatrix(); //reset transformations
        }
    }
    
    public void renderObjects(List<Entity> entities, boolean depthOnly){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    	if(depthOnly){
	        glCullFace(GL_FRONT);
    		renderDepth(entities);
	        glCullFace(GL_BACK);
    	}else{
    		renderFull(entities);
    	}
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }

}
