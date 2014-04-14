package world.entity.gui.hud;

import java.util.ArrayList;
import java.util.List;

import world.World;
import world.entity.gui.AbstractComponent;
import world.entity.smart.Player;


public class HeadsUpDisplay{
	public static List<AbstractComponent> components = new ArrayList<AbstractComponent>();
	private Player player;
	private World world;
	
	
	public HeadsUpDisplay(Player player, World world) {
		this.player = player;
		this.world = world;
		
		components.add(new WeaponDisplay(player));
		components.add(new ShipStat(player));
		components.add(new DialogueBox());
		components.add(new MiniMap(world, player));
		components.add(new Map(world, player));
		
		for(AbstractComponent c: components){
			world.addEntity(c);
		}
		
	}
	
	public void init() {
		for(AbstractComponent c: components){
			c.init();
		}
	}
	
	public void dispose(){
		for(AbstractComponent c: components){
			c.dispose();
		}
	}

}
