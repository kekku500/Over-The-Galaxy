package Weapon;

public interface Weapon {
	public String getTexture();
	
	//Laskemoon
	public int getAmmo();
	public int getMaxAmmo();
	public void setMaxAmmo(int newMax);
	
	public int getClipAmount();
	public int getMaxClips();
	public void setMaxClips(int newMax);
	public void addClips(int clips);
	
	public void reload();
	
}
