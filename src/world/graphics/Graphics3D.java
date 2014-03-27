package world.graphics;

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
import input.Input;
import input.InputListener;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.vecmath.Quat4f;

import main.PlayState;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import controller.Camera;
import resources.Resources;
import resources.model.Material;
import resources.model.Model;
import resources.model.SubModel;
import resources.model.custom.Sphere;
import resources.texture.Texture;
import shader.Shader;
import shader.Shader;
import state.Game;
import threading.RenderThread;
import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Vector2f;
import utils.math.Vector3f;
import utils.math.Vector4f;
import world.World;
import world.entity.VisualEntity;
import world.entity.lighting.DirectionalLighting;
import world.entity.lighting.Lighting;
import world.entity.lighting.PointLighting;
import world.entity.lighting.SpotLighting;
import world.entity.lighting.SunLight;

public class Graphics3D implements Input{
	
	//Store materials, bind textures, normal mapping
    public Shader preprocess = new Shader();
    
    //Lighting and shadows
    private Shader lightingAndShadow = new Shader(); 
    private ShadowMapper shadowMapper = new ShadowMapper();
    
    //Screen space ambient occlusion
    private Shader SSAO = new Shader(); 
    private Shader SSAOFilterH = new Shader(); //SSAO Horizontal filtering
    private Shader SSAOFilterV = new Shader(); //SSAO Vertical filtering
    private IntBuffer SSAOTexturesBlurred;
    private int SSAOTexture; //without blur
    private int rotationTexture; //SSAO randomly rotated texture
    private int SSAOWidth, SSAOHeight;
    
    //Light scattering, Lens, Flare and Halo
    private Shader sunRaysLensFlareHalo = new Shader(); //Light scattering, Lens, Flare and Halo
    private Shader blurH = new Shader(); //Sun blurring
    private Shader blurV = new Shader(); //Vertical
    private IntBuffer sunTextures; //textures required for blurring and light scattering
    private int sunTextureWidth, sunTextureHeight;
    private int dirtTexture;
    
    //SkyBox
    Shader skyBoxShader = new Shader();
    SubModel skyBoxModel = new SubModel();
    int skyVAO;
    private float skyBoxIntensity = .4f;
    
    //Framebuffer object
    private int FBO;
    
    //Texture indicies
    private int colorBuffer, normalBuffer, depthBuffer, combinedLighting, combinedSpecular; //for deferred shading
    private int materialAmbient, materialDiffuse, materialSpecular, materialEmission, materialShininess;

    //Rendering settings
    private int width, height;
    private boolean  texturing = true;
    private boolean normalMapping = true;
    private boolean shadows = false; 
    private boolean filtering = true;
    private boolean occlusion = false;
    private boolean lightScattering = false;
    private int showTexture;
    
    //Other
    private FloatBuffer projectionBiasInverse = BufferUtils.createFloatBuffer(16);
    private Matrix4f viewInverse = new Matrix4f();
    public FloatBuffer cameraProjectionMatrix = BufferUtils.createFloatBuffer(16);
    public FloatBuffer cameraViewMatrix = BufferUtils.createFloatBuffer(16);
    
