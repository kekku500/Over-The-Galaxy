package main;

import org.lwjgl.Sys;

import game.Game;

public class Main {
	
	public static boolean debugging = true;
	public static final String VERSION = "0.2";
	
	public static void main(String[] args){
		Game game = new Game("Over-The-Galaxy v" + VERSION);
		game.start();
	}
	
	/**
	 * Uses System.out.println(data) when debugging is set TRUE.
	 * @param data
	 */
	public static <T> void debugPrint(T data){
		if(debugging)
			System.out.println(data);
	}
	
	/**
	* Get the time in seconds
	*
	* @return The system time in seconds
	*/
	public static float getTime(){
		return Sys.getTime() * 1f / Sys.getTimerResolution() / 1f;
	}

}