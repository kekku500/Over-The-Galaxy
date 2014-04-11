package world.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferSubData;

public class VBORender2D {
	
	public static void drawVBO(int vertexCount, int vboVertexID, BufferSubData vertexModifier, int vboTexVertexID, BufferSubData texVertexModifider, int texture){
		//Setup vertices
	    glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
	    if(vertexModifier != null)
	    	glBufferSubData(GL_ARRAY_BUFFER, vertexModifier.getOffset(), vertexModifier.getData());
	    glVertexPointer(2, GL_FLOAT, 0, 0);
		
	    //Texture vertices
	    if(texture != 0){
			glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
			if(texVertexModifider != null)
				glBufferSubData(GL_ARRAY_BUFFER, texVertexModifider.getOffset(), texVertexModifider.getData());
			glTexCoordPointer(2, GL_FLOAT, 0, 0);
	    }

		
		//Finished vertices stuff
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		//Enable texture and bind it
	    if(texture != 0){
			glEnable(GL_TEXTURE_2D);
			glActiveTexture(GL_TEXTURE0);glBindTexture(GL_TEXTURE_2D, texture);	
	    }

		
		//Enable vbo
	    glEnableClientState(GL_VERTEX_ARRAY);
	    if(texture != 0)
	    	glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		//Draw from vbo
		glDrawArrays(GL_QUADS, 0, vertexCount * 2);
		
		//Disable vbo
		glDisableClientState(GL_VERTEX_ARRAY);
	    if(texture != 0)
	    	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		
		//Unbind texture and disable texturing
	    if(texture != 0){
			glActiveTexture(GL_TEXTURE0);glBindTexture(GL_TEXTURE_2D, 0);
	   	 	glDisable(GL_TEXTURE_2D);
	    }
	}

}
