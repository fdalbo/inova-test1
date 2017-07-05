package contacts;

public class CContact {
	
	private String id;
	private String name;
	private String firstName;
	
	public CContact(String id){
		this.id = id;
	}
	
	public boolean isValid(){
		return this.id != null && !this.id.isEmpty() && this.name != null && !this.name.isEmpty();
	}
	
	public String getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getFirstName(){
		return this.firstName;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public String toString(){
		return String.format("%s %s %s",  this.id, this.name, this.firstName);
	}
}
