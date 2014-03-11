package blender.model;

import static org.lwjgl.opengl.GL11.*;
import game.world.World;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;

import blender.model.custom.Sphere;

import com.bulletphysics.linearmath.QuaternionUtil;

import shader.Shader;
import utils.Utils;
import utils.math.Transform;
import utils.math.Vector2f;
import utils.math.Vector3f;

public class Model{
	
	public List<SubModel> submodels = new ArrayList<SubModel>();
	
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texCoords = new ArrayList<Vector2f>();
    public HashMap<String, Material> materials = new HashMap<String, Material>();
    
    private String pathf; //model .obj directory
    
	public Transform offset;
	
	public boolean quadFaces = false;
	private boolean initialized = false;
	public boolean isTextured = false; //All submodels textured?
    public boolean isGodRays = false; //create light scattering
    
	
    public Model(){}
	
	public Model(String pathf){
		this.pathf = pathf;
		loadModel();
	}
	
	public Model(String pathf, boolean load){
		this.pathf = pathf;
		if(load)
			loadModel();
	}
	
	public void loadModel(){
		try {
			OBJLoader.loadModel(pathf, this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void scale(float s){
		if(!initialized){ //can still modify vertices
			for(Vector3f v: vertices){
				v.mul(s);
			}
		}else{
			if(offset == null){
				offset = new Transform();
				offset.setIdentity();
			}
			offset.scale(s);
		}	
	}
	
	public void translate(Vector3f v2){
		if(!initialized){
			for(Vector3f v: vertices){
				v.add(v2);
			}
		}else{
			if(offset == null){
				offset = new Transform();
				offset.setIdentity();
			}
			offset.origin.add(v2);
		}
	}
	
	public void setRotation(Quat4f q){
		if(offset == null){
			offset = new Transform();
			offset.setIdentity();
		}
		offset.setRotation(q);
	}
	
	public void render(){
		if(offset != null){
			float[] f = new float[16];

			offset.getOpenGLMatrix(f);
			
			FloatBuffer fb = BufferUtils.createFloatBuffer(16);
			fb.put(f);
			fb.rewind();
			
			glMultMatrix(fb);
		}

		renderSubModels();
	}
	
	public void renderSubModels(){
		for(SubModel m: submodels){
			m.render();
		}
	}

	public void prepareVBO(){
		boolean allTex = true;
		for(SubModel m: submodels){
			m.prepareVBO();
			if(!m.isTextured)
				allTex = false;
		}
		if(allTex)
			isTextured = true;
		initialized = true; //vbo created
	}
	
	public void dispose(){
		for(SubModel m: submodels){
			m.dispose();
		}
	}
	
	private static boolean drawVertices = true, drawNormals = true, drawColors = true, drawTextures = true, normalMapping = true, drawMaterial = true;
	public static void setRenderMode(boolean vertices, boolean normals, boolean colors, boolean textures, boolean nMapping, boolean dMaterial){
		drawVertices = vertices; drawNormals = normals; drawColors = colors; drawTextures = textures; normalMapping =  nMapping; drawMaterial = dMaterial;
	}
	
	public static void resetRenderMode(){
		drawVertices = true; drawNormals = true; drawColors = true; drawTextures = true; normalMapping = true; drawMaterial = true;
	}

	public static boolean drawVertices() {
		return drawVertices;
	}

	public static boolean drawNormals() {
		return drawNormals;
	}

	public static boolean drawColors() {
		return drawColors;
	}

	public static boolean drawTextures() {
		return drawTextures;
	}

	public static boolean drawNormalMapping() {
		return normalMapping;
	}
	
	public static boolean drawMaterial(){
		return drawMaterial;
	}

}
