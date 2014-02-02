package math;

public class Point {
	
	public float x, y, z;
	
	public Point(){}
	
	public Point(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point copy(){
		return new Point(x, y, z);
	}
	
	public void add(Vector3fc v){
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public Vector3fc distanceVector(Point p2){
		return new Vector3fc(p2.x-x,p2.y-y,p2.z-z);
	}
	
	public Vector3fc getVector(){
		return new Vector3fc(x, y, z);
	}

}
