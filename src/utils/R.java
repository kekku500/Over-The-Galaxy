package utils;

import java.io.Serializable;

import main.state.Copyable;

/**
 * R aka reference aka pointer.
 */

public class R<T> implements Copyable<R<T>>, Serializable{ //Reference Object

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	T variable;
	
	public R(T t){
		variable = t;
	}
	
	public R(){}
	
	public void set(T t){
		variable = t;
	}
	
	public T get(){
		return variable;
	}

	@Override
	public boolean equals(Object obj) {
		return variable.equals(obj);
	}

	@Override
	public int hashCode() {
		return variable.hashCode();
	}

	@Override
	public String toString() {
		return variable.toString();
	}

	@Override
	public R<T> copy() {
		return new R<T>(variable);
	}

}
