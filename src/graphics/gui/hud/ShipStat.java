package graphics.gui.hud;

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
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import entity.creation.Player;
import graphics.BufferSubData;
import graphics.Graphics2D;
import graphics.gui.AbstractComponent;

import java.nio.FloatBuffer;

import main.Config;
import main.state.threading.RenderThread;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

public class ShipStat extends AbstractComponent{

		private static int vboVertexID;
		private static int vboTexVertexID;
		
		private static float x = 0.304F;
		private static float y = 0.195F;
		private static int hull = 148;
		private static int fuel = 148;
		private Player player;
		private static int width = 250;
		private static int height = 138;
		//private FloatBuffer Vertices = BufferUtils.createFloatBuffer(2 * 6);
		BufferSubData verticesChange = new BufferSubData(BufferUtils.createFloatBuffer(2 * 6), 0).setOffsetByFloat(12); //skip 12 float values
		
		public ShipStat(Player player){
			this.player = player;
			
			setPosition(0,Config.DISPLAY_HEIGHT-height);
		}
		
		@Override
		public void update(float dt) {
			/*float[] vertex = {
					width*x,height*y+6,width*x+hull*(player.getFuel()/100F),height*y+6,width*x+hull*(player.getFuel()/100F),height*y,width*x,height*y,
					width*x,height*y+27,width*x+fuel*(player.getFuel()/100F),height*y+27,width*x+fuel*(player.getFuel()/100F),height*y+21,width*x,height*y+21
			
			};*/
			float[] vertex = {	
					/*0,0,
					0,height, 
					width,height, 
					width,0,
					
					width*x,height*y,
					width*x,height*y+6,*/
					width*x+hull*(player.getFuel()/100F),height*y+6,
					width*x+hull*(player.getFuel()/100F),height*y,
					
					width*x,height*y+21,
					width*x,height*y+27,
					width*x+fuel*(player.getFuel()/100F),height*y+27,
					width*x+fuel*(player.getFuel()/100F),height*y+21
					};
			verticesChange.put(vertex);
			//Vertices.put(vertex);
			//Vertices.rewind();
			
			

		}
		
		@Override
		public void render(){
			int textureid = RenderThread.spritesheet.getTex().getID();
			Vector2f pos = getPosition();
			
			glColor3f(1,1,1);
			
			glPushMatrix();
			
			glTranslatef(pos.x, pos.y, 0);
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			Graphics2D.drawVBO(12, vboVertexID, verticesChange, vboTexVertexID, null, textureid);
			
			glDisable(GL_BLEND);
			
			glPopMatrix();
		}
		
		
		public void dispose(){
		    glDeleteBuffers(vboVertexID);
			glDeleteBuffers(vboTexVertexID);
		}

		public void init() {
			FloatBuffer vb = BufferUtils.createFloatBuffer(2 * 12);
			/*vb.put(new float[]{	
					0,0,0,height, width,height, width,0,
					width*x,height*y,width*x,height*y+6,width*x+hull,height*y+6,width*x+hull,height*y,
					width*x,height*y+21,width*x,height*y+27,width*x+fuel,height*y+27,width*x+fuel,height*y+21}); //clockwise, front face
			*/
			vb.put(new float[]{	
					0,0,
					0,height, 
					width,height, 
					width,0,
					
					width*x,height*y,
					width*x,height*y+6,
					width*x+hull,height*y+6,
					width*x+hull,height*y,
					
					width*x,height*y+21,
					width*x,height*y+27,
					width*x+fuel,height*y+27,
					width*x+fuel,height*y+21
					
					}); //clockwise, front face
			
			vb.rewind();
			
			FloatBuffer texVertices = BufferUtils.createFloatBuffer(2 * 12);
			/*float[] texturea = {
			RenderThread.spritesheet.getBottomLeftCoordNormal(27)[0],
			RenderThread.spritesheet.getBottomLeftCoordNormal(27)[1],
			RenderThread.spritesheet.getBottomRightCoordNormal(30)[0],
			RenderThread.spritesheet.getBottomRightCoordNormal(30)[1],
			RenderThread.spritesheet.getUpRightCoordNormal(10)[0],
			RenderThread.spritesheet.getUpRightCoordNormal(10)[1],
			RenderThread.spritesheet.getUpLeftCoordNormal(7)[0],
			RenderThread.spritesheet.getUpLeftCoordNormal(7)[1],	
			
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[1],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[0],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[1],
			
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomLeftCoordNormal(51)[1],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[0],
			RenderThread.spritesheet.getBottomRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[0],
			RenderThread.spritesheet.getUpRightCoordNormal(51)[1],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[0],
			RenderThread.spritesheet.getUpLeftCoordNormal(51)[1]
			};*/
			float[] texturea = {
		       		0.6f, 0.0f,
		       		0.6f, 0.3f, 
		       		1.0f, 0.3f, 
		       		1.0f, 0.0f, 

		       		0.0f, 0.5f,
					0.0f, 0.6f, 
					0.1f, 0.6f, 
					0.1f, 0.5f,

					0.0f, 0.5f,
					0.0f, 0.6f, 
					0.1f, 0.6f, 
					0.1f, 0.5f
			};

			texVertices.put(texturea);
			texVertices.rewind();
			
			vboVertexID = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
			glBufferData(GL_ARRAY_BUFFER, vb, GL_STREAM_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			
			vboTexVertexID = glGenBuffers();		
			glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
			glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			
			
		}


}
