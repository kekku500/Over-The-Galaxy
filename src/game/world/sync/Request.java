package game.world.sync;

public interface Request {
	
	public enum Action{
		ADD, //Add Entity to the world
		REMOVE, //Remove Entity from the world
		INIT,
		RENEWNEXT, //Update next updating world only (1 to update)
		RENEWALL, //Update all worlds (2 to update)
		SETSTATIC,
		SETDYNAMIC;} //Creates vbo for object
	
	public enum Status{
		FINAL, //One more world left to update
		IDLE, //Current world has done the request
		CONTINUE} //Request not done yet (more worlds to update)
	
	public boolean isDone();
	
	public void waitFor(Request req);

}
