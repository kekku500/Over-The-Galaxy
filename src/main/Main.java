package main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Quat4f;

import utils.R;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import game.Game;
import game.world.culling.BoundingAxis;
import game.world.culling.BoundingCube;
import game.world.culling.Octree;

public class Main {
	
	public static void main(String[] args){
		new Game().start();
	
		
		/*Octree<Obj> octree = new Octree<Obj>(10);
		Obj one = new Obj("1", new BoundingCube(new Vector3f(1,1,1), 1));
		Obj two = new Obj("2", new BoundingCube(new Vector3f(2.1f,1,3), 1));
		Obj three = new Obj("3", new BoundingCube(new Vector3f(-2,1,-2), 1));
		octree.insert(one);
		octree.insert(two);
		octree.insert(three);
		
		System.out.println(octree.getContainer(one));*/
	}
	
}