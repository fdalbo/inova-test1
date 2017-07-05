package tests;

import utils.CLogger;
import utils.CParserReport;

class CTest extends CLogger{
	
	protected CParserReport report;
	
	public CTest(String name) {
		super(name);
		this.report = new CParserReport(this);
	}

}
