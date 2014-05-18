package graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_EMISSION;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SHININESS;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColorMaterial;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE10;
import static org.lwjgl.opengl.GL13.GL_TEXTURE11;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.GL_TEXTURE7;
import static org.lwjgl.opengl.GL13.GL_TEXTURE8;
import static org.lwjgl.opengl.GL13.GL_TEXTURE9;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT5;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT6;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT7;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import input.InputConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Properties;

import main.Config;
import main.PlayState;
import main.state.Game;
import main.state.RenderState;
import math.Matrix3f;
import math.Matrix4f;
import math.Vector3f;
import math.Vector4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;

import resources.Resources;
import resources.model.Model;
import resources.model.SubModel;
import resources.model.custom.Sphere;
import resources.texture.Texture;
import shader.Shader;
import utils.ArrayList;
import utils.Utils;
import entity.creation.Controller;
import entity.creation.SunLight;
import entity.sheet.DirectionalLighting;
import entity.sheet.Lighting;
import entity.sheet.PointLighting;
import entity.sheet.SpotLighting;
import entity.sheet.VisualEntity;
import entitymanager.EntityManager;
import entitymanager.EntitySelectionRequest;

public class Graphics3D{

	//Store materials, bind textures, normal mapping
    public static Shader preprocess = new Shader();
    
    //Lighting and shadows
    private static Shader lightingAndShadow = new Shader(); 
    private static ShadowMapping shadowMapper = new ShadowMapper();
    private static ShadowMapping shadowMapperCube = new ShadowMapperCube();
    
    //Screen space ambient occlusion
    private static Shader SSAO = new Shader(); 
    private static Shader SSAOFilterH = new Shader(); //SSAO Horizontal filtering
    private static Shader SSAOFilterV = new Shader(); //SSAO Vertical filtering
    public static IntBuffer SSAOTexturesBlurred;
    private static int SSAOTexture; //without blur
    private static int rotationTexture; //SSAO randomly rotated texture
    private static int SSAOWidth, SSAOHeight;
    
    //Light scattering, Lens, Flare and Halo
    private static Shader sunRaysLensFlareHalo = new Shader(); //Light scattering, Lens, Flare and Halo
    private static Shader blurH = new Shader(); //Sun blurring
    private static Shader blurV = new Shader(); //Vertical
    public static IntBuffer sunTextures; //textures required for blurring and light scattering
    private static int sunTextureWidth, sunTextureHeight;
    private static int dirtTexture;
    
    //SkyBox
    static Shader skyBoxShader = new Shader();
    static SubModel skyBoxModel = new SubModel();
    static int skyVAO;
    private static float skyBoxIntensity = .4f;
    
    //Framebuffer object
    private static int FBO;
    
    //Texture indicies
    public static int colorBuffer, normalBuffer, depthBuffer, combinedLighting, combinedSpecular; //for deferred shading
    public static int materialAmbient, materialDiffuse, materialSpecular, materialEmission, materialShininess;

    //Rendering settings
    private static int width, height, fov;
    public static boolean  texturing = true;
    public static boolean normalMapping = true;
    public static boolean shadows = true; 
    public static boolean filtering = true;
    public static boolean occlusion = true;
    public static boolean lightScattering = true;
    public static int showTexture;
    
