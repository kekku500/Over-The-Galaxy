package game.world.graphics;

import static org.lwjgl.opengl.GL11.*;
import game.Game;
import game.threading.RenderThread;

import java.awt.Font;
import java.util.HashSet;

import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Graphics2D {
	
	HashSet<UnicodeFont> fonts = new HashSet<UnicodeFont>();
	private UnicodeFont font;
	
	public Graphics2D(){}
	
	public void init(){
		//Create fonts here
		Font awtFont = new Font("Times New Roman", 0, 0);
		font = createFont(awtFont.deriveFont(0, 18));
		createFont(awtFont.deriveFont(0, 20));
	}
	
	public boolean setFontSize(int size){
		for(UnicodeFont f: fonts){
			if(size == f.getFont().getSize()){
				font = f;
				return true;
			}
		}
		Game.print("ERROR: No font size " + size + " (create it in Graphics class)");
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
