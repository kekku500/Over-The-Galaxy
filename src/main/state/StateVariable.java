package main.state;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Container of multiple copies of a variable.
 */
public class StateVariable <T extends Copyable<T>> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public ArrayList<T> vars;
	
	public StateVariable(T t){
		this(t, State.STATE_COUNT);
	}
	
	public StateVariable(T t, int count){
		vars = new ArrayList<T>();
		vars.add(t);
		for(int i = 1;i<count;i++){
			vars.add(t.copy());
		}
	}
	
	public void replaceAll(T t){
		int count = vars.size();
		vars.clear();
		vars.add(t);
		for(int i = 1;i<count;i++){
			vars.add(t.copy());
		}
	}
	
	public T rendering(){
		return vars.get(RenderState.rendering());
	}
	
	public T updating(){
		return vars.get(RenderState.updating());
	}
	
	public T uptodate(){
		return vars.get(RenderState.uptodate());
	}

}
