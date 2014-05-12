package utils;

import state.Copyable;

public class LinkedList<T> extends java.util.LinkedList<T> implements Copyable<LinkedList<T>>{

	private static final long serialVersionUID = 1L;

	@Override
	public LinkedList<T> copy() {
		LinkedList<T> list = new LinkedList<T>();
		list.addAll(this);
		return list;
	}

}
