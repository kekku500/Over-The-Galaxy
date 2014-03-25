package blender.model;

import static org.lwjgl.opengl.GL11.*;
import game.world.World;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Quat4f;

import org.lwjgl.BufferUtils;

import blender.model.custom.Sphere;

import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.util.ObjectArrayList;

import shader.Shader;
import utils.Utils;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector2f;
import utils.math.Vector3f;

public class Model{
	
	public List<SubModel> submodels = new ArrayList<SubModel>();
	
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texCoords = new ArrayList<Vector2f>();
    public HashMap<String, Material> materials = new HashMap<String, Material>();
    
    public String modelPath; //model .obj directory
	
	public boolean VBOCreated = false;
	public boolean isTextured = false; //All submodels textured?
    public boolean isGodRays = false; //create light scattering
    
	
    public Model(){}
	
	public Model(String pathf){
		this.modelPath = pathf;
		loadModel();
	}
	
	public Model(String pathf, boolean load){
		this.modelPath = pathf;
		if(load)
			loadModel();
	}
	
	public FloatBuffer getVertices(){
		FloatBuffer b = BufferUtils.createFloatBuffer(vertices.size()*3);
		for(Vector3f f: vertices){
			b.put((f.x)).put(f.y).put(f.z);
		}
		b.rewind();
		return b;
	}
	
	public void loadModel(){
		try {
			OBJLoader.loadModel(modelPath, this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void render(){
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
		VBOCreated = true; //vbo created
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
	
	public boolean isVBOCreated(){
		return VBOCreated;
	}
	
	public List<Vector3f> getVerticesList(){
		return vertices;
	}

}
