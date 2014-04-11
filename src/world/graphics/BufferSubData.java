package world.graphics;

import java.nio.FloatBuffer;

public class BufferSubData {
	
	private FloatBuffer data;
	private int offset;
	
	public BufferSubData(FloatBuffer data, int offset) {
		super();
		this.data = data;
		this.offset = offset;
	}
	
	public FloatBuffer getData() {
		return data;
	}
	
	public void setData(FloatBuffer data) {
		this.data = data;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setOffsetByFloat(int offset){
		this.offset = offset * (Float.SIZE / 8);
	}
	

}
