package blender.model;

import game.vbo.ModelVBO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL13.*;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

public class Model extends ModelVBO{
	
	public List<SubModel> submodels = new ArrayList<SubModel>();
	
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texCoords = new ArrayList<Vector2f>();
    public HashMap<String, Material> materials = new HashMap<String, Material>();
    
    private String pathf;
    
    public Model(){}
	
	public Model(String pathf){
		this.pathf = pathf;
	}
	
	public void render(){
		if(initialMotion != null){
			//Transform t = new Transform();
			float[] f = new float[16];
			//body.getMotionState().getWorldTransform(t);

			initialMotion.getOpenGLMatrix(f);
			
			FloatBuffer fb = BufferUtils.createFloatBuffer(16);
			fb.put(f);
			fb.rewind();
			
			glMultMatrix(fb);
		}
       for(SubModel m: submodels){
    	   m.render();
       }
	}
	
	public void prepareVBO(){
		//create model
		try {
			OBJLoader.loadModel(pathf, this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(SubModel m: submodels){
			m.prepareVBO();
		}
	}
	
	public void dispose(){
		for(SubModel m: submodels){
			m.dispose();
		}
	}

	@Override
	protected void glDraw() {
	}

}
