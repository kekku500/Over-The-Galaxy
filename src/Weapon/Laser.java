package Weapon;

public class Laser implements Weapon{
	private int Ammo;
	private int MaxAmmo;
	private int ClipCount;
	private int MaxClips;
	private String texture = "res/models/weapons/Laser.png";
	
	public Laser(){
		Ammo = 100;
		MaxAmmo = 100;
		ClipCount = 5;
		MaxClips = 5;
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
		if(ClipCount > 0){
			Ammo = MaxAmmo;
			ClipCount --;
		}
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

	@Override
	public void fire() {
		if(Ammo > 0){
			Ammo --;
		}
	}

}
