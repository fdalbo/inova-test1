package utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.xml.sax.SAXParseException;

/**
 * Process report
 *
 */
public class CParserReport {


	public static enum EnumReportType {
	  INFO ("info"),
	  WARNING ("WARNING"),
	  ERROR ("ERROR"),
	  FATAL ("INFO");
	   
	  private String name = "";

	  EnumReportType(String name){
	    this.name = name;
	  }
	  
	  public boolean isError(){
		  return this == ERROR || this == FATAL;
	  }
	  
	  public boolean greaterOrEqual(EnumReportType type){
		  return type == null ? false : this.ordinal() >= type.ordinal();
	  }
	  
	  public String toString(){
	    return name;
	  }
	}

	private static class CReportItem{
		EnumReportType type;
		String message;
		Optional<Integer> lineNumber;
		Optional<Exception> e;
		
		private CReportItem(EnumReportType type, String message, Optional<Integer> lineNumber, Optional<Exception> e){
			this.type = type == null ? EnumReportType.ERROR : type;
			this.message = message;
			this.lineNumber = lineNumber;
			this.e = e;
		}
		
		@SuppressWarnings("unused")
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
				sb.append("\n\t*Exception*\n");
				StringWriter sw = new StringWriter();
				this.e.get().printStackTrace(new PrintWriter(sw));
				sb.append("\t*Stack*\n\t").append(sw.toString());
			}
			return sb.toString();
		}
	}
	
	private List<CReportItem> items;
	private CLogger logger;
	
	public CParserReport(){
		this(null);
	}
	
	public CParserReport(CLogger logger){
		this.items = new ArrayList<>();
		this.logger = logger;
	}
	
	private void add(CReportItem itm){
		this.items.add(itm);
		if (this.logger != null){
			this.logger.log(itm.toString());
		}
	}
	
	public void error(String message){
		this.add(new CReportItem(EnumReportType.ERROR, message, Optional.ofNullable(null),  Optional.ofNullable(null)));
	}
	
	
	public void error(SAXParseException e){
		this.add(new CReportItem(EnumReportType.ERROR, e.getMessage(),Optional.of(new Integer(e.getLineNumber())), Optional.of(e)));
	}
	
	public void fatalError(Exception e){
		this.add(new CReportItem(EnumReportType.FATAL, e.getMessage(), Optional.ofNullable(null), Optional.of(e)));
	}
	
	public void warning(String message){
		this.add(new CReportItem(EnumReportType.WARNING, message, Optional.ofNullable(null),  Optional.ofNullable(null)));
	}
	
	public void warning(SAXParseException e){
		this.add(new CReportItem(EnumReportType.WARNING, e.getMessage(),Optional.of(new Integer(e.getLineNumber())), Optional.ofNullable(null)));
	}
	
	public void info(String message){
		this.add(new CReportItem(EnumReportType.INFO, message, Optional.ofNullable(null), Optional.ofNullable(null)));
	}

	/**
	 * True if there are at least one items with a type >= given type
	 */
	public boolean checkType(EnumReportType type){
		type = type == null ? EnumReportType.INFO : type;
		for (CReportItem item : this.items){
			if (item.type.greaterOrEqual(type)){
				return true;
			}
		}
		return false;
	
	}
	public boolean hasErrors(){
		return this.checkType(EnumReportType.ERROR);
	}

	public String toString(){
		return toString(null);
	}
	
	/**
	 * Filters items according to the given type
	 */
	public String toString(EnumReportType type){
		type = type == null ? EnumReportType.INFO : type;
		StringBuffer sb = new StringBuffer();
		for (CReportItem item : this.items){
			if (item.type.greaterOrEqual(type)){
				if (sb.length() != 0){
					sb.append("\n");
				}
				sb.append(item.toString());
			}
		}
		return sb.toString();
	}
}
