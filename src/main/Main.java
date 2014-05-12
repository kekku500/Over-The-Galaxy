package main;


import java.util.Iterator;
import java.util.LinkedList;

import javax.vecmath.Quat4f;

import com.bulletphysics.linearmath.QuaternionUtil;

import state.Game;
import utils.Utils;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import utils.math.Vector4f;

public class Main {
	
	static LinkedList<Integer> valuesToBeChecked = new LinkedList<Integer>();
	
	public static void main(String[] args){
		new Game().start();
		
	}
	
}