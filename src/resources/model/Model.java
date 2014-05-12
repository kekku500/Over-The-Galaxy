package resources.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;

import utils.Utils;
import utils.math.Vector3f;

public class Model{
	
	public List<SubModel> submodels = new ArrayList<SubModel>();
	
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	//public List<Vector2f> texCoords = new ArrayList<Vector2f>();
    public HashMap<String, Material> materials = new HashMap<String, Material>();
    
    public String modelPath; //model .obj directory
	
	public boolean VBOCreated = false;
	//public boolean isTextured = false; //All submodels textured?
    public boolean isGodRays = false; //create light scattering
    
    public boolean useTextureAtlas = false;
    public String atlasFile;
    
	
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
		//System.out.println("Model " + modelPath);
		SubModel materialSubModel = null;
		SubModel combinedAltasTexturedSubModel = null;
		//Combine all submodels that dont have textures (probably have different materials)
		List<SubModel> noTexSubModels = new ArrayList<SubModel>();
		for(SubModel m: submodels){
			if(!m.isTextured){
				noTexSubModels.add(m);
			}
		}
		//System.out.println("material combined " + noTexSubModels.size());
		if(!noTexSubModels.isEmpty()){
			materialSubModel = new SubModel(this);
			materialSubModel.prepareCombinedVBOSubModels(noTexSubModels, false);
			submodels.removeAll(noTexSubModels);
			//submodels.add(materialSubModel);
		}
		
		if(useTextureAtlas){
			String[] atlasData = Utils.readFileAsString(atlasFile).split("\n");
			String atlasTextureFileName = atlasFile.substring(0, atlasFile.lastIndexOf("\\")+1)
											+ atlasData[0];
			
			List<String[]> textureAtlasInfo = new ArrayList<String[]>();
			for(int i = 1;i<atlasData.length;i++){
				textureAtlasInfo.add(atlasData[i].split("\\s+"));
			}
			
			//Check which submodels are connected to texture atlas
			boolean altasAlsoHasNormalMapTexture = false;
			String altasNormalMapFile = null;
			List<SubModel> altasTexturedSubModels = new ArrayList<SubModel>();
			for(SubModel m: submodels){
				if(m.isTextured){
					for(String[] infos: textureAtlasInfo){ //texture in atlas?
						if(Utils.removeFileExt(m.material.textureFile).contains(infos[0])){ //does altas contain submodel texture?
							if(m.material.normalFile != null){
								altasAlsoHasNormalMapTexture = true;
								altasNormalMapFile = m.material.normalFile;
							}
							m.setAltasTextureData(infos);
							altasTexturedSubModels.add(m);
							break;
						}
					}
				}
			}
			
			combinedAltasTexturedSubModel = new SubModel(this);
			combinedAltasTexturedSubModel.isTextured = true;
			combinedAltasTexturedSubModel.material.textureFile = atlasTextureFileName;
			combinedAltasTexturedSubModel.isNormalMapped = altasAlsoHasNormalMapTexture;
			combinedAltasTexturedSubModel.material.normalFile = altasNormalMapFile;
			
			//One submodel done!
			combinedAltasTexturedSubModel.prepareCombinedVBOSubModels(altasTexturedSubModels, true);
			submodels.removeAll(altasTexturedSubModels);
			//System.out.println("altas texture count " + altasTexturedSubModels.size());
			

			//submodels.add(combinedAltasTexturedSubModel);
		}
		
		//Prepare all models that have textures on different files
		//System.out.println("size of one textured " + submodels.size());
		for(SubModel m: submodels){
			m.prepareVBO();
		}
		
		//Add rest aswell now
		if(materialSubModel != null)
			submodels.add(materialSubModel);
		if(combinedAltasTexturedSubModel != null)
			submodels.add(combinedAltasTexturedSubModel);

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
