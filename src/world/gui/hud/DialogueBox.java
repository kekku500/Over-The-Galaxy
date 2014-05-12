package world.gui.hud;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import state.Game;
import state.threading.RenderThread;
import utils.Stringcutter;
import world.graphics.Graphics2D;
import world.gui.AbstractComponent;

public class DialogueBox extends AbstractComponent{
	
	private static int vboVertexID;
	private static int vboTexVertexID;
	
	private ArrayList<String> tekst = new ArrayList<String>();
	
	private int algus = 0;
	private int lopp = 7;
	private int tekst_suurus;
	
	private static int width = 250;
	private static int height = 138;
	
	public DialogueBox(){
	setPosition(Game.displayWidth-width,Game.displayHeight-height);
		
	addText("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
	}
	
	public void addText(String tekst){
		String[] meh = Stringcutter.cut(tekst, 30);
		for(String s : meh){
			this.tekst.add(s);
		}
		tekst_suurus = this.tekst.size();
	}
	
	@Override
	public void render() {
		int textureid = RenderThread.spritesheet.getTex().getID();
		Vector2f pos = getPosition();
		
		glColor3f(1,1,1);
		
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Graphics2D.drawVBO(12, vboVertexID, vboTexVertexID, textureid);
		for(int i = algus; i < lopp; i++){
			Graphics2D.drawString(10, 10+(i*15), (i < tekst.size())?tekst.get(i):" ");
			}
		
		glDisable(GL_BLEND);
		
		glPopMatrix();
	}
	
	@Override
	public void update(float dt) {
		if(tekst.size() != tekst_suurus){
			algus +=1;
			lopp += 1;
			tekst_suurus = tekst.size();
		}		
	}
	
	public void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(2 * 4); //(x,y)*(4 vertices on a rectangle)
		vertices.put(new float[]{0,height, width,height, width,0, 0,0});
		vertices.rewind();
		
		FloatBuffer texVertices = BufferUtils.createFloatBuffer(2 * 4);
		texVertices.put(new float[]{
		RenderThread.spritesheet.getBottomLeftCoordNormal(81)[0],
		RenderThread.spritesheet.getBottomLeftCoordNormal(81)[1],
		RenderThread.spritesheet.getBottomRightCoordNormal(85)[0],
		RenderThread.spritesheet.getBottomRightCoordNormal(85)[1],
		RenderThread.spritesheet.getUpRightCoordNormal(65)[0],
		RenderThread.spritesheet.getUpRightCoordNormal(65)[1],
		RenderThread.spritesheet.getUpLeftCoordNormal(61)[0],
		RenderThread.spritesheet.getUpLeftCoordNormal(61)[1]
		});
		texVertices.rewind();
		
		vboVertexID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		vboTexVertexID = glGenBuffers();		
		glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
		glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void dispose(){
	    glDeleteBuffers(vboVertexID);
		glDeleteBuffers(vboTexVertexID);
	}
}

