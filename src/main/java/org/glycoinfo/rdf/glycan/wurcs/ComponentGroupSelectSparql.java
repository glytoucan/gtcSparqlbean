package org.glycoinfo.rdf.glycan.wurcs;

import java.util.HashSet;
import java.util.Set;

import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.Motif;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 

PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>
PREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#> 
PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>

SELECT ?MS count (distinct ?RES) AS ?count
FROM <http://rdf.glytoucan.org/core>
FROM <http://rdf.glytoucan.org/sequence/wurcs>
where {
  #Unique RES 1
  ?gseq wurcs:has_uniqueRES ?uRES .
  ?uRES wurcs:is_monosaccharide ?MS .
  ?RES wurcs:is_uniqueRES ?uRES .
}

 * @author aoki
 *
 */
public class ComponentGroupSelectSparql extends WurcsGlycoSequenceSelectSparql {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(ComponentGroupSelectSparql.class);

	String ident;
	String subNumber;
	String keyword;
	String supIri;
	Set<String> subIriList = new HashSet<String>();
	
	public ComponentGroupSelectSparql() {
		this.prefix = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#>\n"
				+ "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n";
		this.select = "?MS count(distinct ?RES) as ?count \n";;
		this.from = "FROM <http://rdf.glytoucan.org/core>\n"
				+ "FROM <http://rdf.glytoucan.org/sequence/wurcs>";
	}

	@Override
	public String getWhere() throws SparqlException {
		return super.getWhere() + "?" + GlycanSequenceURI + " wurcs:has_uniqueRES ?uRES .\n"
		+ "?uRES wurcs:is_monosaccharide ?MS .\n"
		+ "?RES wurcs:is_uniqueRES ?uRES .\n";
	}
}