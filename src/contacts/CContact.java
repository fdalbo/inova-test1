package contacts;

import org.xml.sax.Attributes;

/**
 * Represents a contact
 * - id is the UUID (to clarify)
 * - myContacts gives the linked contacts  (meaning to clarify)
 *
 */
public class CContact {
	
	private String id;
	private String firstName;
	private String lastName;
	private CContactList myContacts;
	
	public CContact(String id){
		this.id = id;
		this.myContacts = new CContactList();
	}
	
	/**
	 * True if contact is valid (to improve)
	 * @return
	 */
	public boolean isValid(){
		return this.id != null && !this.id.isEmpty() && this.lastName != null && !this.lastName.isEmpty();
	}
	
	public boolean addContact(CContact contact){
		return this.myContacts.add(contact);
	}
	
	public String getId(){
		return this.id;
	}
	
	public String getName(){
		return this.firstName;
	}
	
	public String getLastName(){
		return this.lastName;
	}
	
	public CContactList getMyContacts(){
		return this.myContacts;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
	
	public String toString(){
		return String.format("Id=%s FirstName=%s LastName=%s NbContacts=%d", this.id, this.firstName, this.lastName, myContacts.size());
	}
	
	public String description(String indent){
		indent = indent != null ? indent : "";
		StringBuffer sb = new StringBuffer();
		sb.append(indent).append(this.toString());
		if (this.myContacts.size() > 0){
			sb.append("\n");
			sb.append(indent).append(this.myContacts.listIds());
		}
		return sb.toString();
	}
	
	public static CContact newFromXml (Attributes attributes){
		return new CContact(attributes.getValue("id"));
	}
}
