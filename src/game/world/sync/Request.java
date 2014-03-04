package game.world.sync;

public interface Request {
	
	public enum Action{
		ADD, //Add Entity to the world (for static objects)
		REMOVE, //Remove Entity from the world
		MOVE, //Change motion static/dynamic/physics
		UPDATE, //Updates only next world
		UPDATEALL, //Updates all worlds
		CAMERAFOCUS,
		INIT;} //Creates vbo for object
	
	public enum Status{
		FINAL, //One more world left to update
		IDLE, //Current world has done the request
		CONTINUE} //Request not done yet (more worlds to update)
	
	public boolean isDone();
	
	public void waitFor(Request req);

}
