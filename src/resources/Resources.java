package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import resources.model.Model;
import resources.texture.Texture;
import shader.Shader;
import state.Game;
import utils.Utils;

/**
 * Loads textures, models and shaders from RESOURCESPATH folder.
 * Loaded data can be accessed using the path of the file. 
 * To get model, reference to .obj file. Textures can be accessed the same way.
 * Cube textures are specified using .ct extension. To get cube texture, do include .ct.
 * Vertex and fragment shader must be with same name. Extensions .vt and fs. To get shader
 * reference, do not include extension, ex "shader.vs" and "shader.fs" can be accessed using string "shader".
 * @author Kevin
 */

public class Resources {
	
	private static String[] supportedTextures = {".png", ".jpg"};
	private static String cubeTextureInfoExt = ".ct";
	private static String modelExt = ".obj";
	private static String shaderVertexExt = ".vs";
	private static String shaderFragmentExt = ".fs";
	
	public final static String RESOURCESPATH = "res\\";
	public final static String MODELPATH = "models\\";
	public final static String SHADERPATH = "shaders\\";
	public final static String TEXTUREPATH = "textures\\";
	
	//Collection of all models, textures, shaders
	private final static HashMap<String, Model> models = new HashMap<String, Model>();
	private final static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private final static HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	
	public static void loadResources(String resourcespath) {
		readFiles(RESOURCESPATH);
		
	    createTextures();
	    initializeTextures();

	    createModels();
	    initializeModels();
	    
	    createShaders();
	    
	    
	    clearBS(); //clear string files lists
	}
	
	private static List<String> shaderFiles = new ArrayList<String>();
	private static List<String> modelFiles = new ArrayList<String>();
	private static List<String> textureFiles = new ArrayList<String>();
	private static List<String> ctFiles = new ArrayList<String>();
	private static void readFiles(String mainFolder){
		File folder = new File(mainFolder);
		for(File fileOrInnerFolder: folder.listFiles()){
			if(fileOrInnerFolder.isDirectory()){
				readFiles(mainFolder + fileOrInnerFolder.getName() + "\\");
			}else if(fileOrInnerFolder.isFile()){
				String fname = fileOrInnerFolder.getName();
				String fpath = folder + "\\" + fname;
				if(fname.endsWith(cubeTextureInfoExt)){
					ctFiles.add(fpath);
				}else if(fname.endsWith(modelExt)){
					modelFiles.add(fpath);
				}else if(fname.endsWith(shaderVertexExt)){
					shaderFiles.add(fpath);
				}
				
				for(String supportedTexExt: supportedTextures)
					if(fname.endsWith(supportedTexExt)){
						textureFiles.add(fpath);
						break;
					}
			}
		}
	}
	
	private static void clearBS(){
		shaderFiles.clear();
		modelFiles.clear();
		textureFiles.clear();
		ctFiles.clear();
	}
	
	private static void createModels(){
		for(String modelPath: modelFiles){
			models.put(modelPath, new Model(modelPath));
		}
	}
	
	private static void createTextures(){
		//Search for cube textures
		for(String ctFile: ctFiles){
			String ctData = Utils.readFileAsString(ctFile);
			//Read cube texture paths and remove from textureFiles
			String texturePath = new File(ctFile).getParent();
			String[] cubeTexturePaths = new String[6];
			int c = 0;
			for(String ctLine: ctData.split("\n")){
				if(ctLine.startsWith("\"")){
					String absTexturePath = texturePath + "\\" + ctLine.replace("\"", "");
					textureFiles.remove(absTexturePath);
					cubeTexturePaths[c] = absTexturePath;
					c++;
				}
			}
			//Add cube texture
			Texture tex = new Texture(ctFile, cubeTexturePaths);
			textures.put(ctFile, tex);
			

		}
		//Other textures
		for(String texturePath: textureFiles){
			textures.put(texturePath, new Texture(texturePath));

		}
	}
	
	private static void createShaders(){
		for(String vertexLoc: shaderFiles){
			String fragmentLoc = vertexLoc.replace(shaderVertexExt, shaderFragmentExt);
			Shader s = new Shader(vertexLoc, fragmentLoc);
			shaders.put(vertexLoc.replace(shaderVertexExt, ""), s);
		}
		Game.println("Created and initalized shaders");
	}
	
	private static void initializeModels(){
		int totalModels = models.size();
		int createdModels = 0;
		int lastPrint = 0;
		Game.print("Initializing Models... ");
		for(Model m: models.values()){
			m.prepareVBO();
			createdModels++;
			float percentDone = createdModels/(float)totalModels * 100;
			if(percentDone > lastPrint){
				lastPrint = (int)Math.ceil(percentDone/10f)*10;
				Game.print(Math.round(percentDone) + "% ");
			}
			
		}
		Game.print("\n");
	}
	
	private static void initializeTextures(){
		int totalTextures = textures.size();
		int createdTextures = 0;
		int lastPrint = 0;
		Game.print("Initializing Textures... ");
		for(Texture m: textures.values()){
			m.createTexture();
			createdTextures++;
			float percentDone = ((float)createdTextures)/totalTextures * 100;
			if(percentDone > lastPrint){
				lastPrint = (int)Math.ceil(percentDone/10f)*10;
				Game.print(Math.round(percentDone) + "% ");
			}
		}
		Game.print("\n");
	}
	
	/**
	 * @param modelPath Model path from RESPURCEPATH/MODELPATH include .obj
	 */
	public static Model getModel(String modelPath) throws Exception{
		modelPath = RESOURCESPATH + MODELPATH + modelPath;
		Model m = models.get(modelPath);
		if(m == null)
			throw new Exception("Model " + modelPath + " not found!");
		return m;
	}
	
	/**
	 * @param texturePath Texture path from RESPURCEPATH/TEXTUREPATH include picture extension
	 */
	public static Texture getTexture(String texturePath) throws Exception{
		texturePath = Resources.RESOURCESPATH + Resources.TEXTUREPATH + texturePath;
		Texture tex = textures.get(texturePath);
		if(tex == null)
			throw new Exception("Texture " + texturePath + " not found!");
		return tex;
	}
	
	/**
	 * @param texturePath Full path of the texture file.
	 */
	public static Texture getModelTexture(String texturePath) throws Exception{
		Texture tex = null;
		for(Texture t: textures.values()){
			if((texturePath.endsWith(t.getName()))){
				tex = t;
				break;
			}
		}
		if(tex == null)
			throw new Exception("Model Texture " + texturePath + " not found!");
		return tex;
	}
	
	/**
	 * @param texturePath Shader path from RESPURCEPATH/SHADERPATH Do not include extension.
	 */
	public static Shader getShader(String shaderPath) throws Exception{
		shaderPath = RESOURCESPATH + SHADERPATH + shaderPath;
		Shader shader = shaders.get(shaderPath);
		
		if(shader == null)
			throw new Exception("Shader " + shaderPath + " not found!");
		return shader;
			
	}

	public static void destoryResources(){
		for(Model m: models.values())
			m.dispose();
		for(Texture tex: textures.values())
			tex.dispose();
		for(Shader s: shaders.values())
			s.destroy();
	}

}
