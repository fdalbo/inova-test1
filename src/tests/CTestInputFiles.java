package tests;

/**
 * L’énoncé est volontairement imprécis.
 * Le résultat doit être un programme fonctionnel et exécutable lors de la « code review » que nous devrons planifier à partir du 18 juillet.
 * Nous travaillons habituellement avec Eclipse ou NetBeans, mais si vous avez besoin d’autres éléments lors de la session, dites-le nous dès que possible.
 * La clarté du code, sa qualité et votre description seront prises en considération.
 * --------------------------------------------------------------------------------
 * You should create a SAX XML parser that can handle xml fragments like this one
 * <contacts>
 * 	<contact id="1">
 * 		<name>David</name>
 * 		<lastName>FRALEY</lastName>
 * 		<contacts>
 * 			<contact id="2">
 * 				<name>Mary</name>
 * 				<lastName>JANE</lastName>
 * 			<contacts />
 * 		</contact>
 * 		</contacts>
 * 		</contact>
 * 		<contact id="3">
 * 		<name>John</name>
 * 		<lastName>DOE</lastName>
 * 		</contact>
 * </contacts>
 * Details:
 *	The input should be a path to a file in the file system.
 *	The output of the parser should be a list of Contact objects (This list should be printed to the console).
 *	The parser should be able to handle not well formed xmls.
 */
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;


import contacts.CContactList;
import contacts.CContactsParser;
import utils.CLogger;
import utils.CParserReport;
import utils.CParserReport.EnumReportType; 

public class CTestInputFiles extends CLogger {

	protected CParserReport report;
	
	public CTestInputFiles(){
		super();
		/**
		 *  Enable/Disable console.log
		 */
		boolean trace = false;
		this.report = new CParserReport(trace ? this : null);
	}
	
	public void run(String xmlPath, String expected){
		String strPath = "inputFiles/" + xmlPath;
		this.log(String.format("\n***RUN %s***\nExpected: %s", strPath, expected));
		CContactList contactList = null;
		try{
			if (xmlPath == null){
				throw new Exception("a FiIle path is required");
			}
			//this.log("Parse file: ", strPath);
			Path path = Paths.get(strPath);
			//this.log("path: ", path.toFile().getAbsolutePath());
			String xml = new String (Files.readAllBytes(path),Charset.forName("UTF-8"));
			contactList = CContactsParser.parseContactList(xml, this.report);
		}catch(Exception e){
			this.report.fatalError(e);
		}
		if (this.report.checkType(EnumReportType.WARNING)){
			this.log("-->RESULT KO\n", contactList == null || contactList.isEmpty() ? "NO CONTACTS": contactList.toString());
			this.log(String.format("-->ERRORS/WARNINGS %s***\n", strPath), this.report.toString(EnumReportType.WARNING));
		} else {
			this.log("-->RESULT OK\n", contactList == null || contactList.isEmpty() ? "NO CONTACTS": contactList.toString());
		}
	}
	public static void main(String[] args) {
		new CTestInputFiles().run("test1.xml", "OK - 3 Contacts created");
		new CTestInputFiles().run("test2.xml", "KO - 2 Contacts created - 1 Warning - 2 Errors");
		new CTestInputFiles().run("test3.xml", "OK - 8 Contacts created");
	}

}
