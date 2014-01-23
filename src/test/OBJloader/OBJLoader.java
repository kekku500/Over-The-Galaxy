package test.OBJloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import test.OBJloader.Face;
import test.OBJloader.Model;

public class OBJLoader{
	public static Model loadModel(File f) throws FileNotFoundException, IOException{
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Model m = new Model();
		String line;
		Material material = null;
		while((line = reader.readLine()) != null){
			if(line.startsWith("mtllib ")){
				MTLLoader.loadMaterial(m, "C:/Users/Marko/Documents/GitHub/Over-The-Galaxy/src/" +line.split(" ")[1]  + ".mtl");
			}else if(line.startsWith("v ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.vertices.add(new Vector3f(x,y,z));
			} else if(line.startsWith("vn ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.normals.add(new Vector3f(x,y,z));
			} else if(line.startsWith("vt ")){
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				m.texture.add(new Vector2f(x,y));
			} else if(line.startsWith("usemtl ")){
				material = m.materials.get(line.split(" ")[1]);
			} else if(line.startsWith("f ")){
				Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]),
													  Float.valueOf(line.split(" ")[2].split("/")[0]),
													  Float.valueOf(line.split(" ")[3].split("/")[0]));
				Vector3f textureIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[1]),
						  							   Float.valueOf(line.split(" ")[2].split("/")[1]),
						  							   Float.valueOf(line.split(" ")[3].split("/")[1]));
				Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]),
						  							  Float.valueOf(line.split(" ")[2].split("/")[2]),
						  							  Float.valueOf(line.split(" ")[3].split("/")[2]));
				m.faces.add(new Face(vertexIndices, textureIndices, normalIndices, material));
			}
		}
		reader.close();
		m.prepareVBO();
		return m;
	}
}
