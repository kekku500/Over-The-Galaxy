package game.world;

import game.world.entities.Line;
import game.world.sync.RenderRequest;
import game.world.sync.Request;
import game.world.sync.Request.Action;
import game.world.sync.RequestManager;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

public class Grid {
	
	private boolean enabled = true;
	
	//configure grid
	private int xsize = 800; //Size of lines parallel to x axis
	private int zsize = 800; //Size of lines parallel to z axis
	private int gap = 25; //Distance between two lines
	private boolean boldZeroAxisLines = true;
	
	private ArrayList<Line> xlines = new ArrayList<Line>();
	private ArrayList<Line> zlines = new ArrayList<Line>();
	
	World world;
	
	
	public Grid(World world){
		this.world = world;
		//add x lines
		RequestManager sync = world.getState().getSyncManager();
		
		for(int i=0;i<(int)(zsize/gap);i++){
			blender.model.custom.Line lineVBO = new blender.model.custom.Line(new Vector3f(xsize,0,0));
			Line l = new Line();
			l.setModel(lineVBO);
 			l.setLength(xsize);
			Request request = new RenderRequest(Action.INIT, l);
			sync.add(request);
			xlines.add(l);
		}
		//add z lines
		
		for(int i=0;i<(int)(xsize/gap);i++){
			blender.model.custom.Line lineVBO = new blender.model.custom.Line(new Vector3f(0,0,zsize));
			Line l = new Line();
			l.setModel(lineVBO);
 			l.setLength(zsize);
			Request request = new RenderRequest(Action.INIT, l);
			sync.add(request);
			zlines.add(l);
		}
	}
	
	public void addToWorld(World world){
		for(Line l: xlines)
			world.addEntity(l);
		for(Line l: zlines)
			world.addEntity(l);
	}
	
	public Vector3f getCamPos(){
		return world.getCamera().getPos();
	}
	
	public void update(){
		if(!enabled)
			return;
		//set lines to correct pos
		Vector3f cam = getCamPos();
		int i = (int)(cam.z-zsize/2)/gap;
		for(Line xline: xlines){
			Vector3f pos = xline.getMotionState().origin;
			int zpos = i*gap;
			if(boldZeroAxisLines)
				if(zpos == 0)
					xline.setLineWidth(5);
				else
					xline.setLineWidth(1);
			pos.z = zpos;
			pos.x = cam.x-xline.length()/2;
			i++;
		}
		i = (int)(cam.x-xsize/2)/gap;
		for(Line zline: zlines){
			Vector3f pos = zline.getMotionState().origin;
			int xpos = i*gap;
			if(boldZeroAxisLines)
				if(xpos == 0)
					zline.setLineWidth(5);
				else
					zline.setLineWidth(1);
			pos.x = i*gap;
			pos.z = cam.z-zline.length()/2;
			i++;
		}
		
		
	}
	
	public void render(){
		if(!enabled)
			return;

		GL11.glColor3f(1f, 1f, 1f);
		for(Line l: xlines){
			l.render();
		}
		for(Line l: zlines){
			l.render();
		}
	}
	
	public void dispose(){
		for(Line l: xlines){
			l.dispose();
		}
		for(Line l: zlines){
			l.dispose();
		}
	}
	
	public ArrayList<Line> getLinesX(){
		return xlines;
	}
	
	public ArrayList<Line> getLinesZ(){
		return zlines;
	}

}
