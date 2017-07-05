package tests;

/**
 * L’énoncé est volontairement imprécis. Le résultat doit être un programme fonctionnel et exécutable lors de la « code review » que nous devrons planifier à partir du 18 juillet. Nous travaillons habituellement avec Eclipse ou NetBeans, mais si vous avez besoin d’autres éléments lors de la session, dites-le nous dès que possible.
 * La clarté du code, sa qualité et votre description seront prises en considération.
 * --------------------------------------------------------------------------------
 * You should create a SAX XML parser that can handle xml fragments like this one
 * <contacts>
 * 	<contact id="1">
 * 		<name>David</name>
 * 		<lastName>FRALEY</lastName>
 * 		<contacts>
 * 		<contact id="2">
 * 		<name>Mary</name>
 * 		<lastName>JANE</lastName>
 * 		<contacts />
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

public class CTest1 extends CTest {

	public CTest1(){
		super("CTest1");
		
	}
	
	public void run(String xmlPath){
		try{
			String strPath = "inputFiles/" + (xmlPath != null ? xmlPath : "test1.xml");
			this.log("Parse file: ", strPath);
			Path path = Paths.get(strPath);
			this.log("path: ", path.toFile().getAbsolutePath());
			String xml = new String (Files.readAllBytes(path),Charset.forName("UTF-8"));
			CContactList contactList = CContactsParser.parseContactList(xml, this.report);
			if (contactList != null){
				this.log("\n***RESULT***\n", contactList.toString());
			} else {
				this.log("\n***RESULT***\n", "NO CONTACTS");
			}
		}catch(Exception e){
			
		}
		this.log("\n***REPORT***\n");
		this.log(this.report.toString());
	}
	public static void main(String[] args) {
		new CTest1().run(args.length > 0 ? args[0] : null);

	}

}
