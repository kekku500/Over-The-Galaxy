package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import resources.Resources;
import utils.ArrayList;
import entity.sheet.Entity;
import entitymanager.Level;
import main.state.Game;
import math.Matrix4f;
import math.Quat4f;
import math.Transform;
import math.Vector3f;

public class Main {
	
	public static void main(String[] args){
		//new Game().start();
		Menüü.menüü();
	}
	
}