package game.world.gui.hud;


import java.util.ArrayList;
import java.util.List;

import game.world.World;
import game.world.entities.Player;


public class HeadsUpDisplay{
	public List<HudComponent> components = new ArrayList<HudComponent>();
	private Player player;
	private World world;
	

	public HeadsUpDisplay(Player player, World world) {
		this.player = player;
		this.world = world;
		
		components.add(new HudBase());
		components.add(new WeaponDisplay(player.getWeapon().getTexture()));
		world.addComponent(components.get(0));
		world.addComponent(components.get(1));

	}

	public void update() {
		for(HudComponent c: components){
			c.update();
		}
	}

}
