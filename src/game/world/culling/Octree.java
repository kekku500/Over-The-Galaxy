package game.world.culling;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import utils.math.Vector3f;

public class Octree<T extends Generalizable> {
	
	private int MAX_OBJECTS = 100;
	private int MAX_LEVEL = -1;
	private int level;
	
	public List<T> objects = new ArrayList<T>();
	private Octree<T>[] nodes = new Octree[8];
	
	private BoundingCube bounds;
	
	private List<T> retrieveList = new ArrayList<T>();
	
	/**
	 * @param size Size of the octree
	 */
	public Octree(float size){
		bounds = new BoundingCube(new Vector3f(-size/2f,-size/2f,-size/2f), size);
	}
	
	public Octree(Vector3f pos, float sizeInc, int level){
		bounds = new BoundingCube(pos, sizeInc);
		this.level = level;
	}
	
	public Octree(BoundingCube b, int level){
		bounds = b;
	}
	
	public BoundingCube getBounds(){
		return bounds;
	}
	
	private static final Integer[] pq = new Integer[]{0,1,2,3,4,5,6,7};
	public List<T> retrieve(BoundingAxis frustumAABB){
		retrieveList.clear();
		//System.out.println("Checking octree level " + level);
		if(nodes[0] != null){ //Any nodes to check or add all in current level
			List<Integer> possibleIndexes = new ArrayList<Integer>(Arrays.asList(pq));
			int index = getIndex(frustumAABB, possibleIndexes); //Check if box fits inside node or all nodes must be checked
			//System.out.println("Has nodes and box fits in index " + index);
			//index = -1, Check all current octree nodes
			//index != -1, Check only indexed node
			if(index != -1){ //Going into selected index
				retrieveList.addAll(nodes[index].retrieve(frustumAABB)); //Get everything from node
			}else{ //Check all nodes
				//System.out.println("Possible indexes are " + possibleIndexes);
				for(Integer i: possibleIndexes){
					//Check node if it intersects with frustum
					//if(nodes[i].getBounds().intersects(frustumAABB.getBoundingAxis())){
						//System.out.println("Checking subnode index " + i);
						retrieveList.addAll(nodes[i].retrieve(frustumAABB));
					//}
				}
			}
		}
		//System.out.println("ADDING OBJECTS FROM BOUNDS " + bounds + " at level " + level);
		retrieveList.addAll(objects);

		return retrieveList;
	}
	
	/**
	 * Returns all objects from current octree (doesn't return nodes content)
	 * @return
	 */
	public List<T> retrieve(){
		return objects;
	}
	
	/**
	 * Inserts object into corresponding node.
	 * @param object
	 * @return If inserting was successful.
	 */
	public boolean insert(T object){
		//System.out.println("Inserting " + object);
		if(object.getBoundingAxis() != null){
			//Has children nodes
			if(nodes[0] != null){
				//System.out.println("Has children nodes");
				int index = getIndex(object.getBoundingAxis());
				//System.out.println(object + " should be in index " + index);
				if(index != -1){ //Fits into any children node then add it to children node aswell
					nodes[index].insert(object);
					return true;
				}
			}
			//System.out.println("Added object to level " + level + " " + bounds);
			objects.add(object); //Add object to this node
			if(MAX_LEVEL == -1 || level < MAX_LEVEL){ //Create child nodes only if current level has not reached maximum level
				//System.out.println("Node has not reached max level, can split");
				if(objects.size() > MAX_OBJECTS){ //Object limit reached, split current node into children nodes
					//System.out.println("Node object limit reached, splitting.");
					if(nodes[0] == null){ //Only if not splitted yet
						//System.out.println("Splitted the node");
						split();
					}
					//System.out.println("Sorting all objects in this node to subnodes (" + objects.size() + ")");
					Iterator<T> itr = objects.listIterator();
					while(itr.hasNext()){
						T obj = itr.next();
						int index = getIndex(obj.getBoundingAxis());
						//System.out.println("Sorting " + obj + " index " + index);
						if(index != -1){
							nodes[index].insert(obj);
							itr.remove();
						}
					}
				}
			}
			//System.out.println("DONE!");
			return true;
		}
		return false;
	}

