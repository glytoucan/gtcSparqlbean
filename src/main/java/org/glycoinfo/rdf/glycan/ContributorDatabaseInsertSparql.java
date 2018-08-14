package org.glycoinfo.rdf.glycan;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.UriProvider;

/**
 * insert contributor to database
 *  
 * This work is licensed under the Creative Commons Attribution 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.
 * 
 * @author aoki
 *
 */
public class ContributorDatabaseInsertSparql extends InsertSparqlBean  {

	public ContributorDatabaseInsertSparql() {
		super();
		this.prefix="PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n" 
				+ "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
				+ "PREFIX dcterms: <http://purl.org/dc/terms/>\n";
		this.graph="http://rdf.glytoucan.org/partner/member";
		this.using="USING <http://rdf.glytoucan.org/partner>";
		this.setConstant(false);
	}
	
	@Override
	public String getInsert()  {
		return "<http://rdf.glycoinfo.org/glytoucan/contributor/userId/" + getSparqlEntity().getValue(Contributor.ID) + "> foaf:member ?db .\n";
	}


	@Override
	public String getWhere() {
		return "?db a glytoucan:Partner .\n?db dcterms:identifier \"" + getSparqlEntity().getValue(ResourceEntry.Database_Abbreviation) + "\"^^<http://www.w3.org/2001/XMLSchema#string> . \n";
	}
}
