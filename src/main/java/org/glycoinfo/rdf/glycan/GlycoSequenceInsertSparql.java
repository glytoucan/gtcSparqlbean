package org.glycoinfo.rdf.glycan;

import org.apache.commons.lang.StringUtils;
import org.glycoinfo.rdf.InsertSparqlBean;

/**
 * 
 * An abstract class used to specify the insert command for when a
 * GlycanSequence is to be inserted after conversion.  For example: glycoct
 * converted into wurcs.
 * 
 * Required fields are: Saccharide.PrimaryId, GlycoSequence.Format, and GlycoSequence.Sequence.  GlycoSequence.ErrorMessage is optional to add an rdfs:label.
 * @author aoki
 *
 * This work is licensed under the Creative Commons Attribution 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.
 *
 */
public class GlycoSequenceInsertSparql extends InsertSparqlBean {
	
	
	String graphBase;
	
	public String getGraphBase() {
		return graphBase;
	}

	public void setGraphBase(String graphBase) {
		this.graphBase = graphBase;
	}

	public GlycoSequenceInsertSparql(String sparql) {
		super(sparql);
	}

	public GlycoSequenceInsertSparql() {
		super();
	}

	public String getSaccharideURI() {
		if (null != getSparqlEntity().getValue(Saccharide.URI))
			return "<" + getSparqlEntity().getValue(Saccharide.URI) + ">";
		else 
			return "<http://rdf.glycoinfo.org/glycan/" + getSparqlEntity().getValue(Saccharide.PrimaryId) + ">";
	}

	public String getGlycanSequenceUri() {
		// getSparqlEntity().getValue(ConvertSelectSparql.GlycanSequenceURI)
		return "<http://rdf.glycoinfo.org/glycan/"
				+ getSparqlEntity().getValue(Saccharide.PrimaryId)
				+ "/"
				+ getSparqlEntity()
						.getValue(GlycoSequence.Format) + ">";
	}

//	public String getUsing() {
//		return "USING <http://glytoucan.org/rdf/demo/0.2>\n"
//				+ "USING <http://glytoucan.org/rdf/demo/msdb/8>\n"
//				+ "USING <http://purl.jp/bio/12/glyco/glycan/ontology/0.18>\n"
//				+ "USING <http://www.glytoucan.org/glyco/owl/glytoucan>\n";
//	}

	@Override
	public String getInsert() {
		String saccharideURI = getSaccharideURI();
		String rdf = saccharideURI + " glycan:has_glycosequence " + getGlycanSequenceUri() + " .\n" + 
		(getSparqlEntity().getValue(GlycoSequence.Sequence) != null? getGlycanSequenceUri()	+ " glycan:has_sequence \""	+ getSparqlEntity().getValue(GlycoSequence.Sequence) + "\"^^xsd:string .\n" : "") +
		(getSparqlEntity().getValue(GlycoSequence.Sequence) != null? getGlycanSequenceUri()	+ " rdfs:label \""	+ getSparqlEntity().getValue(GlycoSequence.Sequence) + "\"^^xsd:string .\n" : "")
				+ getGlycanSequenceUri() + " glycan:in_carbohydrate_format " + getCarbFormat() + " .\n" +
		(getSparqlEntity().getValue(GlycoSequence.ErrorMessage) != null? getGlycanSequenceUri()	+ " rdfs:label \""	+ getSparqlEntity().getValue(GlycoSequence.ErrorMessage) + "\"^^xsd:string .\n" : "");

		// String rdf = saccharideURI +
		// " glycan:has_glycosequence ?glycanSeqUri .\n"
		// + "?glycanSeqUri glycan:has_sequence \"" + getSequence() +
		// "\"^^xsd:string .\n"
		// + "?glycanSeqUri glycan:in_carbohydrate_format " + getCarbFormat() +
		// " .\n"
		// + "?glycanSeqUri glytoucan:is_glycosequence_of " + saccharideURI +
		// " .\n";
		return rdf;
	}

	public String getCarbFormat() {
		return "glycan:carbohydrate_format_"
				+ getSparqlEntity()
						.getValue(GlycoSequence.Format);
	}

	@Override
	public String getPrefix() {
//		@prefix rdsf: <http://www.w3.org/2000/01/rdf-schema#> . 
//			@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . 
//			@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . 
//			@prefix owl: <http://www.w3.org/2002/07/owl#> . 
//			@prefix glycan: <http://purl.jp/bio/12/glyco/glycan#> . 
//			@prefix glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#> . 
//			@prefix wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#> . 
//			@prefix dcterms: <http://purl.org/dc/terms/> .
		return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>\n";
	}

	// public String getWhere() {
	// return "{ ?s a glycan:saccharide . \n"
	// + "?s glytoucan:has_primary_id ?AccessionNumber . \n"
	// + "?s glycan:has_glycosequence ?gseq . \n"
	// + "?gseq glycan:has_sequence ?Seq . \n"
	// +
	// "?gseq glycan:in_carbohydrate_format glycan:carbohydrate_format_glycoct \n"
	// + getFilter() + " }\n";
	// }
	
	public String getTtl() {
		String rdf = getSaccharideURI() + " glycan:has_glycosequence " + getGlycanSequenceUri() + " .\n" + 
				getGlycanSequenceUri()	+ " glycan:has_sequence \""	+ getSparqlEntity().getValue(GlycoSequence.Sequence) + "\"^^xsd:string .\n" 
				+ getGlycanSequenceUri() + " glycan:in_carbohydrate_format " + getCarbFormat() + " .\n"
				+ getGlycanSequenceUri() + " glytoucan:is_glycosequence_of " + getSaccharideURI() + " .\n";
		return rdf;
	}

	@Override
	public String getGraph() {
		if (StringUtils.isBlank(super.getGraph()))
			return getGraphBase() + "/sequence/" + getSparqlEntity().getValue(GlycoSequence.Format);
		return super.getGraph();
	}
	
	// public String getFilter() {
	// return "FILTER NOT EXISTS {\n"
	// + "?s glycan:has_glycosequence ?kseq .\n"
	// + "?kseq glycan:has_sequence ?kSeq .\n"
	// + "?kseq glycan:in_carbohydrate_format glycan:carbohydrate_format_"
	// + () + "\n" + "}";
	// }
	
}