package blender.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MTLLoader {
	
	public static void loadMaterial(Model m, String pathf)throws FileNotFoundException, IOException{
		File f = new File(pathf);
		f = new File(f.getAbsolutePath());
		String path = f.getAbsolutePath().replace(f.getName(), "");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line;
		Material ma = new Material();
		while((line = reader.readLine()) != null){
			if(line.startsWith("newmtl ")){
				if(ma.getName() != null){
					m.materials.put(ma.getName(), ma);
					ma = new Material();
				}
				ma.setName(line.split(" ")[1]);
			}else if(line.startsWith("Ns ")){
				ma.setShininess(Float.valueOf(line.split(" ")[1]));
			}else if(line.startsWith("Ka ")){
				float ar = Float.valueOf(line.split(" ")[1]);
				float ag = Float.valueOf(line.split(" ")[2]);
				float ab = Float.valueOf(line.split(" ")[3]);
				ma.setAmbient(new float[]{ar,ag,ab,1f});
			}else if(line.startsWith("Kd ")){
				float dr = Float.valueOf(line.split(" ")[1]);
				float dg = Float.valueOf(line.split(" ")[2]);
				float db = Float.valueOf(line.split(" ")[3]);
				ma.setDiffuse(new float[]{dr,dg,db, 1f});
			}else if(line.startsWith("Ks ")){
				float sr = Float.valueOf(line.split(" ")[1]);
				float sg = Float.valueOf(line.split(" ")[2]);
				float sb = Float.valueOf(line.split(" ")[3]);
				ma.setSpecular(new float[]{sr,sg,sb, 1f});
			}else if(line.startsWith("Ni ")){
				//ma.setRefraction(Float.valueOf(line.split(" ")[1]));
			}else if(line.startsWith("d ")){
				ma.setAlpha(Float.valueOf(line.split(" ")[1]));
			}else if (line.startsWith("map_Kd ")){
				String name = line.split("\\s+")[1];
				ma.textureFile = path + name;
			}else if(line.startsWith("map_bump ")){
				System.out.println("map bump is " + line);
				String name = line.split("\\s+")[1];
				ma.normalFile = path + name;
			}
		}
		m.materials.put(ma.getName(), ma);
		reader.close();		
	}
}
