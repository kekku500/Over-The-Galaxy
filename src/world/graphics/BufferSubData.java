package world.graphics;

import java.nio.FloatBuffer;

public class BufferSubData {
	
	private FloatBuffer data;
	private int offset;
	
	public BufferSubData(){}
	
	public BufferSubData(FloatBuffer data, int offset) {
		super();
		this.data = data;
		this.offset = offset;
	}
	
	public FloatBuffer getData() {
		return data;
	}
	
	/**
	 * Puts data into floatbuffer and rewinds it.
	 * @param fb
	 */
	public void put(float[] newdata){
		data.put(newdata);
		data.rewind();
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public BufferSubData setOffsetByFloat(int offset){
		this.offset = offset * (Float.SIZE / 8);
		return this;
	}
	

}
