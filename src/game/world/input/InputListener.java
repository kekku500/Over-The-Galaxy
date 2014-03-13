package game.world.input;

import java.util.HashSet;
import java.util.Set;

public class InputListener {
	
	public static Set<Input> inputCheckObjects = new HashSet<Input>();
	
	public InputListener(Input i){
		inputCheckObjects.add(i);
	}

}
