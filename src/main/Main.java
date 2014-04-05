package main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Quat4f;

import resources.texture.SpriteSheet;
import state.Game;
import utils.R;
import utils.math.Matrix3f;
import utils.math.Matrix4f;
import utils.math.Transform;
import utils.math.Vector3f;
import world.culling.BoundingAxis;
import world.culling.BoundingCube;
import world.culling.Octree;

public class Main {
	
	public static void main(String[] args){
		new Game().start();
		
		//new SpriteSheet();
	}
	
}