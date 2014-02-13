package game.world.gui.graphics;

import game.Game;

import java.awt.Font;
import java.util.HashMap;
import java.util.HashSet;

import main.Main;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class Graphics {
	
	HashSet<UnicodeFont> fonts = new HashSet<UnicodeFont>();
	private UnicodeFont font;
	
	public Graphics(){}
	
	public void init(){
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //for fonts

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
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
		font.drawString(x, y, text);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void drawString(int x, int y, String text, Color color){
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    font.drawString(x, y, text, color);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public void drawString(int x, int y, String text, Color color, int startIndex, int endIndex){
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
		font.drawString(x, y, text, color, startIndex, endIndex);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public UnicodeFont getFont(){
		return font;
	}

}
