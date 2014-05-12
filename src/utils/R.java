package utils;

import state.Copyable;

public class R<T> implements Copyable<R<T>>{ //Reference Object

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
