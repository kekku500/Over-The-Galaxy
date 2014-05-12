package input;

/**
 * Every entity which has implemented Input interface will be check for inputs
 * using methods below.
 * @author Kevin
 */

public interface InputReciever {

	/**
	 * Returns integer value of a key press. Does not support continuous key holding.
	 * Once key pressing has been detected, release is required to detect event again.
	 * @param k
	 */
	public void checkKeyboardInput(int k);
	
	/**
	 * Returns integer value of a mouse press. Does not support continuous mouse button holding.
	 * Once mouse button pressing has been detected, release is required to detect event again.
	 * @param m
	 */
	public void checkMouseInput(int m);

}
