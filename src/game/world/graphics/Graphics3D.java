package game.world.graphics;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrix;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import game.Game;
import game.world.World;
import game.world.entities.DefaultEntity;
import game.world.entities.Entity;

import java.nio.FloatBuffer;
import java.util.List;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import utils.Utils;
import utils.math.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import controller.Camera;


/**
 * http://www.belanecbn.sk/3dtutorials/index.php?id=11
 */
public class Graphics3D {
	
	private static boolean polygonMode = false; 
	private static boolean enableLighting = true;
	private static boolean enableShadows = true;
	
	public static Vector3f sceneOrigin = new Vector3f();

    public static FloatBuffer cameraProjectionMatrix = BufferUtils.createFloatBuffer(16);
    public static FloatBuffer cameraViewMatrix = BufferUtils.createFloatBuffer(16);
    public static FloatBuffer lightProjectionMatrix = BufferUtils.createFloatBuffer(16);
    public static FloatBuffer lightViewMatrix = BufferUtils.createFloatBuffer(16);
    
    public static Vector3f lightPos = new Vector3f(20,50f,20f);
    public static float sceneBoundingRadius = 50; //light view radius
    
    private static ShadowType shadowType = ShadowType.CUBE;
    
    private static enum ShadowType{HARD, SOFT, CUBE, DEFERREDLIGHTPOINT};
    
	public static void init(){
		//Prepare shadow mappig
		if(shadowType == ShadowType.SOFT)
			SoftPenumbraShadows.init();
		else if(shadowType == ShadowType.HARD)
			HardShadows.init();
		else if(shadowType == ShadowType.CUBE)
			CubeShadows.init();
		else if(shadowType == ShadowType.DEFERREDLIGHTPOINT){
			DeferredLightPoints.init();
		}
	}
    
	public static Camera camera = new Camera();
	public static void updateMatrices(Camera cam){
		camera = cam;
        cameraProjectionMatrix.rewind();
        cameraViewMatrix.rewind();
        lightProjectionMatrix.rewind();
        lightViewMatrix.rewind();
        
    	//Calculate & save matrices
        glPushMatrix();

        //Camera
        glLoadIdentity();
        gluPerspective(Game.fov, (float) Game.width / (float) Game.height, Game.zNear, Game.zFar);
        glGetFloat(GL_MODELVIEW_MATRIX, cameraProjectionMatrix);
        
        glLoadIdentity();
        cam.lookAt();
        glGetFloat(GL_MODELVIEW_MATRIX, cameraViewMatrix);

        //Light
        glLoadIdentity();
        Vector3f sceneOrigin = new Vector3f(0,0,0);
        //sceneOrigin = new Vector3f(cam.getPos().x,0,cam.getPos().z);
        Vector3f savedSceneOrigin = new Vector3f(sceneOrigin.x, sceneOrigin.y, sceneOrigin.z);
        sceneOrigin.negate();
        sceneOrigin.add(lightPos);
        //Set light perspective
        float lightToSceneDistance = sceneOrigin.length();
        
        float nearPlane = lightToSceneDistance - sceneBoundingRadius;
        if (nearPlane < 0) {
            System.err.println("Camera is too close to scene. A valid shadow map cannot be generated.");
        }
        
        float fieldOfView = (float) Math.toDegrees(2.0F * Math.atan(sceneBoundingRadius / lightToSceneDistance));
        glLoadIdentity();
        gluPerspective(fieldOfView, 1.0f, nearPlane, nearPlane + (2.0F * sceneBoundingRadius));
        glGetFloat(GL_MODELVIEW_MATRIX, lightProjectionMatrix);

        glLoadIdentity();
        gluLookAt(lightPos.x, lightPos.y, lightPos.z,
        savedSceneOrigin.x, savedSceneOrigin.y, savedSceneOrigin.z,
        0.0f, 1.0f, 0.0f);
        glGetFloat(GL_MODELVIEW_MATRIX, lightViewMatrix);

        glPopMatrix();
	}
	
