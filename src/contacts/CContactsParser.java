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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * XML contacts file parser
 *
 */
public class CContactsParser {

	private final static HashSet<String> CONTACT_PROPS;
	static{
		String elements[] = {"name","lastName"};
		CONTACT_PROPS = new HashSet<String>(Arrays.asList(elements));
	}
	
	/**
	 * Element handler
	 */
	private static class CSaxHandler extends DefaultHandler {
		
		private CParserReport report;
		private CContactList mainContactList;
		private Queue<CContact> stackContact;
		private Queue<String> stackTag;
		private int indent = 0;
		
		CSaxHandler(CParserReport report, CContactList mainContactList){
			this.report = report;
			this.mainContactList = mainContactList;
			this.stackContact = Collections.asLifoQueue(new LinkedList<>());
			this.stackTag  = Collections.asLifoQueue(new LinkedList<>());
		}
		
		private void logTag(String str, boolean start){
			if (!start) {
				this.indent--;
			}
			String indent = new String(new char[this.indent]).replace((char) 0, '-');
			report.info(indent + str);
			if (start){
				this.indent++;
			}
		}
		
		@Override
		public void startDocument () throws SAXException{
			report.info("startDocument");
	   	}
		
		@Override
	   	public void endDocument () throws SAXException{
			report.info("endDocument");
	    }
		
		@Override
	    public void startElement (String uri, String localName,  String qName, Attributes attributes) throws SAXException{
			logTag("startElement " + qName, true);
			stackTag.add(qName);
			if (CONTACT_PROPS.contains(qName)){
				return;
			}
			CContact contact;
			switch(qName){
				case "contacts":
					break;
				case "contact":
					contact = CContact.newFromXml(attributes);
		            this.report.info("New contact id[" + contact.getId() + "]");
					CContact existingContact = this.mainContactList.find(contact);
					if (existingContact == null){
			            this.report.info("Create contact id[" + contact.getId() + "]");
					} else {
						/**
						 * In case there's multiple contacts with the same ID we assume that the first contact created is the good one
						 * We could check if contact info are consistent with the exiting one (To clarify 
						 */
			            this.report.info("Contact id[" + contact.getId() + "] already exists");
						contact = existingContact;
					}
					/**
					 * Temporary stored in a stack
					 * Will be added to the list in endElement
					 */
		            this.stackContact.add(contact);
					break;
				default:
					this.report.warning("Unknown tag [" + qName + "]");
					break;
				
			}
		}
		
	    @Override
	    public void characters(char ch[], int start, int length) throws SAXException {
	    	String tag = stackTag.peek();
	    	if (CONTACT_PROPS.contains(tag)){
	    		/**
	    		 * FullFill contact object twith the given properties
	    		 */
	    		this.report.info("characters tag[" + tag + "]");
				String value = new String(ch, start, length);
				CContact contact = this.stackContact.peek();
				if (contact == null){
					this.report.error("Unexpected tag " + tag + " - Current contact expected");
					return;
				} 
	    		if (this.mainContactList.exists(contact)){
	    			/**
	    			 * Exiting contact we skip this stage (to clarify)
	    			 */
		    		this.report.info("Skip tag[" + tag + "] - Contact already exixts");
					return;
	    		}
			    switch(tag){
					case "name":
						contact.setFirstName(value);
					break;
					case "lastName":
						contact.setLastName(value);
					break;
				}
	    	}
		}
	    
		@Override
	    public void endElement (String uri, String localName, String qName) throws SAXException {
			logTag("endElement " + qName, false);
			String openedTag = stackTag.remove();
			if (openedTag != qName){
				this.report.error("Unexpected endElement " + qName + " - Expected " + openedTag);
				return;
			}
			if (CONTACT_PROPS.contains(qName)){
				return;
			}
			switch(qName){
				case "contacts":
					// Nothing
					break;
				case "contact":
					CContact contact = this.stackContact.remove();
					if (contact == null){
						this.report.error("bad endElement " + qName + " context - Current contact expected");
					} else if (!contact.isValid()){
						/**
						 * Invalid data provided
						 */
						this.report.error("Invalid contact data " + contact.toString());
					} else {
						/**
						 * We always ad a valid contact in the main list (to  clarify)
						 * If there's a parent contact (contact lst inside a contact) we add the contact to the parent's list
						 */
						boolean added = this.mainContactList.add(contact);
						report.info(String.format("Contact [%s] added to main list -> %b", contact.toString(), added));
						CContact parentContact =  stackContact.peek();
						report.info(String.format("Contact [%s] parentId [%s]", contact.getId(), parentContact == null ? "none": parentContact.getId()));
						if (parentContact != null){
							/**
							 * We'have to check again if the contact has been created for the following use case (to clarify if it's relevant)
							 * <contact id="2"><contacts><contact id="7"><contacts><contact id="2"></contact></contacts></contact></contacts></contact>
							 * Contact id="2" is provided during the initialization of contact id="2" (main one)
							 * When we close the main contact we add the children to the existing contact not to the current one
							 */
							CContact existingParentContact = this.mainContactList.find(parentContact);
							if (existingParentContact != null){
								report.info(String.format("Contact [%s] parentContact[%s] exists", contact.getId(), parentContact.getId()));
								parentContact = existingParentContact;
							}
							added = parentContact.addContact(contact);
							report.info(String.format("Contact [%s] added to parentContact[%s] -> %b", contact.getId(), parentContact.getId(), added));
						}
					}
					break;
			}
        }
		
		@Override
	    public void warning (SAXParseException e) throws SAXException{
			report.warning(e);
	    }
		
		@Override
	    public void error (SAXParseException e) throws SAXException{
			report.error(e);
	    }
		
		@Override
	    public void fatalError (SAXParseException e) throws SAXException{
			/**
			 * Normally a fatal error stops the process
			 */
	        throw e;
	    }
	}
	 
	private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	public CContactList mainContactList = new CContactList();
	
	private CContactsParser(String xml, CParserReport report) throws Exception{
		SAXParser saxParser = saxParserFactory.newSAXParser();
		DefaultHandler handler = new CSaxHandler(report, this.mainContactList);
		InputSource inputSource = new InputSource( new StringReader(xml));
	    saxParser.parse(inputSource, handler);
	}
	
	/**
	 * Parses an XML string and returns a list of contacts
	 * Process report is available in report with errors and warnings
	 */
	public static CContactList parseContactList(String xml, CParserReport report)  throws Exception{
		CContactsParser parser = new CContactsParser(xml, report);
		return parser.mainContactList;
	}
}
