package blender.model;

import game.threading.RenderThread;

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
import org.lwjgl.opengl.GL11;

import com.bulletphysics.linearmath.Transform;

public class Model{
	
	public List<SubModel> submodels = new ArrayList<SubModel>();
	
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Vector2f> texCoords = new ArrayList<Vector2f>();
    public HashMap<String, Material> materials = new HashMap<String, Material>();
    
    public boolean transparent = false;
    
    private String pathf;
    
	protected Transform offset;
	
	private boolean enableLighting = true;
    
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
		boolean allTransparent = true;
		for(SubModel m: submodels){
			if(!m.getMaterial().transparent){
				allTransparent = false;
				break;
			}
		}
		if(allTransparent)
			transparent = true;
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
		if(RenderThread.enableLighting && !enableLighting)
			glDisable(GL_LIGHTING);
		renderDraw();
		if(RenderThread.enableLighting && !enableLighting)
			glEnable(GL_LIGHTING);

	}
	
	public void renderDraw(){
		for(SubModel m: submodels){
			m.render();
		}
	}
	
	public void prepareVBO(){
		for(SubModel m: submodels){
			m.prepareVBO();
		}
	}
	
	public void dispose(){
		for(SubModel m: submodels){
			m.dispose();
		}
	}
	
	public void setOffset(Transform t){
		offset = t;
	}
	
	public void enableLighting(boolean b){
		enableLighting = b;
	}

}
