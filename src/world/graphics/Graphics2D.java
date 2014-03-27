package world.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.util.HashSet;

import javax.vecmath.Quat4f;

import org.lwjgl.util.glu.GLU;
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
	
	HashSet<UnicodeFont> fonts = new HashSet<UnicodeFont>();
	private UnicodeFont font;
	
	public Graphics2D(){}
	
	public void init(){
		//Create fonts here
		Font awtFont = new Font("Times New Roman", 0, 0);
		font = createFont(awtFont.deriveFont(0, 18));
		createFont(awtFont.deriveFont(0, 20));
		Rectangle.init();
		
	}
	
	public void dispose(){
		Rectangle.dispose();
	}
	
	public boolean setFontSize(int size){
		for(UnicodeFont f: fonts){
			if(size == f.getFont().getSize()){
				font = f;
				return true;
			}
		}
		Game.println("ERROR: No font size " + size + " (create it in Graphics2D class, init method)");
		return false;
	}
	
	public UnicodeFont createFont(Font awtFont){
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

	
	public void drawString(int x, int y, String text){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //for fonts
	    glEnable(GL_TEXTURE_2D);
		font.drawString(x, y, text);
		glBindTexture(GL_TEXTURE_2D, 0);
	    glDisable(GL_TEXTURE_2D);
	    glDisable(GL_BLEND);
	}
	
	public void drawString(int x, int y, String text, Color color){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //for fonts
	    glEnable(GL_TEXTURE_2D);
	    font.drawString(x, y, text, color);
	    glBindTexture(GL_TEXTURE_2D, 0);
	    glDisable(GL_TEXTURE_2D);
	    glDisable(GL_BLEND);
	}
	
	public void drawString(int x, int y, String text, Color color, int startIndex, int endIndex){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); //for fonts
	    glEnable(GL_TEXTURE_2D);
		font.drawString(x, y, text, color, startIndex, endIndex);
	    glBindTexture(GL_TEXTURE_2D, 0);
	    glDisable(GL_TEXTURE_2D);
	    glDisable(GL_BLEND);
	}
	
	public void drawTexture(int textureID, int x, int y, int width, int height, int rotationCenterDegree, boolean cullFace, boolean blend, float alpha){
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
	
	public void drawTexture(int textureID){
		Rectangle.render(textureID);
	}
	
	/*public void drawTexture(int...texturesID){
		
	}*/
	
	public void drawTexture(int textureID, int x, int y, int width, int height, int rotationCenterDegree, float alpha){
		drawTexture(textureID, x, y, width, height, rotationCenterDegree, true, true, alpha);
	}
	
	public void drawTexture(int textureID, int x, int y, int width, int height, int rotationCenterDegree){
		drawTexture(textureID, x, y, width, height, rotationCenterDegree, true, true, 1.0f);
	}
	
	public void drawTexture(int textureID, int x, int y, int width, int height){
		drawTexture(textureID, x, y, width, height, 0);
	}
	
	public void drawTexture(Texture texture, int x, int y){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), 0);
	}
	
	public void drawTexture(Texture texture, int x, int y, float alpha){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), 0, true, true, alpha);
	}
	
	public void drawTexture(Texture texture, int x, int y, int rotationCenterDegree, float alpha){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), rotationCenterDegree, true, true, alpha);
	}
	
	
	public void drawTexture(Texture texture, int x, int y, int rotationCenterDegree){
		drawTexture(texture.getID(), x, y, texture.getWidth(), texture.getHeight(), rotationCenterDegree);
	}
	
	public void drawTexture(Texture texture, int x, int y, float scalex, float scaley){
		drawTexture(texture.getID(), x, y, (int)(texture.getWidth()*scalex), (int)(texture.getHeight()*scalex));
	}
	
	public void drawTexture(Texture texture, int x, int y, float scalex, float scaley, float alpha){
		drawTexture(texture.getID(), x, y, (int)(texture.getWidth()*scalex), (int)(texture.getHeight()*scalex), 0, true, true, alpha);
	}
	
	public void drawTexture(Texture texture, int x, int y, float scalex, float scaley, int rotationCenterDegree){
		drawTexture(texture.getID(), x, y, (int)(texture.getWidth()*scalex), (int)(texture.getHeight()*scalex), rotationCenterDegree);
	}
	
	public UnicodeFont getFont(){
		return font;
	}
	
	public static void perspective2D(){   
	    glMatrixMode(GL_PROJECTION);
	    glLoadIdentity();
	    GLU.gluOrtho2D(0.0f, (float)RenderThread.displayWidth, (float)RenderThread.displayHeight, 0.0f);
	    glMatrixMode(GL_MODELVIEW);
	    glLoadIdentity();
	}

}
