package test.OBJloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
		ArrayList<Face> face = new ArrayList<Face>();
		int i = -1;
		while((line = reader.readLine()) != null){
			if(line.startsWith("mtllib ")){
				MTLLoader.loadMaterial(m, "D:\\Programming\\eclipse\\workspaces\\java\\github\\Over-The-Galaxy\\src\\resources\\" +line.split(" ")[1]  + ".mtl");
				//MTLLoader.loadMaterial(m, "src/" +line.split(" ")[1]);
			}else if(line.startsWith("o ")){
				i++;
				if(i == 0){
					continue;
				}
				m.faces.add(face);
				face = new ArrayList<Face>();
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
				m.textured = true;
				m.texture.add(new Vector2f(x,y));
			} else if(line.startsWith("usemtl ")){
				material = m.materials.get(line.split(" ")[1]);
			} else if(line.startsWith("f ")){
				Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]),
													  Float.valueOf(line.split(" ")[2].split("/")[0]),
													  Float.valueOf(line.split(" ")[3].split("/")[0]));
				Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]),
						  							  Float.valueOf(line.split(" ")[2].split("/")[2]),
						  							  Float.valueOf(line.split(" ")[3].split("/")[2]));
				if(m.textured){
				Vector3f textureIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[1]),
						  							   Float.valueOf(line.split(" ")[2].split("/")[1]),
						  							   Float.valueOf(line.split(" ")[3].split("/")[1]));
				face.add(new Face(vertexIndices, textureIndices, normalIndices, material));
				}else{
					face.add(new Face(vertexIndices, null, normalIndices, material));
				}
				
			}
		}
		reader.close();
		m.faces.add(face);
		m.prepareVBO();
		return m;
	}
}