    //Other
    private static FloatBuffer projectionBiasInverse = BufferUtils.createFloatBuffer(16);
    private static Matrix4f viewInverse = new Matrix4f();
    public static FloatBuffer cameraProjectionMatrix = BufferUtils.createFloatBuffer(16);
    public static FloatBuffer cameraViewMatrix = BufferUtils.createFloatBuffer(16);
    

    
	public static void init(){
		InputStream input = null;
		Properties prop = new Properties();
		try{
			input = new FileInputStream("res/config/config.properties");
			
			prop.load(input);
			
			
			fov = Integer.parseInt(prop.getProperty("FOV"));
			width = Integer.parseInt(prop.getProperty("Resolution").split("x")[0]);
			height = Integer.parseInt(prop.getProperty("Resolution").split("x")[1]);
			shadows = Boolean.parseBoolean(prop.getProperty("Shadows"));
			filtering = Boolean.parseBoolean(prop.getProperty("Shadow_Filtering"));
			occlusion = Boolean.parseBoolean(prop.getProperty("Ambient_Occlusion"));
			lightScattering = Boolean.parseBoolean(prop.getProperty("Light_Scattering"));
			normalMapping = Boolean.parseBoolean(prop.getProperty("Normal_Mapping"));
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
        skyBoxModel.material.texture = skyBox;
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
	    
	    //shadowMapperNormal.init();
	    shadowMapper.init();
	    shadowMapperCube.init();
	}
	
	private static void prepareFiltering(){
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
	
    private static void prepareShaders(){
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
	    
    	preprocess.attribLocations = new int[5];
        preprocess.attribLocations[0] = glGetAttribLocation(preprocess.i(), "vert_Tangent");
        preprocess.attribLocations[1] = glGetAttribLocation(preprocess.i(), "Material_Ambient");
        preprocess.attribLocations[2] = glGetAttribLocation(preprocess.i(), "Material_Specular");
        preprocess.attribLocations[3] = glGetAttribLocation(preprocess.i(), "Material_Emission_Shininess");
        
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

        lightingAndShadow.uniformLocations = new int[27];
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
        
        lightingAndShadow.uniformLocations[25] = glGetUniformLocation(lightingAndShadow.i(), "CubeShadowedLight");
        lightingAndShadow.uniformLocations[26] = glGetUniformLocation(lightingAndShadow.i(), "DirectionalShadowEnabled");
        
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
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "ShadowMap"), 4);
    	glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "RotationTexture"), 5);

        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialAmbient"), 6);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialDiffuse"), 7);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialSpecular"), 8);
        glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialEmission"), 9);
    	glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "MaterialShininess"), 10);
    	
    	
    	glUniform1i(glGetUniformLocation(lightingAndShadow.i(), "ShadowCubeMap"), 11);
        Shader.unbind();   
    }

    
    public static VisualEntity selectObject(int screenX, int screenY, ArrayList<VisualEntity> objects){
    	if(objects.size() <= 0)
    		return null;
    	
    	glClearColor(1, 1, 1, 1);
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    	Model.setRenderMode(true, false, false, false, false, false);
    	for(int i = 0; i < objects.size(); i++){
    		float red = (float)i%256;
    		float green = (int)((float)i/256)%256;
    		float blue = (int)((float)i/256/256)%256;
    		glColor3f(red/255, green/255, blue/255);
    		
    		objects.get(i).render();
    	}
    	Model.resetRenderMode();
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

    	//Get the pixel
    	FloatBuffer pixelColors = BufferUtils.createFloatBuffer(3*Float.SIZE);
    	glReadPixels(screenX, screenY, 1, 1, GL_RGB, GL_FLOAT, pixelColors);

    	int index = (int)(pixelColors.get(2)*256*256*255 + pixelColors.get(1)*256*255 + pixelColors.get(0)*255);
    	
    	//if selected something
    	if(index != 16777215){
    		return objects.get(index);
    	}
    	
    	return null;
    }
    
    private static boolean trsv = false;
    private static boolean trsv2 = false;
    static EntitySelectionRequest tempReq; 
    public static void render(EntityManager world){
    	if(world.getState().getCamera() == null) //wait for controller to be ready
    		return;
    	
		//Vector3f proj = new Vector3f(Mouse.getX(),Mouse.getY(),50);
		//System.out.println(proj + " -> " + glToWorld(new Vector2f(proj.x, proj.y), proj.z));
    	
    	/*for(KbEvent event : world.getState().getInput().keyboardEvents.rendering()){
    		if(event.state)
    			checkKeyboardInput(event.key);
    	}
    	
    	for(MsEvent event : world.getState().getInput().mouseEvents.rendering()){
    		if(event.state)
    			checkMouseInput(event.button);
    	}*/
    	
    	

    	cameraViewMatrix =  world.getState().getCamera().getOpenGLView(RenderState.rendering()).fb();//.view.fb();
    	
    	//System.out.println("view is " + Utils.getFB(cameraViewMatrix) + " of " + RenderState.rendering());
    	//Random input
    	//moveLight(world.getLightingEntities());<
    	//cameraProjectionMatrix = world.getState().getCamera().projection.fb();
    	
    	// 1st pass - render scene to textures ------------------------------------------------------------------------------------
	    glMatrixMode(GL_PROJECTION);
	    glLoadMatrix(cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(cameraViewMatrix);
        
        glViewport(0, 0, width, height);
        
        EntitySelectionRequest eReq = null;
        if((eReq = world.getEntitySelectionRequests().poll()) != null){
        	float pixelx = (float)eReq.getScreenX()/Display.getWidth()*world.getState().getCamera().viewportWidth;
        	float pixely = (float)eReq.getScreenY()/Display.getHeight()*world.getState().getCamera().viewportHeight;

        	VisualEntity selectedEntity = selectObject((int)pixelx, (int)pixely, world.getState().getCamera().cameraFrustum.rendering().getInsideFrustumEntities());
        	eReq.setEntity(selectedEntity);

			FloatBuffer depthValue = BufferUtils.createFloatBuffer(1*Float.SIZE);
			
			glReadPixels((int)pixelx, (int)pixely, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, depthValue);
			
			eReq.setDepth(depthValue.get(0));
			
			tempReq = eReq;
			eReq.getSelectionHandler().requestDone(eReq);
	    }
        //VisualEntity selectedEntity = selectObject(Mouse.getX(), Mouse.getY(), world.getState().getCamera().cameraFrustum.rendering().getInsideFrustumEntities());
        //System.out.println("selected " + selectedEntity);
        
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
	    
	    Vector3f skyPos = world.getState().getCamera().getPosition(RenderState.rendering()).copy();

	    glUniform3(skyBoxShader.uniformLocations[0], skyPos.fb());
	    glUniformMatrix4(skyBoxShader.uniformLocations[1], false, viewProjectionMatrix.fb());
	    glBindTexture(GL_TEXTURE_CUBE_MAP, skyBoxModel.material.texture.getID());
	    glBindVertexArray(skyVAO);
	    glDrawArrays(GL_TRIANGLES, 0, 36);
	    glBindVertexArray(0);
	    glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
	    Shader.unbind();
	    
	    //OTHER
	    preprocess.bind();
	    
	    for(Lighting ls: world.getLightingEntities(RenderState.rendering()))
	    	if(ls instanceof SunLight){
	    		((SunLight)ls).render();
	    		break;
	    	}
    	
    	//glDisable(GL_BLEND);
	    //world.getVisualEntities(RenderState.rendering())
        renderObjects(world.getState().getCamera().cameraFrustum.rendering().getInsideFrustumEntities(), false);
        
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
	    	viewInverse = new Matrix4f(cameraViewMatrix).inv();
	    	
	    	for(Lighting ls: world.getLightingEntities(RenderState.rendering())){
	    		if(ls instanceof SunLight){
	    			if(ls.isEnabled() && ls.isShadowed()){
	    				shadowMapper.setParent(ls);

	    			}
	    		}
	    		if(ls instanceof PointLighting){
	    			if(ls.isEnabled() && ls.isShadowed()){
	    				shadowMapperCube.setParent(ls);
	    			}
	    		}
	    	}
	    	if(shadowMapper.isEnabled() && shadowMapper.getParent() != null)
	    		 shadowMapper.render(world.getState().getCamera(), 
	    				 world.getVisualEntities(RenderState.rendering()), FBO);
	    	if(shadowMapperCube.getParent() != null && shadowMapperCube.isEnabled())
	    		shadowMapperCube.render(world.getState().getCamera(), 
	    				 world.getVisualEntities(RenderState.rendering()), FBO);
	        glViewport(0, 0, width,  height);
	    }
	    
	    //occlusion, using data from 1st pass  -------------------------------------------------------------------------------
	    if(occlusion){
	    	glViewport(0, 0, SSAOWidth, SSAOHeight);
	    	SSAOStuff();
	        glViewport(0, 0, width,  height);
	        SSAOFilterStuff();
	    }

	    // apply shadows and lightin, 2nd pass ---------------------------------------------------------------
	    //oldDeferredLightingStuff(lightSources);
	    
	    deferredLightingStuff(world);

	    
		// applying light scattering to lighting stuff
		if(lightScattering){
			
			SunLight sun = null;
			for(Lighting e : world.getLightingEntities(RenderState.rendering())){
				if(e instanceof SunLight){
					sun = (SunLight)e;
				}
			}
			sunRaysLensFlareHaloStuff(sun, world);
		}
		
		//Debbuging textures
	    showTextures();
    }

	
	private static void showTextures(){
		if(showTexture != 0){
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			
			Rectangle.render(showTexture);
		}
	}
	
    public static boolean sunRaysLensFlareHaloStuff(SunLight sun, EntityManager world){
    	if(sun == null)
    		return false;
	    boolean CalculateSunRaysLensFlareHalo = false;
		Vector3f lightPosOnScreen = sun.getPosition(RenderState.rendering()).copy();
	    world.getState().getCamera().project(lightPosOnScreen, RenderState.rendering());
    	lightPosOnScreen = new Vector3f(lightPosOnScreen.x/Display.getWidth(),lightPosOnScreen.y/Display.getHeight(), 0);
	    int Test = 0, Tests = 16;
	    float Angle = 0.0f, AngleInc = 360.0f / Tests;
	    Matrix4f biasMatrix = Matrix4f.BIASMATRIX.copy();
	   // Matrix4f VPB = biasMatrix.mulReverse(new Matrix4f(cameraProjectionMatrix)).mulReverse(new Matrix4f(cameraViewMatrix));
	    Matrix4f VPB = new Matrix4f(cameraViewMatrix).mul(new Matrix4f(cameraProjectionMatrix)).mul(biasMatrix);
	    Controller cam = world.getState().getCamera();
	    while(Test < Tests && !CalculateSunRaysLensFlareHalo){
	    	Vector3f temp2 = cam.getRightVector(RenderState.rendering()).copy().rotateGet(Angle, cam.getViewRay(RenderState.rendering())).scl(sun.getRadius()* 1.3f);//Utils.rotate(cam.getRightVector(), Angle, cam.getViewRay()).mul(8.75f * 1.3f);
	    	Vector4f temp = new Vector4f(sun.getPosition(RenderState.rendering()).copy().add(temp2), 1.0f);
	    	
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
	        glUniform2(glGetUniformLocation(sunRaysLensFlareHalo.i(), "SunPosProj"), lightPosOnScreen.fb());
	        
	        Rectangle.render(sunTextures.get(1), sunTextures.get(2), dirtTexture);

	        glUseProgram(0);
	
	        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		    glViewport(0, 0, Display.getWidth(), Display.getHeight());
	        
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
     
    private static void SSAOStuff(){
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
    
    private static void deferredLightingStuff(EntityManager world){
    	//calculate matrices required for lighting
    	Matrix4f modelMatrix = new Matrix4f();
    	modelMatrix.setIdentity();
    	modelMatrix.set(15, 0);
    	//Matrix4f modelViewMatrix = new Matrix4f(cameraViewMatrix).mul(modelMatrix);
    	Matrix4f modelViewMatrix = modelMatrix.mul(new Matrix4f(cameraViewMatrix));
    	//System.out.println(modelViewMatrix)
    	Matrix3f normalMatrix = new Matrix3f(modelViewMatrix).inv().trans();
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
        glUniformMatrix4(lightingAndShadow.uniformLocations[2], false, viewInverse.fb());
        
        //other
        glUniformMatrix4(lightingAndShadow.uniformLocations[19], false, normalMatrix.fb()); //normal
        glUniformMatrix4(lightingAndShadow.uniformLocations[20], false, modelViewMatrix.fb()); //view
        glUniformMatrix4(lightingAndShadow.uniformLocations[21], false, cameraProjectionMatrix); //projection
		glUniform1f(lightingAndShadow.uniformLocations[23], skyBoxIntensity); //skybox intensity
		
		//count stuff
		int lightCount = 0;
		int directionalLights = 0;
		int pointLights = 0;
		int spotLights = 0;
		
		for(Lighting ls: world.getLightingEntities(RenderState.rendering())){
			if(ls.isEnabled()){
				lightCount++;
				if(ls instanceof DirectionalLighting){
					directionalLights++;
				}
				else if(ls instanceof PointLighting){
					pointLights++;
				}
				else if(ls instanceof SpotLighting){
					spotLights++;
				}
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
		
		FloatBuffer shadowLightTex = BufferUtils.createFloatBuffer(16*7);
		
		if(shadows){
			if(shadowMapper.getParent() != null && shadowMapper.isEnabled()){
				glUniform1i(lightingAndShadow.uniformLocations[4], 1); //Tell shader shadows are enabled
				glUniform1i(lightingAndShadow.uniformLocations[26], 1); //Tell shader directional shadows are enabled
				glActiveTexture(GL_TEXTURE4); glBindTexture(GL_TEXTURE_2D, shadowMapper.getShadowMap()); //send shadow texture
				shadowLightTex.put(shadowMapper.getLightTexture());
				shadowMapper.setParent(null);
			}else{
				shadowLightTex.position(16);
			}
		}
		
		//Lights
		Matrix4f viewMatrix = new Matrix4f(cameraViewMatrix);
		Vector3f normal = new Vector3f(-viewMatrix.get(4), -viewMatrix.get(5), -viewMatrix.get(6));
		int i = 0;
		boolean foundcubelight = false;
		for(Lighting ls: world.getLightingEntities(RenderState.rendering())){
			if(ls.isEnabled()){
	    		Vector4f pos =  new Vector4f(ls.getPosition(RenderState.rendering()), 1.0f);
	    		pos = viewMatrix.mul(pos);

	    		lightPositions.put(pos.x).put(pos.y).put(pos.z);
	    		Vector4f ambient = ls.getAmbient();
	    		lightAmbient.put(ambient.x).put(ambient.y).put(ambient.z).put(ambient.w);
	    		Vector4f diffuse = ls.getDiffuse();
	    		lightDiffuse.put(diffuse.x).put(diffuse.y).put(diffuse.z).put(diffuse.w);
	    		Vector4f specular = ls.getSpecular();
	    		lightSpecular.put(specular.x).put(specular.y).put(specular.z).put(specular.w);
	    		
	    		if(ls instanceof DirectionalLighting){
	    			lightType.put(0);
	    		}else if(ls instanceof PointLighting){
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
	    		
	    		
	    		if(shadowMapperCube.getParent() != null && !foundcubelight && shadowMapperCube.isEnabled()){
	    			if(shadowMapperCube.getParent() == ls){
	    				glUniform1i(lightingAndShadow.uniformLocations[25], i); //Tell shader cube shadows are enabled
	    				foundcubelight = true;
	    			}
	    		}
	    		i++;
			}
		}
		
		if(!foundcubelight)
			glUniform1i(lightingAndShadow.uniformLocations[25], -1); //Tell shader cube shadows are enabled

		if(shadowMapperCube.getParent() != null && shadowMapperCube.isEnabled() && foundcubelight){
			glUniform1i(lightingAndShadow.uniformLocations[4], 1); //Tell shader shadows are enabled
			glActiveTexture(GL_TEXTURE11); glBindTexture(GL_TEXTURE_2D_ARRAY, shadowMapperCube.getShadowMap()); //send shadow texture
			shadowLightTex.put(shadowMapperCube.getLightTexture());
			shadowMapperCube.setParent(null);
		}
		//System.out.println(Utils.getFB(Utils.combineFloatBuffers(shadowLightTex)));

		shadowLightTex.flip();
		glUniformMatrix4(lightingAndShadow.uniformLocations[3], false, shadowLightTex); //send view texture
		
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
		glUniform3(lightingAndShadow.uniformLocations[7], normal.fb());
		
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
       	
        glActiveTexture(GL_TEXTURE11); glBindTexture(GL_TEXTURE_2D, 0);
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
    		
	    glViewport(0, 0, Display.getWidth(), Display.getHeight());
      	
    	glMatrixMode(GL_PROJECTION);
    	glLoadIdentity();
		glOrtho(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);

    	glMatrixMode(GL_MODELVIEW);
    	glLoadIdentity();

    	Rectangle.render(colorBuffer);

    }
    
    private static void SSAOFilterStuff(){
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
    
	public static void resized(int displayWidth, int displayHeight){
		width = displayWidth;
		height = displayHeight;
		
		Game.println("Resized to: " + width + " " + height);
        
        cameraProjectionMatrix = Matrix4f.perspectiveMatrix(fov, (float) width / (float) height, Config.Z_NEAR, Config.Z_FAR).fb();
        

        
        projectionBiasInverse = Matrix4f.BIASMATRIXINV.copy().mul(new Matrix4f(cameraProjectionMatrix).inv()).fb();
        

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
		
		SSAOWidth = width / 1;
		SSAOHeight = height / 1;
		
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
    
    public static void dispose() {
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
        
        shadowMapper.dispose();
        shadowMapperCube.dispose();
    }
    
    public static void renderObjects(ArrayList<VisualEntity> entities, boolean depthOnly){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    	if(depthOnly){ //shadows
	        glCullFace(GL_FRONT);
	        Model.setRenderMode(true, false, false, false, false, false); //only vertices
	        for(VisualEntity e: entities){
	        	if(!e.isShadowEnabled()){
	        		continue;
	        	}
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

	public static  void perspective3D(int width, int height){
	    glMatrixMode(GL_PROJECTION);
	    glLoadMatrix(cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(cameraViewMatrix);
        
        glViewport(0, 0, width, height);
	}
	

    static Sphere s = new Sphere(2, 16, 16);
	
	private static int axesLength = 2000;
	public static void renderAxes(PlayState state){

		
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
       
        glLineWidth(5);
		glColor3f(1f, 1f, 1f);
		
		/*if(tempReq != null){
			//System.out.println("proj " + new Matrix4f(cameraProjectionMatrix));
			//System.out.println("view " + new Matrix4f(cameraViewMatrix));
			
			//origin = glToWorld(new Vector2f(tempReq.getScreenX(), tempReq.getScreenY()), tempReq.getDepth());
			//direction = glToWorld(new Vector2f(tempReq.getScreenX(), tempReq.getScreenY()), 0);
			
			//System.out.println("proj " + state.getCamera().getProjectionMatrix());
			//System.out.println("view " + state.getCamera().getOpenGLView(RenderState.rendering()));

			origin = state.getCamera().unproject(new Vector3f(tempReq.getScreenX(), tempReq.getScreenY(), tempReq.getDepth()));
			direction = state.getCamera().unproject(new Vector3f(tempReq.getScreenX(), tempReq.getScreenY(), 0));

			tempReq = null;
		}
		
		 glBegin(GL_LINES);
	        glVertex3f(origin.x, 
	        		origin.y,
	        		origin.z);
	        glVertex3f(direction.x, 
	        		direction.y,
	        		direction.z);
	     glEnd();*/
        
       /* Ray ray = state.getCamera().getPickRay(Mouse.getX()+10, Mouse.getY()+10);
        float len = 50;
        
        System.out.println(ray.origin + " " + ray.direction);
        
        glBegin(GL_LINES);
        glVertex3f(ray.origin.x, 
        		ray.origin.y,
        		ray.origin.z);
        glVertex3f(ray.origin.x+len*ray.direction.x, 
        		ray.origin.y+len*ray.direction.y,
        		ray.origin.z+len*ray.direction.z);
        glEnd();*/




	}
}
