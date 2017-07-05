package utils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.xml.sax.SAXParseException;


public class CParserReport {


	private static enum Type {
	  INFO ("info"),
	  ERROR ("Error"),
	  FATAL ("FataError"),
	  WARNING ("WARNING");
	   
	  private String name = "";

	  Type(String name){
	    this.name = name;
	  }
	  
	  public boolean isError(){
		  return this == ERROR || this == FATAL;
	  }
	  
	  public String toString(){
	    return name;
	  }
	}

	private static class CReportItem{
		Type type;
		String message;
		Optional<Integer> lineNumber;
		Optional<Exception> e;
		
		private CReportItem(Type type, String message, Optional<Integer> lineNumber, Optional<Exception> e){
			this.type = type == null ? Type.ERROR : type;
			this.message = message;
			this.lineNumber = lineNumber;
			this.e = e;
		}
		
		public boolean isError(){
			return this.type.isError();
		}
		
		public String toString(){
			StringBuffer sb = new StringBuffer();
			sb.append(type).append(" ");

			if (this.lineNumber.isPresent()){
				sb.append("line[").append(this.lineNumber.get()).append("] ");
			}
			
			sb.append(this.message);
			
			if (this.e.isPresent()){
				sb.append("\n\t*Exception*\n").append(this.e.get().getMessage());
				StringWriter sw = new StringWriter();
				this.e.get().printStackTrace(new PrintWriter(sw));
				sb.append("\n\t*Stack*\n").append(sw.toString());
			}
			return sb.toString();
		}
	}
	
	private List<CReportItem> items;
	private CLogger logger;
	
	public CParserReport(CLogger logger){
		this.items = new ArrayList<>();
		this.logger = logger;
	}
	
	private void addItem(CReportItem itm){
		this.items.add(itm);
		this.logger.log(itm.toString());
	}
	
	public void addError(SAXParseException e){
		this.addItem(new CReportItem(Type.ERROR, e.getMessage(),Optional.of(new Integer(e.getLineNumber())), Optional.of(e)));
	}
	
	public void addFatalError(Exception e){
		this.addItem(new CReportItem(Type.FATAL, e.getMessage(),Optional.ofNullable(null), Optional.of(e)));
	}
	
	public void addWarning(SAXParseException e){
		this.addItem(new CReportItem(Type.WARNING, e.getMessage(),Optional.of(new Integer(e.getLineNumber())), Optional.ofNullable(null)));
	}
	
	public void addInfo(String message){
		this.addItem(new CReportItem(Type.INFO, message, Optional.ofNullable(null), Optional.ofNullable(null)));
	}
	
	public boolean hasErrors(){
		for (CReportItem item : this.items){
			if (item.isError()){
				return true;
			}
		}
		return false;
	}
	
	public String toString (){
		StringBuffer sb = new StringBuffer();
		for (CReportItem item : this.items){
			sb.append("-------------------\n").append(item.toString());
		}
		return sb.toString();
	}
}
