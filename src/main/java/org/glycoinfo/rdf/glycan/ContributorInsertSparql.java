package org.glycoinfo.rdf.glycan;

import org.glycoinfo.rdf.InsertSparqlBean;

/**
 * 
 * @prefix dcterms: <http://purl.org/dc/terms/> .
 * @prefix foaf: <http://xmlns.com/foaf/0.1/#>
 * 
 * <http://rdf.glycoinfo.org/glytoucan/contributor/1>
 *     a    foaf:Person ;
 *     dcterms:identifier    "1";
 *     foaf:name "Administrator" .
 *     
 * @author aoki
 *
 */
public class ContributorInsertSparql extends InsertSparqlBean implements Contributor {
	
	public static final String UserId = "UserId";
	public static final String ContributorName = "ContributorName";
	
	public ContributorInsertSparql() {
		this.prefix="PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX dcterms: <http://purl.org/dc/terms/>\n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	}

	@Override
	public String getInsert() {
		String insert = getUri() + " a foaf:Person .\n";
	insert += getUri() + " dcterms:identifier \"" + getSparqlEntity().getValue(ID) + "\"^^xsd:string .\n";
	insert += getUri() + " foaf:name \"" + getSparqlEntity().getValue(ContributorName) + "\"^^xsd:string .\n"; 
//  insert += getUri() + " glytoucan:system_id \"" + getSparqlEntity().getValue(HASH) + "\"^^xsd:string .\n"; 

		return insert;
	}

	private String getUri() {
		return "<http://rdf.glycoinfo.org/glytoucan/contributor/userId/" + getSparqlEntity().getValue(ID) + ">";
	}
}