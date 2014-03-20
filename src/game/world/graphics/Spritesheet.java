package game.world.graphics;

import blender.model.Texture;

public class Spritesheet {
	private Texture tex;
	private int Id;
	private int Squares_across;
	private int Square_width;


	
	public Spritesheet(String loc, int Square_width){
		tex = Texture.loadTexture(loc);
		Id = tex.id;
		this.Square_width = Square_width;
		Squares_across = tex.width/Square_width;

	
	}
	
	public float[] getUpLeftCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex = {(across-1)*Square_width/(float)tex.width, (down-1)*Square_width/(float)tex.height};
		return vertex;
	}
	
	public float[] getUpRightCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex = {across*Square_width/(float)tex.width, (down-1)*Square_width/(float)tex.height};
		return vertex;
	}
	
	public float[] getBottomLeftCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex =  {(across-1)*Square_width/(float)tex.width, down*Square_width/(float)tex.height};
		return vertex;
	}
	
	public float[] getBottomRightCoordNormal(int square){
		int down = (int)(Math.ceil((double)square/Squares_across));
		int across = square-(down-1)*Squares_across;
		
		float[] vertex =  {across*Square_width/(float)tex.width, down*Square_width/(float)tex.height};
		return vertex;
	}

	public Texture getTex() {
		return tex;
	}

	public int getId(){
		return Id;
	}
}
