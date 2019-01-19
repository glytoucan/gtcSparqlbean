package org.glycoinfo.rdf.glycan;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.glycoinfo.rdf.InsertSparql;
import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.UriProvider;

/**
 * 
 * Insert core data for a Saccharide.  If Saccharide.PrimaryId exists, it will be added with glytoucan:has_primary_id predicate.
 * 
 * @prefix dc: <http://purl.org/dc/elements/1.1/> .
 * @prefix dcterms: <http://purl.org/dc/terms/> .
 * @prefix glycan: <http://purl.jp/bio/12/glyco/glycan#> .
 * @prefix foaf: <http://xmlns.com/foaf/0.1/#>
 * 
 * <http://rdf.glycoinfo.org/glycan/331ebfcfc29a997790a7a4f1671a9882>
 *     a    glycan:saccharide ;
 *
 * @author aoki
 *
 */
public class SaccharideInsertSparql extends InsertSparqlBean implements Saccharide, UriProvider {

	String type = "a glycan:saccharide";
	String hasPrimaryId = "glytoucan:has_primary_id";

	void init() {
		this.prefix="prefix glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n";
	}
	public SaccharideInsertSparql() {
		init();
	}
	
	public SaccharideInsertSparql(GlycoSequenceInsertSparql sequence) {
		init();
		ArrayList<InsertSparql> list = new ArrayList<InsertSparql>();
		list.add(sequence);
		addRelated(list);
	}
	
	public String getInsert() {
		if (StringUtils.isNotBlank(getSparqlEntity().getValue(PrimaryId))) {
			this.insert = getSaccharideURI() + " a glycan:saccharide .\n"
					+ getSaccharideURI() + " glytoucan:has_primary_id \"" + getSparqlEntity().getValue(PrimaryId) + "\" .\n";
		}
		return this.insert;
	}

	public String getSaccharideURI() {
		return "<" + getUri() + ">";
	}

	public String getUri() {
		return SaccharideUtil.getURI(getSparqlEntity().getValue(PrimaryId)); 
	}
	
}