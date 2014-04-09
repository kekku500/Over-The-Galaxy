package world.entity.gui.hud;

import java.util.ArrayList;
import java.util.List;

import world.World;
import world.entity.smart.Player;


public class HeadsUpDisplay{
public List<HudComponent> components = new ArrayList<HudComponent>();
private Player player;
private World world;


public HeadsUpDisplay(Player player, World world) {
this.player = player;
this.world = world;

// components.add(new HudBase());
components.add(new WeaponDisplay(player));
components.add(new ShipStat(player));
components.add(new DialogueBox());

for(HudComponent c: components){
	world.addEntity(c);
}

}

public void update() {
for(HudComponent c: components){
c.update();
}
}

}
