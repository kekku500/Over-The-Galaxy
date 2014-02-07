package game.vbotemplates;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import org.lwjgl.util.glu.Sphere;

public class SphereVBO extends AbstractVBO{
	
	private Sphere sphere;
	private float radius;
	private int detail1, detail2;
	
	public SphereVBO(float radius, int detail1, int detail2){
		sphere = new Sphere();
		this.radius = radius;
		this.detail1 = detail1;
		this.detail2 = detail2;
		
	}
	
	@Override
	public void render(){
		sphere.draw(radius, detail1, detail2);
	}

	@Override
	protected void glDraw() {
		// TODO Auto-generated method stub
		
	}
	
	public void dispose(){
	 
	}
	
	public void prepareVBO() {

	}
	
}
