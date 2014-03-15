package Weapon;

public class Laser implements Weapon{
	private int Ammo = 100;
	private int MaxAmmo = 100;
	private int ClipCount = 5;
	private int MaxClips = 5;
	private String texture = "res/models/weapons/Laser.png";
	
	public Laser(){
		
	}

	@Override
	public String getTexture() {
		return texture;
	}

	@Override
	public int getAmmo() {
		return Ammo;
	}

	@Override
	public int getMaxAmmo() {
		return MaxAmmo;
	}

	@Override
	public int getClipAmount() {
		return ClipCount;
	}

	@Override
	public void reload() {
		Ammo = MaxAmmo;
		ClipCount --;		
	}

	@Override
	public void addClips(int clips) {
		ClipCount += clips;
		
	}

	@Override
	public void setMaxAmmo(int newMax) {
		MaxAmmo = newMax;		
	}

	@Override
	public int getMaxClips() {
		return MaxClips;
	}

	@Override
	public void setMaxClips(int newMax) {
		MaxClips = newMax;
		
	}

}
