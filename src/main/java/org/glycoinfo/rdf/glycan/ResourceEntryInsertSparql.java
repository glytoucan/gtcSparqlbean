package org.glycoinfo.rdf.glycan;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.UriProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a ResourceEntry Insert updateSPARQL.
 * Required fields are: PrimaryId, Database, AccessionNumber, ContributorId, and date submitted.  Please refer to ResourceEntry.class.
 * 
 * prefix dc: <http://purl.org/dc/elements/1.1/> 
 * prefix dcterms: <http://purl.org/dc/terms/> 
 * prefix glycan: <http://purl.jp/bio/12/glyco/glycan#> 
 * <http://rdf.glycoinfo.org/resource-entry/331ebfcfc29a997790a7a4f1671a9882>
 *     a    glycan:resource_entry ;
 *     glycan:in_glycan_database glycan:database_glytoucan ;
 *     dcterms:identifier "G00021MO" ;
 *     rdfs:seeAlso <https://glytoucan.org/Structures/Glycans/G00021MO> ;
 *     dc:contributor    <http://rdf.glycoinfo.org/glytoucan/contributor/1> ;
 *     dcterms:dataSubmitted  "2014-10-20 06:47:31.204"^^xsd:dateTimeStamp .
 * 
 * This work is licensed under the Creative Commons Attribution 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.
 * 
 * @author aoki
 *
 */
public class ResourceEntryInsertSparql extends InsertSparqlBean implements ResourceEntry, UriProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceEntryInsertSparql.class);


	/*
	 * @PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> .
@PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#> .
@PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#> .
	 */
	void init() {
		this.prefix="prefix glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "prefix dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "prefix dcterms: <http://purl.org/dc/terms/>\n"
				+ "prefix glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n";
    }

	public ResourceEntryInsertSparql() {
		super();
		init();
	}
	
	public String getUri() {
		if (null != getSparqlEntity().getValue(ResourceEntry.ResourceEntryURI) && null != getSparqlEntity().getValue(ResourceEntry.Identifier)) {
			String url = getSparqlEntity().getValue(ResourceEntry.ResourceEntryURI) + getSparqlEntity().getValue(ResourceEntry.Identifier);
			return url;
		} else {
			String database = null;
			if (StringUtils.isNotBlank(getSparqlEntity().getValue(Database)))
				database = getSparqlEntity().getValue(Database);
			if (StringUtils.isNotBlank(getSparqlEntity().getValue(PartnerId)))
				database = getSparqlEntity().getValue(PartnerId);

			return generateUri(getSparqlEntity().getValue(AccessionNumber), database, getSparqlEntity().getValue(Identifier));
		}
	}

	public String getInsert()  {
//		if (StringUtils.isBlank(getSparqlEntity().getValue(Saccharide.PrimaryId)))
//			throw new SparqlException("requires primary id");
//		else if (StringUtils.isBlank(getSparqlEntity().getValue(Database)))
//			throw new SparqlException("requires database");
//		else if(StringUtils.isBlank(getSparqlEntity().getValue(ContributorId)))
//			throw new SparqlException("requires contributor id");
//		else if (StringUtils.isBlank(getSparqlEntity().getValue(DataSubmittedDate))) {
//			throw new SparqlException("requires date submitted");
//		}
		String saccharideRelation = null;
		if (null != getSparqlEntity().getValue(Saccharide.PrimaryId)) {
			getSparqlEntity().setValue(Saccharide.URI, "http://rdf.glycoinfo.org/glycan/" + getSparqlEntity().getValue(Saccharide.PrimaryId));
		}
		if (null != getSparqlEntity().getValue(Saccharide.URI)) {
			saccharideRelation = "<" + getSparqlEntity().getValue(Saccharide.URI) + "> glycan:has_resource_entry <" + getUri() + "> .\n";
		}
		
		StringBuilder insertBuilder = new StringBuilder((StringUtils.isBlank(saccharideRelation)? "" : saccharideRelation) + "<" + getUri() + ">" + " a " + "glycan:Resource_entry .\n");
		if (StringUtils.isNotBlank(getSparqlEntity().getValue(Database)))
			insertBuilder.append("<" + getUri() + ">" + " glycan:in_glycan_database glytoucan:database_" + getSparqlEntity().getValue(Database) + " .\n");
		if (StringUtils.isNotBlank(getSparqlEntity().getValue(GlycanDatabaseLiteral)))
			insertBuilder.append("<" + getUri() + ">" + " glycan:in_glycan_database <" + getSparqlEntity().getValue(GlycanDatabaseLiteral) + "> .\n");
		if (StringUtils.isNotBlank(getSparqlEntity().getValue(DatabaseName)))
			insertBuilder.append("<" + getUri() + ">" + " rdfs:label \"" + getSparqlEntity().getValue(DatabaseName) + "\" .\n");
		
		insertBuilder.append("<" + getUri() + ">" + " dcterms:identifier \"" + getSparqlEntity().getValue(Identifier) + "\" .\n");
		if (StringUtils.isNotBlank(getSparqlEntity().getValue(DatabaseURL))) {
			String databaseUrl = getSparqlEntity().getValue(DatabaseURL);
			logger.debug("databaseUrl:>" + databaseUrl);
			databaseUrl = databaseUrl.replace("[?id?]", getSparqlEntity().getValue(Identifier));
			logger.debug("databaseUrl:>" + databaseUrl);
			insertBuilder.append("<" + getUri() + ">" + " rdfs:seeAlso <" + databaseUrl + "> .\n");
		} else
			insertBuilder.append("<" + getUri() + ">" + " rdfs:seeAlso <https://glytoucan.org/Structures/Glycans/" + getSparqlEntity().getValue(Identifier) + "> .\n");
		insertBuilder.append("<" + getUri() + ">" + " glytoucan:contributor <http://rdf.glycoinfo.org/glytoucan/contributor/userId/" + getSparqlEntity().getValue(ContributorId) + "> .\n");
		insertBuilder.append("<" + getUri() + ">" + " glytoucan:date_registered \"" + dateTimeStamp((Date) getSparqlEntity().getObjectValue(DataSubmittedDate)) + "\"^^xsd:dateTimeStamp ."); 
		this.insert = insertBuilder.toString();
		return this.insert;
	}

	private String dateTimeStamp(Date value) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(value);
	}

  public static String generateUri(String accessionNumber, String database, String partnerAccessionNumber) {
    return "http://rdf.glycoinfo.org/glycan/resource-entry/" + accessionNumber + "/" + database + "/" + partnerAccessionNumber;
//    return "http://rdf.glycoinfo.org/glycan/resource-entry/" + database + "/" + partnerAccessionNumber;
  }
}