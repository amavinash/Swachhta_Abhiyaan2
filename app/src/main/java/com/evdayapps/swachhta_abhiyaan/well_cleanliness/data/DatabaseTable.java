package com.evdayapps.swachhta_abhiyaan.well_cleanliness.data;

public class DatabaseTable {
	StringBuilder sbForRegistration;
	StringBuilder sbForTimeTable;
	StringBuilder sbForSOURCE;
	public DatabaseTable() {
		 sbForRegistration=new StringBuilder();
		 sbForTimeTable=new StringBuilder();
		 sbForSOURCE=new StringBuilder();
		 appendtable();
	}

	private void appendtable() {
		sbForRegistration.append("CREATE TABLE user (");
		sbForRegistration.append("username TEXT,");
		sbForRegistration.append("password TEXT)");
			
		
		sbForTimeTable.append("CREATE TABLE pratishtaninfo (");
		sbForTimeTable.append("blooddonation TEXT,");
		sbForTimeTable.append("plantation TEXT,");
		sbForTimeTable.append("swachata TEXT,");
		sbForTimeTable.append("bloodmanpower TEXT,");
		sbForTimeTable.append("plantationmanpower TEXT,");
		sbForTimeTable.append("swachatamanpower TEXT)");
		
		
		sbForSOURCE.append("CREATE TABLE villageinfo (");
		sbForSOURCE.append("id TEXT,");
		sbForSOURCE.append("village TEXT,");
		sbForSOURCE.append("taluka TEXT,");
		sbForSOURCE.append("district TEXT,");
		sbForSOURCE.append("state TEXT)");
	
		
	}


	
}
