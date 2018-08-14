package org.glycoinfo.rdf.glycan;

import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.springframework.stereotype.Component;

/**
 * 
 * SelectSparql for retrieving glycan_database NamedIndividual.
 * 
 * @author aoki
 *
 */
@Component
public class DatabaseSelectSparql extends SelectSparqlBean implements ResourceEntry  {

	public DatabaseSelectSparql(String sparql) {
		super(sparql);
	}

//	SELECT DISTINCT ?userID ?user ?db ?uriTmp ?urlTmp
//			FROM <http://purl.jp/bio/12/glyco/glycan#>
//			FROM <http://rdf.glytoucan.org/users>
//			WHERE{
//				?user a foaf:Person .
//				VALUES ?userID { 5861 }
//				?user dcterms:identifier ?userID .
//				?user foaf:member ?db .
//				?db  a glycan:glycan_database . 
//	OPTIONAL{
//		?db glycan:uriTemplate ?uri .
//		BIND(str(?uri) AS ?uriTmp)
//	}
//	OPTIONAL{
//		?db glycan:has_url_template ?url .
//		BIND(str(?url) AS ?urlTmp)
//	}
//}
	
	public DatabaseSelectSparql() {
		super();

		this.select = "DISTINCT ?" + ContributorId + " ?" + ResourceEntry.UserURI + " ?" + ResourceEntry.GlycanDatabaseLiteral + " ?" + ResourceEntry.ResourceEntryURI + " ?" + ResourceEntry.DatabaseURL + " ?" + ResourceEntry.PartnerId + " ?" + ResourceEntry.Label + " ?" + ResourceEntry.DatabaseName;
		this.from = "FROM <http://purl.jp/bio/12/glyco/glycan#>\nFROM <http://rdf.glytoucan.org/users>\nFROM <http://rdf.glytoucan.org/partner>\nFROM <http://rdf.glytoucan.org/partner/member>\n";
	}

	@Override
	public String getWhere() throws SparqlException {
		String lWhere = "?" + ResourceEntry.UserURI + " a foaf:Person .\n"
				+ "VALUES ?" + ResourceEntry.ContributorId + " { \"" + getSparqlEntity().getValue(ResourceEntry.ContributorId) + "\"^^xsd:string }\n"
				+ "?" + ResourceEntry.UserURI + " dcterms:identifier ?" + ResourceEntry.ContributorId + " .\n"
				+ "?" + ResourceEntry.UserURI + " foaf:member ?" + ResourceEntry.GlycanDatabaseLiteral + " .\n"
				+ "?" + ResourceEntry.GlycanDatabaseLiteral + " a glytoucan:Partner . \n"
				+ "?" + ResourceEntry.GlycanDatabaseLiteral + " dcterms:identifier ?" + ResourceEntry.PartnerId + " . \n"
				+ "?" + ResourceEntry.GlycanDatabaseLiteral + " rdfs:label ?" + ResourceEntry.Label + " . \n"
        + "?" + ResourceEntry.GlycanDatabaseLiteral + " foaf:name ?" + ResourceEntry.DatabaseName + " . \n"
				+ "OPTIONAL{\n"
				+ "?" + ResourceEntry.GlycanDatabaseLiteral + " glycan:uriTemplate ?uri .\n"
				+ "BIND(str(?uri) AS ?" + ResourceEntry.ResourceEntryURI + ")\n"
				+ "}\n"
				+ "OPTIONAL{\n"
				+ "?" + ResourceEntry.GlycanDatabaseLiteral + " glycan:has_url_template ?url .\n"
				+ "BIND(str(?url) AS ?" + ResourceEntry.DatabaseURL + ")\n"
				+ "}";
		return lWhere;
	}
}