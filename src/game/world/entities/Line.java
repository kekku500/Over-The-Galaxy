package game.world.entities;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Line extends AbstractEntity{
	
	private float length;
	
	private int lineWidth = 1;

	public Line(Vector3f startPos, Vector3f endPos){
		super(startPos);

		//calculate length
		Vector3f vec = new Vector3f(endPos.x-startPos.x, endPos.y-startPos.y, endPos.z-startPos.z);
		length = vec.length();
		
		//Create Vertex Buffer
		vertices = BufferUtils.createFloatBuffer(3 * 2);
		vertices.put(new float[]
				{0, 0, 0,
				endPos.x-startPos.x, endPos.y-startPos.y, endPos.z-startPos.z});
		vertices.rewind();
	}
	
	@Override
	public void render2(){
		GL11.glLineWidth(lineWidth);
		glDrawArrays(GL_LINES, 0, 2);
		GL11.glLineWidth(1);
	}
	
	public float length(){
		return length;
	}
	
	public void setLineWidth(int w){
		lineWidth = w;
	}
	





}
