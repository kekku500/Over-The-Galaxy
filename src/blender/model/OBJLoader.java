package blender.model;

import game.Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;




import utils.math.Vector2f;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class OBJLoader {
	
	public static void loadModel(String pathf, Model m) throws FileNotFoundException, IOException{
		//File f = new File(Game.RESOURCESPATH + Game.MODELPATH + pathf);
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
			}else if(line.startsWith("f ")){
				String[] indicesData = line.replace("f ", "").trim().split("\\s+");
				
				//Split face vertices into triangles (TRIANGLE FANNING)
				String[] indiceData1 = indicesData[0].split("/");
				
				for(int i=0;i+2<indicesData.length;i++){
					String[] indiceData2= indicesData[i+1].split("/");
					String[] indiceData3 = indicesData[i+2].split("/");
					
					
					Vector3f vertexIndices = new Vector3f(Integer.parseInt(indiceData1[0]) - 1,
														Integer.parseInt(indiceData2[0]) - 1,
														Integer.parseInt(indiceData3[0]) - 1);
					
					Vector3f normalIndices = new Vector3f(Integer.parseInt(indiceData1[2]) - 1,
														Integer.parseInt(indiceData2[2]) -1 ,
														Integer.parseInt(indiceData3[2]) - 1);
	                if (subM.isTextured){
	                	if(!useMtl){ //model is textured, but no usemtl was specified, use previous material
	                		subM.setMaterial(material);
	                	}
						Vector3f texIndices = new Vector3f(Integer.parseInt(indiceData1[1]) - 1,
								Integer.parseInt(indiceData2[1]) - 1,
								Integer.parseInt(indiceData3[1]) - 1);
						subM.faces.add(new Face(vertexIndices, normalIndices, texIndices, material));
	                }else{
	                	subM.faces.add(new Face(vertexIndices, normalIndices, null, material));
	                }
	                
				}
                finalStep = true;
			}
		}
		m.submodels.add(subM); 
		subM = new SubModel(m);
		reader.close();
	}

}
