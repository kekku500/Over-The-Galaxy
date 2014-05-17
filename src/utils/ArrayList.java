package utils;

import main.state.Copyable;

public class ArrayList<T> extends java.util.ArrayList<T> implements Copyable<ArrayList<T>>{

	private static final long serialVersionUID = 1L;

	@Override
	public ArrayList<T> copy() {
		ArrayList<T> refList = new ArrayList<T>();
		refList.addAll(this);
		return refList;
	}


}
