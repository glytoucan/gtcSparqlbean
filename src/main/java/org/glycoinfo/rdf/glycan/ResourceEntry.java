package org.glycoinfo.rdf.glycan;

public interface ResourceEntry {
	public static final String Database = "Database";
	public static final String DataSubmittedDate = "DataSubmittedDate";
	public static final String ContributorId = "ContributorId";
	public static final String Identifier = "Identifier";
	public static final String ResourceEntryURI = "ResourceEntryURI"; // "http://rdf.glycoinfo.org/unicarb-db/"^^<http://www.w3.org/2001/XMLSchema#string>
	public static final String UserURI = "UserURI"; //http://rdf.glycoinfo.org/glytoucan/contributor/userId/254
	public static final String GlycanDatabaseLiteral = "GlycanDatabaseLiteral"; //http://purl.jp/bio/12/glyco/glycan#database_unicarb_db
	public static final String DatabaseURL = "DatabaseURL"; // http://unicarb-db.biomedicine.gu.se:9000/msData/"^^<http://www.w3.org/2001/XMLSchema#string>
	public static final String Database_Glytoucan = "glytoucan"; // database_glytoucan
	public static final String Database_Abbreviation = "database_abbreviation"; // database_abbreviation
	public static final String AccessionNumber = "AccessionNumber";
	public static final String PartnerId = "PartnerId";
	public static final String Label = "Label";
	public static final String DatabaseName = "DatabaseName";
}