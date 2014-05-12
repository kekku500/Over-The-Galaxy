package input;

public class KbEvent {
	
	public final int key;
	public final char character;
	public final boolean state;
	
	public KbEvent(int key, char character, boolean state) {
		super();
		this.key = key;
		this.character = character;
		this.state = state;
	}

}
