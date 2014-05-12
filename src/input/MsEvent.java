package input;

public class MsEvent {
	
	public final int x, y, button;
	public final boolean state;
	
	public final int dx, dy, dwheel;
	
	public MsEvent(int x, int y, int button, boolean state, int dx, int dy, int dwheel) {
		super();
		this.x = x;
		this.y = y;
		this.button = button;
		this.state = state;
		this.dx = dx;
		this.dy = dy;
		this.dwheel = dwheel;
	}

}
