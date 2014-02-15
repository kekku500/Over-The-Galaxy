package blender.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class OBJLoader {
	
	public static void loadModel(String pathf, Model m) throws FileNotFoundException, IOException{
		File f = new File(pathf);
		f = new File(f.getAbsolutePath());
		String path = f.getAbsolutePath().replace(f.getName(), "");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		Material material = null;
		String line = null;
		SubModel subM = new SubModel(m);
		boolean finalStep = false;
		boolean skipVertices = false;
		boolean useMtl = false;
		if(!m.vertices.isEmpty())
			skipVertices = true;
		while((line = reader.readLine()) != null){
			if(line.startsWith("mtllib ")){
				MTLLoader.loadMaterial(m, path + line.split(" ")[1]);
			}else if(!skipVertices && line.startsWith("v ")){
				if(finalStep){ //new object
					m.submodels.add(subM);
					subM = new SubModel(m);
					useMtl = false;
					finalStep = false;
				}
                String[] values = line.split("\\s+");
                float x = Float.parseFloat(values[1]);
                float y = Float.parseFloat(values[2]);
                float z = Float.parseFloat(values[3]);
                m.vertices.add(new Vector3f(x, y, z));
			}else if(line.startsWith("vn ")){
                String[] values = line.split("\\s+");
                float x = Float.parseFloat(values[1]);
                float y = Float.parseFloat(values[2]);
                float z = Float.parseFloat(values[3]);
                m.normals.add(new Vector3f(x, y, z));
			}else if (line.startsWith("vt ")){
				String[] values = line.split(" ");
				float x = Float.parseFloat(values[1]);
				float y = Float.parseFloat(values[2]);
				Vector2f texCoord = new Vector2f(x, y);
				subM.isTextured = true;
				m.texCoords.add(texCoord);
			}else if(line.startsWith("usemtl ")){
				useMtl = true;
                material = m.materials.get(line.replaceAll("usemtl ", "").trim());
                subM.setMaterial(material);
			}else if(line.startsWith("f ")){ //this goest to subM!!!!!!!!!!!!!!!!!, before get usemtl plane (line 12066)
                String[] values = line.split("\\s+");
                float v1 = Float.parseFloat(values[1].split("/")[0]);
                float v2 = Float.parseFloat(values[2].split("/")[0]);
                float v3 = Float.parseFloat(values[3].split("/")[0]);
                Vector3f vertex = new Vector3f(v1, v2, v3);
                
                float vn1 = Float.parseFloat(values[1].split("/")[2]);
                float vn2 = Float.parseFloat(values[2].split("/")[2]);
                float vn3 = Float.parseFloat(values[3].split("/")[2]);
                Vector3f normal = new Vector3f(vn1, vn2, vn3);
                
                if (subM.isTextured){
                	if(!useMtl){ //model is textured, but no usemtl was specified, use previous material
                		subM.setMaterial(material);
                	}
                    float vt1 = Float.parseFloat(values[1].split("/")[1]);
                    float vt2 = Float.parseFloat(values[2].split("/")[1]);
                    float vt3 = Float.parseFloat(values[3].split("/")[1]);

                    Vector3f texCoords = new Vector3f(vt1, vt2, vt3);

                    subM.faces.add(new Face(vertex, normal, texCoords));
                }else{
                	subM.faces.add(new Face(vertex, normal, null));
                }
                finalStep = true;
			}
		}
		m.submodels.add(subM); 
		subM = new SubModel(m);
		reader.close();
	}

}
