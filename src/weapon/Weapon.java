package weapon;

import utils.math.Vector2i;

public interface Weapon {
	public Vector2i getTexture();
	public int getID();
	
	//Laskemoon
	public int getAmmo();
	public int getMaxAmmo();
	public void setMaxAmmo(int newMax);
	
	public int getClipAmount();
	public int getMaxClips();
	public void setMaxClips(int newMax);
	public void addClips(int clips);
	
	public void reload();
	public void fire();
	
	public int getDurability();

}
