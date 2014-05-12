package state;

import java.util.ArrayList;

public class StateVariable <T extends Copyable<T>>{
	
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
		return vars.get(RenderState.getRenderingId());
	}
	
	public T updating(){
		return vars.get(RenderState.getUpdatingId());
	}
	
	public T uptodate(){
		return vars.get(RenderState.getUpToDateId());
	}

}
