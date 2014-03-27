package world.sync;

/**
 * Objects that need to have common(same reference) variables should implement this interface.
 * All entities and world implements this interface.
 * @author Kevin
 * @param <T>
 */
public interface Linkable<T> {
	
	/**
	 * Joins together two objects.
	 * @param t Object to take variables from (Object in parameter doesn't change).
	 * @return This entity
	 */
	public T setLink(T t);
	
	/**
	 * @return New object which links to itself.
	 */
	public T getLinked();

}
