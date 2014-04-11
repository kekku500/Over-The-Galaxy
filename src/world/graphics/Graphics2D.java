package world.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferSubData;

import java.awt.Font;
import java.nio.FloatBuffer;
import java.util.HashSet;

import javax.vecmath.Quat4f;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import com.bulletphysics.linearmath.QuaternionUtil;

import resources.Resources;
import resources.texture.Texture;
import state.Game;
import threading.RenderThread;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Vector3f;

/**
 * A class to make drawing 2d stuff easier.
 * @author Kevin
 */
public class Graphics2D {
	
	private static HashSet<UnicodeFont> fonts = new HashSet<UnicodeFont>();
	private static UnicodeFont font;
	
	public static void init(){
		//Create fonts here
		Font awtFont = new Font("Times New Roman", 0, 0);
		font = createFont(awtFont.deriveFont(0, 18));
		createFont(awtFont.deriveFont(0, 20));
		Rectangle.init();
		
	}
	
	public static void dispose(){
		Rectangle.dispose();
	}
	
	public static boolean setFontSize(int size){
		for(UnicodeFont f: fonts){
			if(size == f.getFont().getSize()){
				font = f;
				return true;
			}
		}
		Game.println("ERROR: No font size " + size + " (create it in Graphics2D class, init method)");
		return false;
	}
	
	public static UnicodeFont createFont(Font awtFont){
		UnicodeFont font = new UnicodeFont(awtFont);
		font.addAsciiGlyphs();
		ColorEffect e = new ColorEffect();
		e.setColor(java.awt.Color.white);
		font.getEffects().add(e);
		try {
			font.loadGlyphs();
		} catch (SlickException e1) {
			e1.printStackTrace();
		}
		fonts.add(font);
		return font;
	}

	
	public static void drawString(int x, int y, String text){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //for fonts
	    glEnable(GL_TEXTURE_2D);
		font.drawString(x, y, text);
		glBindTexture(GL_TEXTURE_2D, 0);
	    glDisable(GL_TEXTURE_2D);
	    glDisable(GL_BLEND);
	}
	
	public static void drawString(int x, int y, String text, Color color){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //for fonts
	    glEnable(GL_TEXTURE_2D);
	    font.drawString(x, y, text, color);
	    glBindTexture(GL_TEXTURE_2D, 0);
	    glDisable(GL_TEXTURE_2D);
	    glDisable(GL_BLEND);
	}
	
	public static void drawString(int x, int y, String text, Color color, int startIndex, int endIndex){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //for fonts
	    glEnable(GL_TEXTURE_2D);
		font.drawString(x, y, text, color, startIndex, endIndex);
	    glBindTexture(GL_TEXTURE_2D, 0);
	    glDisable(GL_TEXTURE_2D);
	    glDisable(GL_BLEND);
	}
	
	public static void drawTexture(int textureID, int x, int y, int width, int height, int rotationCenterDegree, boolean cullFace, boolean blend, float alpha){
		glPushMatrix();{
			if(cullFace)
				glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			
			
			if(blend){
				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //corner color (i think)
			}
			if(alpha < 1.0f)
				glColor4f(1.0f, 1.0f, 1.0f, alpha);

			//Matrix for transformations
			Matrix4f m = new Matrix4f();
			m.setIdentity();
			
			//Rotation around center
			m.translate(-0.5f, -0.5f, 0);
			m.rotate(Utils.rads(rotationCenterDegree), new Vector3f(0,0,1));
			m.translate(0.5f, 0.5f, 0);
			
			//Scaling
			m.scale(width, height, 0);

			//Translation
			m.translate(x, y, 0);

			glMultMatrix(m.asFlippedFloatBuffer());

			Rectangle.render(textureID);
			
			if(blend)
				glDisable(GL_BLEND);
			if(cullFace)
				glDisable(GL_CULL_FACE);
		}glPopMatrix();
	}
	
	public static void drawTexture(int textureID){
		Rectangle.render(textureID);
	}
	
	public static void drawTexture(int textureID, int x, int y, int width, int height, int rotationCenterDegree, float alpha){
		drawTexture(textureID, x, y, width, height, rotationCenterDegree, true, true, alpha);
	}
	
	public static void drawTexture(int textureID, int x, int y, int width, int height, int rotationCenterDegree){
		drawTexture(textureID, x, y, width, height, rotationCenterDegree, true, true, 1.0f);
	}
	
