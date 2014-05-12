package input;

import state.Copyable;

public class MsBtnDown implements Copyable<MsBtnDown>{
	
	public boolean left, right, middle;

	public MsBtnDown(boolean left, boolean right, boolean middle) {
		super();
		this.left = left;
		this.right = right;
		this.middle = middle;
	}
	
	public void set(boolean left, boolean right, boolean middle){
		this.left = left;
		this.right = right;
		this.middle = middle;
	}
	
	public boolean get(int i){
		return (i == 0 ? left : (i == 1 ? right : middle));
	}

	@Override
	public MsBtnDown copy() {
		return new MsBtnDown(left, right, middle);
	}

}
