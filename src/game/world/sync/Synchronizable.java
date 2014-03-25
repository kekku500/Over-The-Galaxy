package game.world.sync;

public interface Synchronizable<T> {
	
	public T setLink(T t);
	
	public T getLinked();

}
