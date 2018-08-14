package org.glycoinfo.rdf.glycan.wurcs;

import java.util.HashSet;
import java.util.Set;

import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.Motif;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotifSelectSparql extends SelectSparqlBean {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(MotifSelectSparql.class);

	String ident;
	String subNumber;
	String keyword;
	String supIri;
	Set<String> subIriList = new HashSet<String>();
	
	public MotifSelectSparql() {
		this.select = "?" + Motif.URI + " ?" + Saccharide.PrimaryId + " ?" + GlycoSequence.URI + " ?" + GlycoSequence.Sequence;
		this.prefix = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX toucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>";
		this.from = "FROM <http://rdf.glytoucan.org/core>\n"
				+ "FROM <http://glytoucan.org/rdf/demo/0.3/wurcs>";
		this.where 
		= "?" + Motif.URI + " a glycan:glycan_motif .\n"
		+ "?" + Motif.URI + " toucan:has_primary_id ?" + Saccharide.PrimaryId + " .\n"
		+ "?" + Motif.URI + " glycan:has_glycosequence ?" + GlycoSequence.URI + " .\n"
		+ "?" + GlycoSequence.URI + " glycan:has_sequence ?" + GlycoSequence.Sequence + " .\n"
		+ "?" + GlycoSequence.URI + " glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs\n";
	}
//	String graph = "http://glytoucan.org/rdf/demo/0.2/motif";


//	@Override
//	public String getInsert() {
//		// insert the substructure rdf
//		// if it's a motif, add the has_motif rdf
//		StringBuffer insert = new StringBuffer();
//		for (String string : subIriList) {
//			insert.append("<" + getSupIri() + "> glycan:has_motif <" + string + "> .\n");
//			insert.append("<" + getSupIri() + "> glycan:has_substructure <" + string + "> .\n");
//			insert.append("<" + string + "> glycan:has_superstructure <" + getSupIri() + "> .\n");
//		}
//		return insert.toString();
//	}

//	@Override
//	public void setInsert(String insert) {
//		return;
//	}

//	public String getSubNumber() {
//		return subNumber;
//	}
//
//	public void setSubNumber(String subNumber) {
//		this.subNumber = subNumber;
//	}

//	public String getKeyword() {
//		return keyword;
//	}
//
//	public void setKeyword(String keyword) {
//		this.keyword = keyword;
//	}
//
//	public String getSupIri() {
//		return supIri;
//	}
//
//	public void setSupIri(String supIri) {
//		this.supIri = supIri;
//	}
//
//	public Set<String> getSubIri() {
//		return subIriList;
//	}
//
//	public void setSubIri(Set subIri) {
//		this.subIriList = subIri;
//	}

//	public String getSelectRdf() {
//		StringBuilder query = new StringBuilder();
//		query.append(getPrefix());
////		query.append(getSelect());
////		query.append(getFrom());
////		query.append(matchStatement != null ? " MATCH " + matchStatement : "");
//		query.append(getWhere() != null ? getWhere() : "");
////		query.append(" RETURN ").append(returnStatement);
////		query.append(getOrderBy() != null ? getOrderBy() : "");
//
//		String resultingQuery = query.toString();
//
//		if (logger.isDebugEnabled()) {
//			logger.debug(resultingQuery);
//		}
//
//		return resultingQuery;
//	}


	/*
	 * 
	 * total list:
	 * 
	 * PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> PREFIX toucan:
	 * <http://www.glytoucan.org/glyco/owl/glytoucan#> SELECT DISTINCT ?glycans
	 * ?AccessionNumber ?Seq from <http://glytoucan.org/rdf/demo/0.6> from
	 * <http://glytoucan.org/rdf/demo/0.6/rdf> WHERE { ?glycans a
	 * glycan:saccharide . ?glycans toucan:has_primary_id ?AccessionNumber .
	 * ?glycans glycan:has_glycosequence ?gseq . ?gseq glycan:has_sequence ?Seq
	 * . ?gseq glycan:in_carbohydrate_format glycan:carbohydrate_format_kcf }
	 * 
	 * 
	 * motifs to check:
	 * 
	 * PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> PREFIX toucan:
	 * <http://www.glytoucan.org/glyco/owl/glytoucan#> SELECT DISTINCT ?glycans
	 * ?AccessionNumber ?Seq from <http://glytoucan.org/rdf/demo/0.6> from
	 * <http://glytoucan.org/rdf/demo/0.6/rdf> WHERE { ?glycans a
	 * glycan:glycan_motif . ?glycans toucan:has_primary_id ?AccessionNumber .
	 * ?glycans glycan:has_glycosequence ?gseq . ?gseq glycan:has_sequence ?Seq
	 * . ?gseq glycan:in_carbohydrate_format glycan:carbohydrate_format_kcf }
	 * 
	 * 
	 * PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> PREFIX toucan:
	 * <http://www.glytoucan.org/glyco/owl/glytoucan#> SELECT DISTINCT ?glycans
	 * ?id ?Seq from <http://glytoucan.org/rdf/demo/0.6> from
	 * <http://bluetree.jp/test> WHERE { ?glycans a glycan:glycan_motif .
	 * ?glycans toucan:has_primary_id ?id . ?glycans glycan:has_glycosequence
	 * ?gseq . ?gseq glycan:has_sequence ?Seq . ?gseq
	 * glycan:in_carbohydrate_format glycan:carbohydrate_format_kcf }
	 * 
	 * glycan:has_motif
	 */

	// triple store batch process
	// conversion
	// retrieve a list of asc#
	// for each asc #
	// convert to kcf
	// insert into rdf

	// mass

	// motif/substructure
	// retrieve a list of asc#
	// for each asc# -
	// retrieve list of structures to find subs
	// search for substructure - kcam
	// insert substructure/motif
}