	public static void drawTexture(int textureID, int x, int y, int width, int height){
		drawTexture(textureID, x, y, width, height, 0);
	}
	
	public static void drawTexture(Texture texture, int x, int y){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), 0);
	}
	
	public static void drawTexture(Texture texture, int x, int y, float alpha){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), 0, true, true, alpha);
	}
	
	public static void drawTexture(Texture texture, int x, int y, int rotationCenterDegree, float alpha){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), rotationCenterDegree, true, true, alpha);
	}
	
	
	public static void drawTexture(Texture texture, int x, int y, int rotationCenterDegree){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), rotationCenterDegree);
	}
	
	public static void drawTexture(Texture texture, int x, int y, float scalex, float scaley){
		drawTexture(texture.getID(), x, y, (int)(texture.getWidth()*scalex), (int)(texture.getHeight()*scalex));
	}
	
	public static void drawTexture(Texture texture, int x, int y, float scalex, float scaley, float alpha){
		drawTexture(texture.getID(), x, y, (int)(texture.getWidth()*scalex), (int)(texture.getHeight()*scalex), 0, true, true, alpha);
	}
	
	public static void drawTexture(Texture texture, int x, int y, float scalex, float scaley, int rotationCenterDegree){
		drawTexture(texture.getID(), x, y, (int)(texture.getWidth()*scalex), (int)(texture.getHeight()*scalex), rotationCenterDegree);
	}
	
	public static UnicodeFont getFont(){
		return font;
	}
	
	public static void drawVBOMultipleTextures(int vertexCount, int vboVertexID, BufferSubData vertexModifier, int vboTexVertexID, BufferSubData texVertexModifider, int...textures){
		//Setup vertices
	    glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
	    if(vertexModifier != null)
	    	glBufferSubData(GL_ARRAY_BUFFER, vertexModifier.getOffset(), vertexModifier.getData());
	    glVertexPointer(2, GL_FLOAT, 0, 0);	
	    
	    //Bind textures
		int totalTextures = 0;
		boolean hasValidTexture = false;
		for(int i=0;i<textures.length;i++){
			int textureid = textures[i];
			if(textureid != 0 && !hasValidTexture)
				hasValidTexture = true;
			if(textureid != 0){
		        glActiveTexture(GL_TEXTURE0+totalTextures); glBindTexture(GL_TEXTURE_2D, textureid);
				totalTextures++;
			}
		}
		
		//Setup texture vertices
		if(hasValidTexture){
		    glEnable(GL_TEXTURE_2D);
			glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
			if(texVertexModifider != null)
				glBufferSubData(GL_ARRAY_BUFFER, texVertexModifider.getOffset(), texVertexModifider.getData());
			glTexCoordPointer(2, GL_FLOAT, 0, 0);
	    }

		//Finished vertices stuff
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		//Enable vbo
	    glEnableClientState(GL_VERTEX_ARRAY);
	    if(hasValidTexture)
	    	glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		//Draw from vbo
		glDrawArrays(GL_QUADS, 0, vertexCount);
		
		//Disable vbo
		glDisableClientState(GL_VERTEX_ARRAY);
	    if(hasValidTexture)
	    	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		
		//Unbind texture and disable texturing
	    if(hasValidTexture){
        	totalTextures--;
    		while(totalTextures >= 0){
    			glActiveTexture(GL_TEXTURE0+totalTextures); glBindTexture(GL_TEXTURE_2D, 0);
    			totalTextures--;
    		}
    	    glDisable(GL_TEXTURE_2D);
	    }
	}
	
	public static void drawVBO(int vertexCount, int vboVertexID, BufferSubData vertexModifier, int vboTexVertexID, BufferSubData texVertexModifider, int...textures){
		drawVBOMultipleTextures(vertexCount, vboVertexID, vertexModifier, vboTexVertexID, texVertexModifider, textures);
	}
	
	public static void drawVBO(int vertexCount, int vboVertexID, int vboTexVertexID, int...textures){
		drawVBO(vertexCount,vboVertexID, null,vboTexVertexID, null,textures);
	}
	
	public static void drawVBO(int vertexCount, int vboVertexID){
		drawVBO(vertexCount,vboVertexID, null,0, null,0);
	}
	
	public static void perspective2D(){   
	    glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
	    GLU.gluOrtho2D(0.0f, (float)RenderThread.displayWidth, (float)RenderThread.displayHeight, 0.0f);
	    glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
		glViewport(0, 0, RenderThread.displayWidth, RenderThread.displayHeight);
	}

}
