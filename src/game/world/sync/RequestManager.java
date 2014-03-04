package game.world.sync;

import java.util.LinkedList;

public class RequestManager{
	
	LinkedList<UpdateRequest<?>> updateRequests = new LinkedList<UpdateRequest<?>>();
	LinkedList<RenderRequest<?>> renderRequests = new LinkedList<RenderRequest<?>>();
	
	public LinkedList<UpdateRequest<?>> getUpdateRequests(){
		return updateRequests;
	}
	
	public LinkedList<RenderRequest<?>> getRenderRequests(){
		return renderRequests;
	}
	
	public void removeByID(int id){
		for(Request w: updateRequests){
			if(((UpdateRequest<?>)w).getID() == id){
				updateRequests.remove(w);
				break;
			}
		}
	}
	
	public boolean add(Request req){
		if(req instanceof UpdateRequest){
			return updateRequests.add((UpdateRequest<?>)req);
		}else if(req instanceof RenderRequest){
			return renderRequests.add((RenderRequest<?>)req);
		}
		return false;
		
	}
	
	public boolean addCheck(UpdateRequest<?> w){
		if(w.getID() != 0){
			removeByID(w.getID());
		}
		return updateRequests.add(w);
	}

	

}
