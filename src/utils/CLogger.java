package utils;

public class CLogger {
	public String name;
	
	public CLogger(){
		this(null);
	}
	public CLogger(String name){
		this.name = name == null ? "": name;
	}
	
	public void log(String...args){
		StringBuffer sb = new StringBuffer();
		if (!this.name.isEmpty()){
			sb.append(this.name).append("\t");
		}
		for (String st : args) {
			sb.append(st);
		}
		System.out.println(sb);
	}
}
