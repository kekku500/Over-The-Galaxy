package game.world.sync;

import java.util.LinkedList;

import game.world.entities.Entity;

public class RequestManager{
	
	LinkedList<UpdateRequest> updateRequests = new LinkedList<UpdateRequest>();
	LinkedList<RenderRequest> renderRequests = new LinkedList<RenderRequest>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LinkedList<UpdateRequest> getUpdateRequests(){
		return updateRequests;
	}
	
	public LinkedList<RenderRequest> getRenderRequests(){
		return renderRequests;
	}
	
	public void removeByID(int id){
		for(UpdateRequest w: updateRequests){
			if(w.getID() == id){
				updateRequests.remove(w);
				break;
			}
		}
	}
	
	public boolean add(Request req){
		if(req instanceof UpdateRequest){
			return updateRequests.add((UpdateRequest)req);
		}else if(req instanceof RenderRequest){
			return renderRequests.add((RenderRequest)req);
		}
		return false;
		
	}
	
	public boolean addCheck(UpdateRequest w){
		if(w.getID() != 0){
			removeByID(w.getID());
		}
		return updateRequests.add(w);
	}

	

}
