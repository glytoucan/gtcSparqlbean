package org.glycoinfo.rdf.glycan.wurcs;

import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.Monosaccharide;
import org.glycoinfo.rdf.glycan.Motif;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MonosaccharideSelectSparql extends WurcsGlycoSequenceSelectSparql implements WurcsMonosaccharide {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(MonosaccharideSelectSparql.class);

	/**
	 * 
PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>
PREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#> 
PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>
select distinct ?ms
FROM <http://rdf.glytoucan.org/core>
FROM <http://rdf.glytoucan.org/sequence/wurcs>
FROM <http://rdf.glytoucan.org/wurcs/ms>
where {
?glycan a glycan:saccharide.
?glycan glytoucan:has_primary_id "G07716WO" .
?glycan glycan:has_glycosequence ?gseq .
?gseq wurcs:has_monosaccharide	?ms .
}

	 *
PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>
PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>
PREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#>
 SELECT DISTINCT ?Sequence
?PrimaryId
 ?WurcsMonosaccharideURI FROM <http://rdf.glytoucan.org/core>
FROM <http://rdf.glytoucan.org/sequence/wurcs>
FROM <http://rdf.glytoucan.org/wurcs/ms>
 WHERE {
?SaccharideURI a glycan:saccharide .
?SaccharideURI glytoucan:has_primary_id ?PrimaryId .
?SaccharideURI glycan:has_glycosequence ?GlycanSequenceURI .
?GlycanSequenceURI glycan:has_sequence ?Sequence .
?GlycanSequenceURI glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .
?SaccharideURI glytoucan:has_primary_id "G11301TD" .
?GlycanSequenceURI  wurcs:has_monosaccharide ?WurcsMonosaccharideURI .
}    

	 */
	public MonosaccharideSelectSparql() {
		this.prefix = super.getPrefix() + "PREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#>\n";
		this.select = super.getSelect() + " ?" + WurcsMonosaccharideURI;
		this.from = super.getFrom() + "FROM <http://rdf.glytoucan.org/wurcs/ms>\n";
	}

	@Override
	public String getWhere() throws SparqlException {
		return super.getWhere() + "?" + GlycanSequenceURI + "  wurcs:has_monosaccharide ?" + WurcsMonosaccharideURI + " .\n";
	}
}