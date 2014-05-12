package utils;

import state.Copyable;

public class HashSet<T> extends java.util.HashSet<T> implements Copyable<HashSet<T>>{

	private static final long serialVersionUID = 1L;

	@Override
	public HashSet<T> copy() {
		HashSet<T> referencedCopy =  new HashSet<T>();
		referencedCopy.addAll(this);
		return referencedCopy;
	}

}
