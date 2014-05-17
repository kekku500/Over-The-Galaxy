package math;

import java.io.Serializable;

public class Vector2i implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;

	public Vector2i(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
}
