package contacts;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CContactList {
	
	private CContactList parent;
	private Map<String, CContact> map;
	
	
	public CContactList(Optional<CContactList> parent){
		this.parent = parent.isPresent() ? parent.get() : null;
		this.map = new HashMap<>();
	}
	
	public CContactList getRoot(){
		return this.parent == null ? this : this.parent.getRoot();
	}
	public boolean add (CContact contact){
		if (contact == null || this.map.containsKey(contact.getId())){
			return false;
		}
		return true;
	}
	
		
}
