package blender.model;

import static org.lwjgl.opengl.GL11.glMultMatrix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import com.bulletphysics.linearmath.Transform;

public class Model{
	
	public List<SubModel> submodels = new ArrayList<SubModel>();
	
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texCoords = new ArrayList<Vector2f>();
    public HashMap<String, Material> materials = new HashMap<String, Material>();
    
    private String pathf;
    
	public Transform offset;
	
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
	
	public void render(){
		if(offset != null){
			float[] f = new float[16];

			offset.getOpenGLMatrix(f);
			
			FloatBuffer fb = BufferUtils.createFloatBuffer(16);
			fb.put(f);
			fb.rewind();
			
			glMultMatrix(fb);
		}


		renderDraw();
		
	}
	
	public void render(boolean translate){
		if(translate)
			if(offset != null){
				float[] f = new float[16];
				
				offset.getOpenGLMatrix(f);
				
				FloatBuffer fb = BufferUtils.createFloatBuffer(16);
				fb.put(f);
				fb.rewind();
				
				glMultMatrix(fb);
			}

		renderDraw();
		
	}
	
	public void renderDraw(){
		for(SubModel m: submodels){
			m.render();
		}
	}
	public boolean isTextured = false;
	public void prepareVBO(){
		boolean allTex = true;
		for(SubModel m: submodels){
			m.prepareVBO();
			if(!m.isTextured)
				allTex = false;
		}
		if(allTex)
			isTextured = true;
	}
	
	public void dispose(){
		for(SubModel m: submodels){
			m.dispose();
		}
	}
	
	public void setOffset(Transform t){
		offset = t;
	}

}
