package resources.texture;

import resources.Resources;


public class Spritesheet2 {
	private Texture tex;
	private int Id;
	private int Squares_across;
	private int Square_width;
	
	
	
	public Spritesheet2(String loc, int Square_width){
		tex = null;
		try {
			tex = Resources.getTexture(loc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Id = tex.getID();
		this.Square_width = Square_width;
		Squares_across = tex.getWidth()/Square_width;
		
	
	}
	
	public float[] getUpLeftCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex = {(across-1)*Square_width/(float)tex.getWidth(), (down-1)*Square_width/(float)tex.getHeight()};
		return vertex;
	}
	
	public float[] getUpRightCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex = {across*Square_width/(float)tex.getWidth(), (down-1)*Square_width/(float)tex.getHeight()};
		return vertex;
	}
	
	public float[] getBottomLeftCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex = {(across-1)*Square_width/(float)tex.getWidth(), down*Square_width/(float)tex.getHeight()};
		return vertex;
	}
	
	public float[] getBottomRightCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex = {across*Square_width/(float)tex.getWidth(), down*Square_width/(float)tex.getHeight()};
		return vertex;
	}
	
	public Texture getTex() {
		return tex;
	}
	
	public int getId(){
		return Id;
	}
}
