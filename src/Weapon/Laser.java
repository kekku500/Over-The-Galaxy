package Weapon;

import utils.math.Vector2i;

public class Laser implements Weapon{
	private int Ammo;
	private int MaxAmmo;
	private int ClipCount;
	private int MaxClips;
	private int id;
	private int durability = 100;
	private Vector2i texture = new Vector2i(31,35);
	
	public Laser(){
		Ammo = 100;
		MaxAmmo = 100;
		ClipCount = 5;
		MaxClips = 5;
	}
	
	@Override
	public Vector2i getTexture() {
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
	
	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public int getDurability() {
		return durability;
	}

}