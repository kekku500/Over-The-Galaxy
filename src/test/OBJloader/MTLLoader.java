package test.OBJloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import test.OBJloader.Texture;

import org.lwjgl.util.vector.Vector3f;
//import org.newdawn.slick.opengl.TextureLoader;
//import org.newdawn.slick.util.ResourceLoader;


public class MTLLoader {
	public static void loadMaterial(Model m, String file)throws FileNotFoundException, IOException{
		File f = new File(file);
		f = new File(f.getAbsolutePath());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line;
		Material ma = new Material();
		while((line = reader.readLine()) != null){
			if(line.startsWith("newmtl ")){
				if(ma.name != null){
					m.materials.put(ma.name, ma);
					ma = new Material();
				}
				ma.setName(line.split(" ")[1]);
			}else if(line.startsWith("Ns ")){
				ma.setShininess(Float.valueOf(line.split(" ")[1]));
			}else if(line.startsWith("Ka ")){
				float ar = Float.valueOf(line.split(" ")[1]);
				float ag = Float.valueOf(line.split(" ")[2]);
				float ab = Float.valueOf(line.split(" ")[3]);
				ma.setAmbient(new Vector3f(ar,ag,ab));
			}else if(line.startsWith("Kd ")){
				float dr = Float.valueOf(line.split(" ")[1]);
				float dg = Float.valueOf(line.split(" ")[2]);
				float db = Float.valueOf(line.split(" ")[3]);
				ma.setDiffuse(new Vector3f(dr,dg,db));
			}else if(line.startsWith("Ks ")){
				float sr = Float.valueOf(line.split(" ")[1]);
				float sg = Float.valueOf(line.split(" ")[2]);
				float sb = Float.valueOf(line.split(" ")[3]);
				ma.setSpecualrity(new Vector3f(sr,sg,sb));
			}else if(line.startsWith("Ni ")){
				ma.setRefraction(Float.valueOf(line.split(" ")[1]));
			}else if(line.startsWith("d ")){
				ma.setAlpha(Float.valueOf(line.split(" ")[1]));
			}else if(line.startsWith("map_Kd ")){
				if(line.split(" ").length > 1)
					ma.texture = Texture.loadTexture(line.split(" ")[1]);
			//	ma.texture = TextureLoader.getTexture("TGA", ResourceLoader.getResourceAsStream("src/resources/" + line.split(" ")[1]));
			}
		}
		m.materials.put(ma.name, ma);
		reader.close();		
	}
}
