package world.entity.gui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import world.World;
import world.entity.Entity;

public class HudExample extends AbstractComponent{

		private static int vboVertexID;
		
		public HudExample(float x, float y){
			setPosition(x, y);
		}
		
		@Override
		public Entity setLink(Entity t) {
			if(t instanceof HudExample){
				HudExample wt = (HudExample)t;
				
			}

			return this;
		}
		

		@Override
		public Entity getLinked() {
			return new HudExample(getPosition().x, getPosition().y).setLink(this);
		}
		
		@Override
		public void update(float dt) {

		}
		
		@Override
		public void render(){
			glPushMatrix(); //save current transformations
			
			Vector2f pos = getPosition();
			
			glTranslatef(pos.x, pos.y, 0);
	        
			// Bind the vertex buffer
			glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
			glVertexPointer(2, GL_FLOAT, 0, 0);
		    
		    glEnableClientState(GL_VERTEX_ARRAY);
		    
		    //color example
		    GL11.glColor3f(1f, 0f, 3f);
		    
			glDrawArrays(GL_QUADS, 0, 4);
			
		    glDisableClientState(GL_VERTEX_ARRAY);

		    glPopMatrix(); //reset transformations
		}
		
		
		@Override
		public void dispose(){
		    glDeleteBuffers(vboVertexID);
		}

		public static void init() {
			FloatBuffer vb = BufferUtils.createFloatBuffer(2 * 4);
			vb.put(new float[]{0,0, 0,100, 100,100, 100,0}); //clockwise, front face
			vb.rewind();
			
			vboVertexID = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
			glBufferData(GL_ARRAY_BUFFER, vb, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			
		}


}
