package world.sync;

import java.util.ArrayList;
import java.util.List;

/**
 * A Wrapper for request list (extra methods for convenience).
 * @author Kevin
 */
public class RequestList{
	
	List<Request<?>> updateRequests = new ArrayList<Request<?>>();
	
	public List<Request<?>> getUpdateRequests(){
		return updateRequests;
	}
	
	
	public void removeByID(int id){
		for(Request<?> w: updateRequests){
			if(w.getID() == id){
				updateRequests.remove(w);
				break;
			}
		}
	}
	
	public boolean add(Request<?> req){
		return updateRequests.add(req);
	}
	
	/**
	 * If request by the same id already exists in the list,
	 * remove it and add request in the parameter to the list.
	 * @param w
	 * @return
	 */
	public boolean replace(Request<?> w){
		if(w.getID() != 0){
			removeByID(w.getID());
		}
		return updateRequests.add(w);
	}

}