	/**
	 * @param queryBounds 
	 * @param possibleQuadrant Returns nodes which need further checking.
	 * @return Index in which node object belongs to. Returns -1 if objects intersects with multiple nodes.
	 */
	private int getIndex(BoundingAxis objectBounds, List<Integer> possibleQuadrant){
		//BoundingAxis objectBounds = queryBounds.getBoundingAxis();
		int index = -1;
		float Xmid = bounds.getMin().x+bounds.getSize()/2f;
		float Ymid = bounds.getMin().y+bounds.getSize()/2f;
		float Zmid = bounds.getMin().z+bounds.getSize()/2f;
		boolean leftQuadrant = (objectBounds.getMax().z < Zmid); //Z
		boolean rightQuadrant = (objectBounds.getMin().z > Zmid); //-Z
		
		boolean bottomQuadrant = (objectBounds.getMax().y < Ymid); //-Y
		boolean topQuadrant = (objectBounds.getMin().y > Ymid); //Y
		
		boolean nearQuadrant = (objectBounds.getMax().x < Xmid); //X
		boolean farQuadrant = (objectBounds.getMin().x > Xmid); //-X
		
		if(leftQuadrant){ //0, 2, 4, 6
			possibleQuadrant.remove((Object)1);possibleQuadrant.remove((Object)3);
			possibleQuadrant.remove((Object)5);possibleQuadrant.remove((Object)7);
			if(bottomQuadrant){ //0, 2
				if(nearQuadrant){
					index = 0;
				}else if(farQuadrant){
					index = 2;
				}
			}else if(topQuadrant){ //4, 6
				if(nearQuadrant){
					index = 4;
				}else if(farQuadrant){
					index = 6;
				}
			}
		}else if(rightQuadrant){ //1, 3, 5, 7
			possibleQuadrant.remove((Object)0);possibleQuadrant.remove((Object)2);
			possibleQuadrant.remove((Object)4);possibleQuadrant.remove((Object)6);
			if(bottomQuadrant){ //1, 3
				if(nearQuadrant){
					index = 1;
				}else if(farQuadrant){
					index = 3;
				}
			}else if(topQuadrant){// 5, 7
				if(nearQuadrant){
					index = 5;
				}else if(farQuadrant){
					index = 7;
				}
			}
		}
		if(index == -1){
			if(bottomQuadrant){
				possibleQuadrant.remove((Object)4);
				possibleQuadrant.remove((Object)6);
				possibleQuadrant.remove((Object)5);
				possibleQuadrant.remove((Object)7);
			}else if(topQuadrant){
				possibleQuadrant.remove((Object)0);
				possibleQuadrant.remove((Object)2);
				possibleQuadrant.remove((Object)1);
				possibleQuadrant.remove((Object)3);
			}
			if(nearQuadrant){
				possibleQuadrant.remove((Object)2);
				possibleQuadrant.remove((Object)3);
				possibleQuadrant.remove((Object)6);
				possibleQuadrant.remove((Object)7);
			
			}else if(farQuadrant){
				possibleQuadrant.remove((Object)0);
				possibleQuadrant.remove((Object)4);
				possibleQuadrant.remove((Object)1);
				possibleQuadrant.remove((Object)5);
			}
		}
		return index;
	}
		
	/**
	 * @param queryBounds
	 * @return Index in which node object belongs to. Returns -1 if objects intersects with multiple nodes.
	 */
	private int getIndex(BoundingAxis objectBounds){
		//BoundingAxis objectBounds = queryBounds.getBoundingAxis();
		int index = -1;
		float Xmid = bounds.getMin().x+bounds.getSize()/2f;
		float Ymid = bounds.getMin().y+bounds.getSize()/2f;
		float Zmid = bounds.getMin().z+bounds.getSize()/2f;
		
		boolean leftQuadrant = (objectBounds.getMax().z < Zmid); //Z
		boolean bottomQuadrant = (objectBounds.getMax().y < Ymid); //Y
		boolean nearQuadrant = (objectBounds.getMax().x < Xmid); //X
	
		if(leftQuadrant){ //0, 2, 4, 6
			if(bottomQuadrant){ //0, 2
				if(nearQuadrant){
					index = 0;
				}else if(objectBounds.getMin().x > Xmid){
					index = 2;
				}
			}else if(objectBounds.getMin().y > Ymid){ //4, 6
				if(nearQuadrant){
					index = 4;
				}else if(objectBounds.getMin().x > Xmid){
					index = 6;
				}
			}
		}else if(objectBounds.getMin().z > Zmid){ //1, 3, 5, 7
			if(bottomQuadrant){ //1, 3
				if(nearQuadrant){
					index = 1;
				}else if(objectBounds.getMin().x > Xmid){
					index = 3;
				}
			}else if(objectBounds.getMin().y > Ymid){// 5, 7
				if(nearQuadrant){
					index = 5;
				}else if(objectBounds.getMin().x > Xmid){
					index = 7;
				}
			}
		}
		return index;
	}
	
	/**
	 * Recursively remove objects from all inner nodes.
	 */
	public void clear(){
		objects.clear();
		for(int i=0;i<nodes.length;i++){
			if(nodes[i] != null){
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}
	
	/**
	 * Splits current octree into nodes (does not put objects into corresponding nodes)
	 */
	public void split(){
		Vector3f childPos = bounds.getMin().copy();
		float childSize = bounds.getSize()/2f;
		int childLevel = level+1;
		
		nodes[0] = new Octree<T>(childPos.addGet(0, 0, 0), childSize, childLevel);
		nodes[1] = new Octree<T>(childPos.addGet(0, 0, childSize), childSize, childLevel);
		nodes[2] = new Octree<T>(childPos.addGet(childSize, 0, 0), childSize, childLevel);
		nodes[3] = new Octree<T>(childPos.addGet(childSize, 0, childSize), childSize, childLevel);
		
		nodes[4] = new Octree<T>(childPos.addGet(0, childSize, 0), childSize, childLevel);
		nodes[5] = new Octree<T>(childPos.addGet(0, childSize, childSize), childSize, childLevel);
		nodes[6] = new Octree<T>(childPos.addGet(childSize, childSize, 0), childSize, childLevel);
		nodes[7] = new Octree<T>(childPos.addGet(childSize, childSize, childSize), childSize, childLevel);
	}
	
	@Override
	public String toString() {
		String r = "Octree [level=" + level + ", bounds="
				+ bounds + ", objects=\n";
		for(T t: objects){
			r += t + "\n";
		}
		return r;
	}

}

