package contacts;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a list of contacts
 * Contacts are stored in an HashMap for optimization (key is the contact's id)
 * Will be improved according to the real functional needs
 *
 */
public class CContactList {
	
	private Map<String, CContact> map;
	
	
	public CContactList(){
		this.map = new HashMap<>();
	}
	
	public boolean isEmpty(){
		return this.map.isEmpty();
	}
	
	public int size(){
		return this.map.size();
	}

	public CContact find(String id){
		return this.map.get(id);
	}

	public CContact find(CContact contact){
		return contact == null ? null : this.find(contact.getId());
	}
	
	public boolean exists(CContact contact){
		return contact == null ? false : this.map.containsKey(contact.getId());
	}
	
	public boolean add (CContact contact){
		if (contact == null || this.map.containsKey(contact.getId())){
			return false;
		}
		this.map.put(contact.getId(), contact);
		return true;
	}
	
	public String toString(){
		return this.description(null);
	}
	
	public String listIds(){
		StringBuffer sb = new StringBuffer();
		map.keySet().forEach((key) -> {
			if (sb.length() > 0){
				sb.append(", ");
			}
			sb.append(key);
		});
		return String.format("Contact Id's [%s]", sb.toString());
	}
	
	public String description(String indent){
		String indentation = indent != null ? indent : "";
		StringBuffer sb = new StringBuffer();
		sb.append(indentation).append(String.format("Contact list - %d entries", this.map.size())); 
		String contactIdent = indentation == null ? indentation : indentation + "---";
		map.values().forEach((contact) -> {
			sb.append("\n");
			sb.append(contact.description(contactIdent));
		});
		return sb.toString();
	}
		
}
