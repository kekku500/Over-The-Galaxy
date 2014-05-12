package world.gui.hud;

import java.util.ArrayList;
import java.util.List;

import world.EntityManager;
import world.entity.smart.Player;
import world.gui.AbstractComponent;
import world.gui.HUDManager;


public class HeadsUpDisplay{
	public static List<AbstractComponent> components = new ArrayList<AbstractComponent>();
	private Player player;
	private HUDManager hudManager;
	
	
	public HeadsUpDisplay(Player player, HUDManager hudManager) {
		this.player = player;
		this.hudManager = hudManager;
		
		components.add(new WeaponDisplay(player));
		components.add(new ShipStat(player));
		components.add(new DialogueBox());
		components.add(new MiniMap(hudManager, player));
		components.add(new Map(hudManager, player));
		
		for(AbstractComponent c: components){
			hudManager.add(c);
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
