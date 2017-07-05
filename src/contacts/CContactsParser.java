package contacts;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import org.xml.sax.InputSource;
import utils.CParserReport;

import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class CContactsParser {

	private static class CSaxHandler extends DefaultHandler {
		
		private CParserReport report;
		private CContactsParser parser;
		private Queue<CContact> stack;
		
		CSaxHandler(CParserReport report, CContactsParser parser){
			this.report = report;
			this.parser = parser;
			stack = Collections.asLifoQueue(new LinkedList<>());
		}
		
		@Override
		public void startDocument () throws SAXException{
			report.addInfo("startDocument");
	   	}
		
		@Override
	   	public void endDocument () throws SAXException{
			report.addInfo("endDocument");
	    }
		
		@Override
	    public void startElement (String uri, String localName,  String qName, Attributes attributes) throws SAXException{
			report.addInfo("startElement " + qName);
			switch(qName){
				case "contacts":
					break;
				case "contact":
		            String id = attributes.getValue("id");
					break;
				case "name":
					break;
				case "lastName":
					break;
				
			}
		}
		
		@Override
	    public void endElement (String uri, String localName, String qName) throws SAXException {
			report.addInfo("startElement " + qName);
        }
		
		@Override
	    public void characters (char ch[], int start, int length) throws SAXException{
	  
        }
		
		@Override
	    public void warning (SAXParseException e) throws SAXException{
			report.addWarning(e);
	    }
		
		@Override
	    public void error (SAXParseException e) throws SAXException{
			report.addError(e);
	    }
		
		@Override
	    public void fatalError (SAXParseException e) throws SAXException{
	        throw e;
	    }
	}
	 
	private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	public CContactList mainContactList;
	
	private CContactsParser(String xml, CParserReport report) throws Exception{
		SAXParser saxParser = saxParserFactory.newSAXParser();
		DefaultHandler handler = new CSaxHandler(report, this);
		InputSource inputSource = new InputSource( new StringReader(xml));
	    saxParser.parse(inputSource, handler);
	}
	
	public static CContactList parseContactList(String xml, CParserReport report){
		try{
			CContactsParser parser = new CContactsParser(xml, report);
			return parser.mainContactList;
		}catch(Exception e){
			report.addFatalError(e);
		}
		return null;
	}
}
