package math;

public class Ray {
	
	public Vector3f origin, direction;
	
	public Ray(){
		origin = new Vector3f();
		direction = new Vector3f();
		
	}

	public Ray(Vector3f origin, Vector3f direction) {
		super();
		this.origin = origin;
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "Ray [origin=" + origin + ", direction=" + direction + "]";
	}

}
