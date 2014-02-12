package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;

import game.Game;

public class Main {
	
	public static boolean debugging = true;
	public static final String VERSION = "0.4";
	
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