	public static boolean first = true;
    public static Entity camBall = new DefaultEntity();
	public static void someCheck(World world){
		if(first){
			//visual
			//Model testModel = new Sphere(0.f,30,30);
			//camBall.setModel(testModel);
			
			CollisionShape shape = new SphereShape(3.0f);
			DefaultMotionState motionState = new DefaultMotionState(new Transform(new javax.vecmath.Matrix4f(
					new Quat4f(0,0,0,1),
					new javax.vecmath.Vector3f(0,0,0), 1)));
			javax.vecmath.Vector3f intertia = new javax.vecmath.Vector3f();
			shape.calculateLocalInertia(1.0f,  intertia);
			RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1.0f, motionState, shape, intertia);
			constructionInfo.restitution = 0.75f;
			constructionInfo.angularDamping = 0.95f;
			RigidBody body = new RigidBody(constructionInfo);
			camBall.setRigidBody(body);
			body.setCollisionFlags(CollisionFlags.NO_CONTACT_RESPONSE);
			body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
			world.addEntity(camBall);
			body.setGravity(new javax.vecmath.Vector3f(0,0,0));
			first = false;
		}
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
    	lightPos = new Vector3f(50+dx, 50+dy, 50+dz);
       		
    	
    	Transform t = new Transform();
        camBall.getRigidBody().getWorldTransform(t);
        t.origin.set(new javax.vecmath.Vector3f(lightPos.x, lightPos.y, lightPos.z));
        camBall.getRigidBody().setWorldTransform(t);
	}

	///float val = -0.0f;
	private static float dx, dz, dy;
    /** Render the scene, and then update. */
    public static void render(List<Entity> entities, World world) {
    	someCheck(world);
		if(polygonMode)
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    	if(enableShadows){
    		if(shadowType == ShadowType.SOFT)
    			SoftPenumbraShadows.render(entities);
    		else if(shadowType == ShadowType.HARD)
    			HardShadows.render(entities);
    		else if(shadowType == ShadowType.CUBE)
    			CubeShadows.render(entities);
    		else if(shadowType == ShadowType.DEFERREDLIGHTPOINT){
    			DeferredLightPoints.render(entities);
    		}
    	}else{
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            cameraPerspective();

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glEnable(GL_TEXTURE_2D);
            if(enableLighting){
    	    	glEnable(GL_LIGHTING);
                glLight(GL_LIGHT0, GL_POSITION, Utils.asFlippedFloatBuffer(lightPos.x, lightPos.y, lightPos.z, 1.0f));
            	glEnable(GL_LIGHT0);
            }
            
    		renderObjects(entities);
    		
    		
            if(enableLighting){
    	    	glDisable(GL_LIGHTING);
            	glDisable(GL_LIGHT0);
            }
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
    	}
		if(polygonMode)
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    		
    }
    
	private static void renderObjects(List<Entity> entities) {
    	for(Entity e: entities){
    		e.drawTexture(true);
    		e.render();
    	}
    }
   
    /** Cleanup after the program. */
    public static void dispose() {
		if(shadowType == ShadowType.SOFT)
			SoftPenumbraShadows.dispose();
		else if(shadowType == ShadowType.HARD)
			HardShadows.dispose();
		else if(shadowType == ShadowType.CUBE)
			CubeShadows.dispose();
		else if(shadowType == ShadowType.DEFERREDLIGHTPOINT)
			DeferredLightPoints.dispose();;
    }
	
	public static void cameraPerspective(){
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(Graphics3D.cameraProjectionMatrix);

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(Graphics3D.cameraViewMatrix);
	}
	
	public static void perspective2D(){   
	    glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
	    GLU.gluOrtho2D(0.0f, (float)Game.width, (float)Game.height, 0.0f);
	    glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
	}
			


}