	public void init(){
		//new InputListener(this);
		InputListener.addGlobalInput(this);
		// check OpenGL version ---------------------------------------------------------------------------------------------------
        if(!GLContext.getCapabilities().OpenGL30){
            System.err.println("OpenGL 3.0 not supported!");
            System.exit(0);
        }
        boolean error = false;
        
        //Load textures
        Texture tex = null;
		try {
			tex = Resources.getTexture("sunlens\\lensdirt_lowc.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //for sun
        dirtTexture = tex.getID();
        
        //get shaders -----------------------------------------------------------------------------------------------------------
        try {
			preprocess = Resources.getShader("renderengine\\preprocess");
			SSAO = Resources.getShader("renderengine\\ssao");
			SSAOFilterH = Resources.getShader("renderengine\\ssaofilterh");
			SSAOFilterV = Resources.getShader("renderengine\\ssaofilterv");
			lightingAndShadow = Resources.getShader("renderengine\\deferredlighting");
			blurH = Resources.getShader("renderengine\\blurh");
			blurV = Resources.getShader("renderengine\\blurv");
			sunRaysLensFlareHalo = Resources.getShader("renderengine\\sunrayslensflarehalo");
			skyBoxShader = Resources.getShader("renderengine\\skybox");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
    	if(error){
    		System.err.println("Error occoured!");
            System.exit(0);
    	}
    	
    	//skbox model
        Texture skyBox = null;
		try {
			skyBox = Resources.getTexture("skybox\\skybox.ct");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        skyBoxModel.material.textureHandle = skyBox.getID();
    	skyBoxModel.prepareVBOVertices(new Vector3f[]{
    			new Vector3f( 1.0f, -1.0f, -1.0f), new Vector3f( 1.0f, -1.0f,  1.0f), new Vector3f( 1.0f,  1.0f,  1.0f), new Vector3f( 1.0f,  1.0f,  1.0f), new Vector3f( 1.0f,  1.0f, -1.0f), new Vector3f( 1.0f, -1.0f, -1.0f),
    		    new Vector3f(-1.0f, -1.0f,  1.0f), new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(-1.0f,  1.0f, -1.0f), new Vector3f(-1.0f,  1.0f, -1.0f), new Vector3f(-1.0f,  1.0f,  1.0f), new Vector3f(-1.0f, -1.0f,  1.0f),
    		    new Vector3f(-1.0f,  1.0f, -1.0f), new Vector3f( 1.0f,  1.0f, -1.0f), new Vector3f( 1.0f,  1.0f,  1.0f), new Vector3f( 1.0f,  1.0f,  1.0f), new Vector3f(-1.0f,  1.0f,  1.0f), new Vector3f(-1.0f,  1.0f, -1.0f),
    		    new Vector3f(-1.0f, -1.0f,  1.0f), new Vector3f( 1.0f, -1.0f,  1.0f), new Vector3f( 1.0f, -1.0f, -1.0f), new Vector3f( 1.0f, -1.0f, -1.0f), new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(-1.0f, -1.0f,  1.0f),
    		    new Vector3f( 1.0f, -1.0f,  1.0f), new Vector3f(-1.0f, -1.0f,  1.0f), new Vector3f(-1.0f,  1.0f,  1.0f), new Vector3f(-1.0f,  1.0f,  1.0f), new Vector3f( 1.0f,  1.0f,  1.0f), new Vector3f( 1.0f, -1.0f,  1.0f), 
    		    new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f( 1.0f, -1.0f, -1.0f), new Vector3f( 1.0f,  1.0f, -1.0f), new Vector3f( 1.0f,  1.0f, -1.0f), new Vector3f(-1.0f,  1.0f, -1.0f), new Vector3f(-1.0f, -1.0f, -1.0f) 
    	});
    	
    	prepareShaders(); //--------------------------------
    	
    	//sky
        skyVAO = glGenVertexArrays();
        glBindVertexArray(skyVAO);
        glBindBuffer(GL_ARRAY_BUFFER, skyBoxModel.vboVertexID);
        ARBVertexShader.glVertexAttribPointerARB(skyBoxShader.attribLocations[0], 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(skyBoxShader.attribLocations[0]);
        glBindVertexArray(0);
        
    	preprocess.validate();
    	SSAO.validate();
    	SSAOFilterH.validate();
    	SSAOFilterV.validate();
    	lightingAndShadow.validate();
    	blurH.validate();
    	blurV.validate();
    	sunRaysLensFlareHalo.validate();
    	skyBoxShader.validate();
    	
    	prepareFiltering(); ////////////
        
        // generate framebuffer textures ------------------------------------------------------------------------------------------
        colorBuffer = glGenTextures();
        normalBuffer = glGenTextures();
        depthBuffer = glGenTextures();
        SSAOTexturesBlurred = BufferUtils.createIntBuffer(2);
        glGenTextures(SSAOTexturesBlurred);
        SSAOTexture = glGenTextures();
        sunTextures = BufferUtils.createIntBuffer(4);
        glGenTextures(sunTextures);
        
        materialAmbient = glGenTextures();
        materialDiffuse = glGenTextures();
        materialSpecular = glGenTextures();
        materialEmission = glGenTextures();
        materialShininess = glGenTextures();
        
        combinedLighting = glGenTextures();
        combinedSpecular = glGenTextures();
        
        // generate framebuffer object --------------------------------------------------------------------------------------------
        FBO = glGenFramebuffersEXT();
        
        //Materials
	    glColorMaterial(GL_FRONT, GL_DIFFUSE);
	    glColorMaterial(GL_FRONT, GL_AMBIENT);
	    glColorMaterial(GL_FRONT, GL_SPECULAR);
	    glColorMaterial(GL_FRONT, GL_EMISSION);
	    glColorMaterial(GL_FRONT, GL_SHININESS);
	    
	    shadowMapper.init();
	}
	
	private void prepareFiltering(){
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
    	
    	float radyus = 2f; //default  2
        for(int i = 0; i < 16; i++){
        	samples.put((float)Math.sin(angle) * ((float)i + 1.0f) / 16.0f / 1024.0f * radyus);
        	samples.put((float)Math.cos(angle) * ((float)i + 1.0f) / 16.0f / 1024.0f * radyus);

        	angle += (float)Math.PI/2;

            if(((i + 1) % 4) == 0) angle += (float)Math.PI/4;
        }
        samples.flip();

        lightingAndShadow.bind();
        glUniform2(glGetUniformLocation(lightingAndShadow.i(), "Samples"), samples);
        Shader.unbind();  

        // generate 64x64 rotation texture used for rotating the sampling 2D vectors (SSAOShadow) ---------------------------------
        FloatBuffer rotationTextureData = BufferUtils.createFloatBuffer(64*64*4);

        randomAngle = 0.0f;
        
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
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
    private void prepareShaders(){
		// Uniform stuff --------------------------------------------------------------------------------------------------
	    //Skybox
        skyBoxShader.uniformLocations = new int[2];
        skyBoxShader.uniformLocations[0] = glGetUniformLocation(skyBoxShader.i(), "CameraPosition");
        skyBoxShader.uniformLocations[1] = glGetUniformLocation(skyBoxShader.i(), "ViewProjectionMatrix");

        skyBoxShader.attribLocations = new int[1];
        skyBoxShader.attribLocations[0] = glGetAttribLocation(skyBoxShader.i(), "vert_Position");
        
        
        /*skyBoxShader.bind();
        glUniform1i(glGetUniformLocation(skyBoxShader.i(), "CubeMap"), 0);
        glUniform1i(glGetUniformLocation(skyBoxShader.i(), "DepthBuffer"), 1);
        Shader.unbind();*/
    	
        //Sun
    	sunRaysLensFlareHalo.bind();
	    glUniform1i(glGetUniformLocation(sunRaysLensFlareHalo.i(), "LowBlurredSunTexture"), 0);
	    glUniform1i(glGetUniformLocation(sunRaysLensFlareHalo.i(), "HighBlurredSunTexture"), 1);
	    glUniform1i(glGetUniformLocation(sunRaysLensFlareHalo.i(), "DirtTexture"), 2);
	    glUniform1f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "Dispersal"), 0.1875f);
	    glUniform1f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "HaloWidth"), 0.45f);
	    glUniform1f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "Intensity"), 1.5f);
	    glUniform3f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "Distortion"), 0.94f, 0.97f, 1.00f);
	    
	    glUniform1f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "exposure"), 0.0035f);
	    glUniform1f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "decay"), 0.97875f);
	    glUniform1f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "density"), 1f);
	    glUniform1f(glGetUniformLocation(sunRaysLensFlareHalo.i(), "weight"), 8.65f);
	    Shader.unbind();
	    
    	preprocess.attribLocations = new int[1];
        preprocess.attribLocations[0] = glGetAttribLocation(preprocess.i(), "vert_Tangent");
        
    	preprocess.uniformLocations = new int[3];
        preprocess.uniformLocations[0] = glGetUniformLocation(preprocess.i(), "Texturing");
        preprocess.uniformLocations[1] = glGetUniformLocation(preprocess.i(), "NormalMapping");
        preprocess.uniformLocations[2] = glGetUniformLocation(preprocess.i(), "GodRays");

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

        lightingAndShadow.uniformLocations = new int[25];
        lightingAndShadow.uniformLocations[0] = glGetUniformLocation(lightingAndShadow.i(), "ProjectionBiasInverse");
        lightingAndShadow.uniformLocations[2] = glGetUniformLocation(lightingAndShadow.i(), "ViewInverse");
        lightingAndShadow.uniformLocations[3] = glGetUniformLocation(lightingAndShadow.i(), "LightTexture");
        lightingAndShadow.uniformLocations[4] = glGetUniformLocation(lightingAndShadow.i(), "Shadows");
        lightingAndShadow.uniformLocations[5] = glGetUniformLocation(lightingAndShadow.i(), "Filtering");
        lightingAndShadow.uniformLocations[6] = glGetUniformLocation(lightingAndShadow.i(), "Occlusion");
        lightingAndShadow.uniformLocations[8] = glGetUniformLocation(lightingAndShadow.i(), "CubeLight");
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
        lightingAndShadow.uniformLocations[23] = glGetUniformLocation(lightingAndShadow.i(), "SkyBoxIntensity");
        lightingAndShadow.uniformLocations[24] = glGetUniformLocation(lightingAndShadow.i(), "LightCount");
        
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

        lightingAndShadow.bind();
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "ColorBuffer"), 0);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "NormalBuffer"), 1);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "DepthBuffer"), 2);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "SSAOTexture"), 3);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "ShadowCubeMap"), 4);
    	glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "RotationTexture"), 5);

        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialAmbient"), 6);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialDiffuse"), 7);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialSpecular"), 8);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialEmission"), 9);
    	glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialShininess"), 10);
        Shader.unbind();   
    }

    public void render(World world){
    	//Get data from world
    	Camera cam = world.getCamera();
    	if(cam == null) //wait for camera to be ready
    		return;

    	Set<VisualEntity> entities = world.getCamera().cameraFrustum.getInsideFrustumEntities();//world.cameraFrustum.getInsideFrustumEntities();
    	//set camera view/perspective ready
    	cameraStuff(cam);
    	
    	//Random input
    	moveLight(world.getLightingEntities());
    	if(Mouse.isButtonDown(1)){
    		Mouse.setGrabbed(true);
    	}else if(Mouse.isButtonDown(0)){
    		Mouse.setGrabbed(false);
    	}
    	
    	// 1st pass - render scene to textures ------------------------------------------------------------------------------------
	    glMatrixMode(GL_PROJECTION);
	    glLoadMatrix(cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(cameraViewMatrix);
        
        glViewport(0, 0, width, height);
        
    	IntBuffer colorWriteValues = BufferUtils.createIntBuffer(8);
		colorWriteValues.put(GL_COLOR_ATTACHMENT0).put(GL_COLOR_ATTACHMENT1).put(GL_COLOR_ATTACHMENT2).
		put(GL_COLOR_ATTACHMENT3).put(GL_COLOR_ATTACHMENT4).put(GL_COLOR_ATTACHMENT5).put(GL_COLOR_ATTACHMENT6).
		put(GL_COLOR_ATTACHMENT7);
		colorWriteValues.rewind();
		
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
        glDrawBuffers(colorWriteValues);glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorBuffer, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normalBuffer, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthBuffer, 0);
        
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, materialAmbient, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, materialDiffuse, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, materialSpecular, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT5, GL_TEXTURE_2D, materialEmission, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, materialShininess, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT7, GL_TEXTURE_2D, sunTextures.get(0), 0);
        
        //glClearColor(0.0f, 0.1f, 0.1f, 1.0f)
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    
		//glEnable(GL_BLEND);
		//glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	    //Matrix4f viewProjectionMatrix = new Matrix4f(cameraProjectionMatrix).mul(new Matrix4f(cameraViewMatrix));
	    Matrix4f viewProjectionMatrix = new Matrix4f(cameraViewMatrix).mul(new Matrix4f(cameraProjectionMatrix));
	    
	    //SKYBOX
	    skyBoxShader.bind();
	    glUniform3(skyBoxShader.uniformLocations[0], cam.getPosition().asFlippedFloatBuffer());
	    glUniformMatrix4(skyBoxShader.uniformLocations[1], false, viewProjectionMatrix.asFlippedFloatBuffer());
	    glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxModel.material.textureHandle);
	    glBindVertexArray(skyVAO);
	    glDrawArrays(GL_TRIANGLES, 0, 36);
	    glBindVertexArray(0);
	    glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
	    Shader.unbind();
	    
	    //OTHER
	    preprocess.bind();
	    
	    for(Lighting ls: world.getLightingEntities())
	    	if(ls instanceof SunLight){
	    		((SunLight)ls).render();
	    		break;
	    	}
    	
    	//glDisable(GL_BLEND);
	    
        renderObjects(world.getVisualEntities(), false);
        
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT5, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, 0, 0);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT7, GL_TEXTURE_2D, 0, 0);
        Shader.unbind();

	    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	   
	    //independent pass, render shadows -------------------------------------------------------------------------------------------------
	    if(shadows){
	    	viewInverse = new Matrix4f(cameraViewMatrix).invertGet();
	    	
	    	//From where to cast shadow
	    	for(Lighting ls: world.getLightingEntities()){
	    		if(ls instanceof SunLight){
	    			if(ls.isEnabled()){
	    				shadowMapper.setCaster(ls);
	    				break;
	    			}
	    		}
	    	}
	    	if(shadowMapper.isShadowEnabled()){
	    		//Where shadow frustum looks at
	    		shadowMapper.setSceneOrigin(world.getCamera().getPosition());
    			
	    		//Set shadow lookat
    			shadowMapper.update(new Matrix4f(cameraViewMatrix));
	    		
		        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		        glDrawBuffers(0); glReadBuffer(GL_NONE);

	    		glViewport(0, 0, shadowMapper.getShadowMapSize(), shadowMapper.getShadowMapSize());
	    		shadowMapper.setShadowProjection();

	    		shadowMapper.renderToTexture();
	    		shadowMapper.getViewFrustum().cullEntities(world.getVisualEntities());
		        renderObjects(shadowMapper.getViewFrustum().getInsideFrustumEntities(), true);
		        
		        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		        
		        glViewport(0, 0, width, height);
	    	}
	    }
	    
	    //occlusion, using data from 1st pass  -------------------------------------------------------------------------------
	    if(occlusion){
	    	glViewport(0, 0, SSAOWidth, SSAOHeight);
	    	SSAOStuff();
	    	glViewport(0, 0, width, height);
	        SSAOFilterStuff();
	    }

	    // apply shadows and lightin, 2nd pass ---------------------------------------------------------------
	    //oldDeferredLightingStuff(lightSources);
	    
	    deferredLightingStuff(world.getLightingEntities());

	    
		// applying light scattering to lighting stuff
		if(lightScattering){
			/*Vector3f sunPos = null;
			for(OldEntity e : entities){
				if(e.getModel().isGodRays){
					sunPos = e.getPos();
				}
			}
			sunRaysLensFlareHaloStuff(sunPos, cam);*/
			
			SunLight sun = null;
			for(Lighting e : world.getLightingEntities()){
				if(e instanceof SunLight){
					sun = (SunLight)e;
				}
			}
			sunRaysLensFlareHaloStuff(sun, cam);
		}
	    
		//Debbuging textures
	    showTextures();
    }

    public void cameraStuff(Camera cam){
        //cameraProjectionMatrix.rewind();
        cameraViewMatrix.rewind();
        
        //Projection
        /*glLoadIdentity();
        gluPerspective(Game.fov, (float) RenderThread.displayWidth / (float) RenderThread.displayHeight, Game.zNear, Game.zFar);
        glGetFloat(GL_MODELVIEW_MATRIX, cameraProjectionMatrix);*/
        
        //View
        glLoadIdentity();
        cam.lookAt();
        glGetFloat(GL_MODELVIEW_MATRIX, cameraViewMatrix);
	}
	
	private void moveLight(Set<Lighting> lightSources){
		/*for(Lighting ls: lightSources){
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
	    	Vector3f pos = ls.getPosition();
	    	ls.setPosition(pos.x+dx,pos.y+dy,pos.z+dz);
		}*/

	}
	
	private void showTextures(){
		if(showTexture != 0){
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			
			Rectangle.render(showTexture);
		}
	}
	
    public boolean sunRaysLensFlareHaloStuff(SunLight sun, Camera cam){
    	if(sun == null)
    		return false;
	    boolean CalculateSunRaysLensFlareHalo = false;
		Vector2f lightPosOnScreen = glToScreen(sun.getPosition());
    	lightPosOnScreen = new Vector2f(lightPosOnScreen.x/Game.width,lightPosOnScreen.y/Game.height);
	    int Test = 0, Tests = 16;
	    float Angle = 0.0f, AngleInc = 360.0f / Tests;
	    Matrix4f biasMatrix = Matrix4f.biasMatrix.copy();
	   // Matrix4f VPB = biasMatrix.mulReverse(new Matrix4f(cameraProjectionMatrix)).mulReverse(new Matrix4f(cameraViewMatrix));
	    Matrix4f VPB = new Matrix4f(cameraViewMatrix).mul(new Matrix4f(cameraProjectionMatrix)).mul(biasMatrix);
	    
	    while(Test < Tests && !CalculateSunRaysLensFlareHalo){
	    	Vector3f temp2 = cam.getRightVector().rotateGet(Angle, cam.getViewRay()).mul(sun.getRadius()* 1.3f);//Utils.rotate(cam.getRightVector(), Angle, cam.getViewRay()).mul(8.75f * 1.3f);
	    	Vector4f temp = new Vector4f(sun.getPosition().copy().add(temp2), 1.0f);
	    	
	        Vector4f SunPosProj = VPB.mul(temp);
	        SunPosProj.mul(1/SunPosProj.w);

	        CalculateSunRaysLensFlareHalo |= (SunPosProj.x >= 0.0f && SunPosProj.x <= 1.0f && SunPosProj.y >= 0.0f && SunPosProj.y <= 1.0f && SunPosProj.z >= 0.0f && SunPosProj.z <= 1.0f);

	        Angle += AngleInc;
	        Test++;
	    }
    	
		if(CalculateSunRaysLensFlareHalo){
			glViewport(0, 0, sunTextureWidth, sunTextureHeight);
			
	        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, sunTextures.get(2), 0);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);
	
	        blurH.bind();
	        glUniform1i(glGetUniformLocation(blurH.i(), "Width"), 1);
	        
	        Rectangle.render(sunTextures.get(0));
	        
	        glUseProgram(0);
	
	        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, sunTextures.get(1), 0);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);
	
	        blurV.bind();
	        glUniform1i(glGetUniformLocation(blurV.i(), "Width"), 1);
	        
	        Rectangle.render(sunTextures.get(2));

	        glUseProgram(0);
	
	        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, sunTextures.get(3), 0);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);
	
	        blurH.bind();
	        glUniform1i(glGetUniformLocation(blurH.i(), "Width"), 10);
	        
	        Rectangle.render(sunTextures.get(0));
	        
	        glUseProgram(0);

	        
	        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, sunTextures.get(2), 0);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);

	        blurV.bind();
	        glUniform1i(glGetUniformLocation(blurV.i(), "Width"), 10);
	        
	        Rectangle.render(sunTextures.get(3));

	        glUseProgram(0);
	
	        
	        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, sunTextures.get(3), 0);
	        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);
	
	        sunRaysLensFlareHalo.bind();
	        glUniform2(glGetUniformLocation(sunRaysLensFlareHalo.i(), "SunPosProj"), lightPosOnScreen.asFlippedFloatBuffer());
	        
	        Rectangle.render(sunTextures.get(1), sunTextures.get(2), dirtTexture);

	        glUseProgram(0);
	
	        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

			glViewport(0, 0, width, height);
	        
	        //DRAW GOD RAYS
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			
	        glEnable(GL_TEXTURE_2D);
	        glColor3f(1.0f, 1.0f, 1.0f);
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
			
			Rectangle.render(sunTextures.get(3));
			
	    	Shader.unbind();

	        glDisable(GL_TEXTURE_2D);
			glDisable(GL_BLEND);
    	}
		return true;
    }
     
    public void SSAOStuff(){
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, SSAOTexture, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);
    	
        SSAO.bind();
        
        Rectangle.render(normalBuffer, depthBuffer, rotationTexture);

        Shader.unbind();

        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }
    
    public void deferredLightingStuff(Set<Lighting> lightSources){
    	//calculate matrices required for lighting
    	Matrix4f modelMatrix = new Matrix4f();
    	modelMatrix.setIdentity();
    	modelMatrix.set(15, 0);
    	//Matrix4f modelViewMatrix = new Matrix4f(cameraViewMatrix).mul(modelMatrix);
    	Matrix4f modelViewMatrix = modelMatrix.mul(new Matrix4f(cameraViewMatrix));
    	//System.out.println(modelViewMatrix);
    	Matrix3f normalMatrix = new Matrix3f(modelViewMatrix).invertGet().transposeGet();
    	//System.out.println(normalMatrix);
    	

    	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
    	glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
    	glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colorBuffer, 0);
    	glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
    	glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);

    	
		lightingAndShadow.bind();
    	
    	glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, colorBuffer);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, normalBuffer);
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, depthBuffer);
        glActiveTexture(GL_TEXTURE3); glBindTexture(GL_TEXTURE_2D, SSAOTexturesBlurred.get(1));
		glActiveTexture(GL_TEXTURE5); glBindTexture(GL_TEXTURE_2D, rotationTexture);
		
		glActiveTexture(GL_TEXTURE6); glBindTexture(GL_TEXTURE_2D, materialAmbient);
		glActiveTexture(GL_TEXTURE7); glBindTexture(GL_TEXTURE_2D, materialDiffuse);
		glActiveTexture(GL_TEXTURE8); glBindTexture(GL_TEXTURE_2D, materialSpecular);
		glActiveTexture(GL_TEXTURE9); glBindTexture(GL_TEXTURE_2D, materialEmission);
		glActiveTexture(GL_TEXTURE10); glBindTexture(GL_TEXTURE_2D, materialShininess);
		
		glUniform1i(lightingAndShadow.uniformLocations[5], filtering ? 1 : 0);
		glUniform1i(lightingAndShadow.uniformLocations[6], occlusion ? 1 : 0);
        glUniformMatrix4(lightingAndShadow.uniformLocations[2], false, viewInverse.asFlippedFloatBuffer());
        
        //other
        glUniformMatrix4(lightingAndShadow.uniformLocations[19], false, normalMatrix.asFlippedFloatBuffer()); //normal
        glUniformMatrix4(lightingAndShadow.uniformLocations[20], false, modelViewMatrix.asFlippedFloatBuffer()); //view
        glUniformMatrix4(lightingAndShadow.uniformLocations[21], false, cameraProjectionMatrix); //projection
		glUniform1f(lightingAndShadow.uniformLocations[23], skyBoxIntensity); //skybox intensity
		
		//count stuff
		int lightCount = 0;
		int directionalLights = 0;
		int pointLights = 0;
		int spotLights = 0;
		
		for(Lighting ls: lightSources){
			if(ls.isEnabled()){
				lightCount++;
				if(ls instanceof DirectionalLighting)
					directionalLights++;
				else if(ls instanceof PointLighting)
					pointLights++;
				else if(ls instanceof SpotLighting)
					spotLights++;
			}
		}

		glUniform1i(lightingAndShadow.uniformLocations[24], lightCount); //light count
		glUniform1i(lightingAndShadow.uniformLocations[4], 0); //assume no light has shadows
		
		FloatBuffer lightPositions = BufferUtils.createFloatBuffer(3 * lightCount);
		FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4 * lightCount);
		FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4 * lightCount);
		FloatBuffer lightSpecular = BufferUtils.createFloatBuffer(4 * lightCount);
		IntBuffer lightType = BufferUtils.createIntBuffer(lightCount);
		FloatBuffer lightConstantAttenuation = BufferUtils.createFloatBuffer(pointLights);
		FloatBuffer lightLinearAttenuation = BufferUtils.createFloatBuffer(pointLights);
		FloatBuffer lightQuadricAttenuation = BufferUtils.createFloatBuffer(pointLights);
		FloatBuffer spotCutoff = BufferUtils.createFloatBuffer(spotLights);
		FloatBuffer spotDirection = BufferUtils.createFloatBuffer(3 * spotLights);
		FloatBuffer spotExponent = BufferUtils.createFloatBuffer(spotLights);
		
		//Shadows
		if(shadowMapper.isShadowEnabled()){
			glUniform1i(lightingAndShadow.uniformLocations[4], 1); //Tell shader shadows are enabled
			glActiveTexture(GL_TEXTURE4); glBindTexture(GL_TEXTURE_2D, shadowMapper.getShadowMap()); //send shadow texture
			glUniformMatrix4(lightingAndShadow.uniformLocations[3], false, shadowMapper.getLightTexture()); //send view texture
	    	shadowMapper.setCaster(null);
		}
		
		//Lights
		Matrix4f viewMatrix = new Matrix4f(cameraViewMatrix);
		Vector3f normal = new Vector3f(-viewMatrix.get(4), -viewMatrix.get(5), -viewMatrix.get(6));
		for(Lighting ls: lightSources){
			if(ls.isEnabled()){
	    		Vector4f pos =  new Vector4f(ls.getPosition(), 1.0f);
	    		pos = viewMatrix.mul(pos);

	    		lightPositions.put(pos.x).put(pos.y).put(pos.z);
	    		Vector4f ambient = ls.getAmbient();
	    		lightAmbient.put(ambient.x).put(ambient.y).put(ambient.z).put(ambient.w);
	    		Vector4f diffuse = ls.getDiffuse();
	    		lightDiffuse.put(diffuse.x).put(diffuse.y).put(diffuse.z).put(diffuse.w);
	    		Vector4f specular = ls.getSpecular();
	    		lightSpecular.put(specular.x).put(specular.y).put(specular.z).put(specular.w);
	    		
	    		if(ls instanceof DirectionalLighting)
	    			lightType.put(0);
	    		else if(ls instanceof PointLighting){
	    			PointLighting pls = (PointLighting)ls;
	    			lightType.put(1);
	    			lightConstantAttenuation.put(pls.getConstantAttenuation());
	    			lightLinearAttenuation.put(pls.getLinearAttenuation());
	    			lightQuadricAttenuation.put(pls.getQuadricAttenuation());
	    		}else if(ls instanceof SpotLighting){
	    			lightType.put(2);
	    			SpotLighting sls = (SpotLighting)ls;
	    			spotCutoff.put(sls.getSpotCutoff());
	    			Vector3f dir = sls.getSpotLightDirection();
	    			spotDirection.put(dir.x).put(dir.y).put(dir.z);
	    			spotExponent.put(sls.getSpotExponent());
	    		}
			}
		}
		
		lightPositions.flip();
		lightAmbient.flip();
		lightDiffuse.flip();
		lightSpecular.flip();
		lightType.flip();
		lightConstantAttenuation.flip();
		lightLinearAttenuation.flip();
		lightQuadricAttenuation.flip();
		spotCutoff.flip();
		spotDirection.flip();
		spotExponent.flip();


		
		glUniform3(lightingAndShadow.uniformLocations[1], lightPositions);
		glUniform3(lightingAndShadow.uniformLocations[7], normal.asFlippedFloatBuffer());
		
		glUniform4(lightingAndShadow.uniformLocations[9], lightAmbient);
		glUniform4(lightingAndShadow.uniformLocations[10], lightDiffuse);
		glUniform4(lightingAndShadow.uniformLocations[11], lightSpecular);
		
		glUniform1(lightingAndShadow.uniformLocations[18], lightType);
		
		glUniform1(lightingAndShadow.uniformLocations[12], lightConstantAttenuation);
		glUniform1(lightingAndShadow.uniformLocations[13], lightLinearAttenuation);
		glUniform1(lightingAndShadow.uniformLocations[14], lightQuadricAttenuation);
		
		glUniform1(lightingAndShadow.uniformLocations[15], spotCutoff);
		glUniform3(lightingAndShadow.uniformLocations[16], spotDirection);
		glUniform1(lightingAndShadow.uniformLocations[17], spotExponent);
		
		Rectangle.render(0);

       	Shader.unbind();
       	
        glActiveTexture(GL_TEXTURE10); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE9); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE8); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE7); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE6); glBindTexture(GL_TEXTURE_2D, 0);
       	
       	glActiveTexture(GL_TEXTURE4); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE5); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE3); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
	    glDisable(GL_BLEND);
    	
      	glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    		
      	
      	
    	glMatrixMode(GL_PROJECTION);
    	glLoadIdentity();
		glOrtho(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);

    	glMatrixMode(GL_MODELVIEW);
    	glLoadIdentity();

    	Rectangle.render(colorBuffer);

    }
    
    public void SSAOFilterStuff(){
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, SSAOTexturesBlurred.get(0), 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);

		SSAOFilterH.bind();
		
		Rectangle.render(SSAOTexture, depthBuffer);

		Shader.unbind();

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		// --------------------------------------------------------------------------------------------------------------------

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, FBO);
		glDrawBuffers(GL_COLOR_ATTACHMENT0_EXT); glReadBuffer(GL_COLOR_ATTACHMENT0_EXT);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, SSAOTexturesBlurred.get(1), 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, 0, 0);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, 0, 0);

 		SSAOFilterV.bind();

 		Rectangle.render(SSAOTexturesBlurred.get(0), depthBuffer);

		Shader.unbind();

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }
    
	public void resized(int displayWidth, int displayHeight){
		width = displayWidth;
		height = displayHeight;
		
		Game.println("Resized to: " + width + " " + height);
		
        //Projection
        glLoadIdentity();
        gluPerspective(Game.fov, (float) width / (float) height, Game.zNear, Game.zFar);
        glGetFloat(GL_MODELVIEW_MATRIX, cameraProjectionMatrix);
		
        /*Matrix4f camInverse = new Matrix4f(cameraProjectionMatrix);
        camInverse.invert();
        camInverse.mul(Matrix4f.biasMatrixInverse);
        
        
        projectionBiasInverse = camInverse.asFlippedFloatBuffer();*/
        
        projectionBiasInverse = Matrix4f.biasMatrixInverse.copy().mul(new Matrix4f(cameraProjectionMatrix).invertGet()).asFlippedFloatBuffer();
        

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
		
		SSAOWidth = width;
		SSAOHeight = height;
		
		glBindTexture(GL_TEXTURE_2D, SSAOTexture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP );
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, SSAOWidth, SSAOHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);

		for(int i = 0; i < 2; i++){
			glBindTexture(GL_TEXTURE_2D, SSAOTexturesBlurred.get(i));
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		
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
        glUniform2f(SSAO.uniformLocations[0], (float)(SSAOWidth) / 64.0f, (float)(SSAOHeight) / 64.0f);
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

	    lightingAndShadow.bind();
		glUniform2f(lightingAndShadow.uniformLocations[22], (float)width / 64.0f, (float)height / 64.0f);
	    glUniformMatrix4(lightingAndShadow.uniformLocations[0], false, projectionBiasInverse);
	    Shader.unbind();
	    
	    
	    sunTextureWidth = width / 2;
	    sunTextureHeight = height / 2;
	    
        glBindTexture(GL_TEXTURE_2D, sunTextures.get(0));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
        glBindTexture(GL_TEXTURE_2D, 0);

	    for(int i = 1; i < 4; i++)
	    {
	        glBindTexture(GL_TEXTURE_2D, sunTextures.get(i));
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, sunTextureWidth, sunTextureHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
	        glBindTexture(GL_TEXTURE_2D, 0);
	    }
	    
	    blurH.bind();
	    glUniform1f(glGetUniformLocation(blurH.i(), "odw"), 1.0f / (float)sunTextureWidth);
	    blurV.bind();
	    glUniform1f(glGetUniformLocation(blurV.i(), "odh"), 1.0f / (float)sunTextureHeight);
	    Shader.unbind();
	}
    
    public void dispose() {
        glDeleteTextures(rotationTexture);
        glDeleteTextures(colorBuffer);
        glDeleteTextures(normalBuffer);
        glDeleteTextures(depthBuffer);
        glDeleteTextures(SSAOTexturesBlurred);
        glDeleteTextures(SSAOTexture);
        glDeleteTextures(sunTextures);
        glDeleteTextures(dirtTexture);
        
        skyBoxModel.dispose();
        
        glDeleteTextures(materialAmbient);
        glDeleteTextures(materialDiffuse);
        glDeleteTextures(materialSpecular);
        glDeleteTextures(materialEmission);
        glDeleteTextures(materialShininess);
        
        glDeleteTextures(combinedLighting);
        glDeleteTextures(combinedSpecular);
    	
        glDeleteFramebuffers(FBO);
    }
    
    public void renderObjects(Set<VisualEntity> entities, boolean depthOnly){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    	if(depthOnly){ //shadows
	        glCullFace(GL_FRONT);
	        Model.setRenderMode(true, false, false, false, false, false); //only vertices
	        for(VisualEntity e: entities){
	        	e.render();
	        }
	        Model.resetRenderMode();
	        glCullFace(GL_BACK);
    	}else{ //normal
    		Model.setRenderMode(true, true, true, (texturing ? true : false), (normalMapping ? true : false), true);
	        for(VisualEntity e: entities){
    			e.render();
	        }
	        Model.resetRenderMode();
    	}
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }

    public Vector2f glToScreen(Vector3f v){
    	IntBuffer viewport = BufferUtils.createIntBuffer(4);
    	viewport.put(0).put(0).put(Game.width).put(Game.height);
    	viewport.flip();
    	FloatBuffer result = BufferUtils.createFloatBuffer(16);
    	GLU.gluProject(v.x, v.y, v.z, cameraViewMatrix, cameraProjectionMatrix, viewport, result); 
    	float[] getResult = new float[16];
    	result.get(getResult);
    	//System.out.println("pos is " + Arrays.toString(getResult));
    	return new Vector2f(getResult[0], getResult[1]);
    }
    
    public Vector3f glToWorld(Vector2f v, float depth){
    	IntBuffer viewport = BufferUtils.createIntBuffer(4);
    	viewport.put(0).put(0).put(Game.width).put(Game.height);
    	viewport.flip();
    	
    	FloatBuffer result = BufferUtils.createFloatBuffer(16);
    	GLU.gluUnProject(v.x, v.y, depth, cameraViewMatrix, cameraProjectionMatrix, viewport, result);
    	float[] getResult = new float[16];
    	result.get(getResult);
    	
    	return new Vector3f(getResult[0], getResult[1], getResult[2]);
    }

    
    
	@Override
	public void checkKeyboardInput(int k) {
		switch(k){
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
			lightScattering = !lightScattering;
			System.out.println("Changed lightScattering to " + lightScattering);
			break;
		case Keyboard.KEY_NUMPAD7:
			System.out.println("Showing colorBuffer");
			showTexture = colorBuffer;
			break;
		case Keyboard.KEY_NUMPAD8:
			System.out.println("Showing normalBuffer");
			showTexture = normalBuffer;
			break;
		case Keyboard.KEY_NUMPAD9:
			System.out.println("Showing depthBuffer");
			showTexture = depthBuffer;
			break;
		case Keyboard.KEY_1:
			System.out.println("Showing materialAmbient");
			showTexture = materialAmbient;
			break;
		case Keyboard.KEY_2:
			System.out.println("Showing materialDiffuse");
			showTexture = materialDiffuse;
			break;
		case Keyboard.KEY_3:
			System.out.println("Showing materialEmission");
			showTexture = materialEmission;
			break;
		case Keyboard.KEY_4:
			System.out.println("Showing materialShininess");
			showTexture = materialShininess;
			break;
		case Keyboard.KEY_5:
			System.out.println("Showing materialSpecular");
			showTexture = materialSpecular;
			break;
		case Keyboard.KEY_NUMPAD5:
			System.out.println("Showing godRaysTexture");
			showTexture = sunTextures.get(0);
			break;
		case Keyboard.KEY_NUMPAD2:
			System.out.println("Showing blurred SSAO");
			showTexture = SSAOTexturesBlurred.get(1); //blurred ssao
			break;
		case Keyboard.KEY_NUMPAD0:
			System.out.println("Showing normal.");
			showTexture = 0;
			break;
		}
		
	}

	public void perspective3D(){
	    glMatrixMode(GL_PROJECTION);
	    glLoadMatrix(cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(cameraViewMatrix);
        
        glViewport(0, 0, width, height);
	}
	
	@Override
	public void checkMouseInput(int m) {
		// TODO Auto-generated method stub
		
	}

	private int axesLength = 2000;
	public void renderAxes(){

		
		glColor3f(1f, 0f, 0f);
        glLineWidth(1);
        glBegin(GL_LINES);
        glVertex3f(-axesLength, 0, 0);
        glVertex3f(0, 0, 0);
        glEnd();
        glLineWidth(3);
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(axesLength, 0, 0);
        glEnd();
        glLineWidth(1);
		glColor3f(0f, 1f, 0f);
        glBegin(GL_LINES);
        glVertex3f(0,-axesLength, 0);
        glVertex3f(0,0, 0);
        glEnd();
        glLineWidth(3);
        glBegin(GL_LINES);
        glVertex3f(0,0, 0);
        glVertex3f(0,axesLength, 0);
        glEnd();
		glColor3f(0f, 0f, 1f);
        glLineWidth(1);
        glBegin(GL_LINES);
        glVertex3f(0, 0, -axesLength);
        glVertex3f(0, 0, 0);
        glEnd();
        glLineWidth(3);
        glBegin(GL_LINES);
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, axesLength);
        glEnd();
        glLineWidth(1);
	}
}
