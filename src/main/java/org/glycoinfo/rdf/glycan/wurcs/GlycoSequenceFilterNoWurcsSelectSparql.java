package org.glycoinfo.rdf.glycan.wurcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.GlycoSequenceSelectSparql;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * SelectSparql for retrieving the GlycoCT where there is no Wurcs.
 * 
 * @author aoki
 *
 */
@Component
public class GlycoSequenceFilterNoWurcsSelectSparql extends GlycoSequenceSelectSparql implements InitializingBean {
	public static final String WurcsGlycanSequenceURI = "WurcsGlycanSequenceURI";
	public static final String WurcsSequence = "WurcsSequence";

	private boolean whereset = false;
	public GlycoSequenceFilterNoWurcsSelectSparql(String sparql) {
		super(sparql);
	}

	public GlycoSequenceFilterNoWurcsSelectSparql() {
		super();
		this.from = "FROM <http://rdf.glytoucan.org/core>\n"
		    + "FROM <http://rdf.glytoucan.org>\n"
				+ "FROM <http://rdf.glytoucan.org/sequence/wurcs>\n";
	}
	
	public String getPrimaryId() {
		return "\"" + getSparqlEntity().getValue(Saccharide.PrimaryId) + "\"";
	}


	@Override
	public String getWhere() throws SparqlException {
		String whereCopy = super.getWhere()
				+ "?" + GlycanSequenceURI + " glycan:in_carbohydrate_format glycan:carbohydrate_format_glycoct .\n"
				+ getFilter();
		
		return whereCopy;
	}

	protected Log logger = LogFactory.getLog(getClass());

	String glycanUri;

	public String getFilter() {
	    return "FILTER NOT EXISTS {"
		+ "?" + SaccharideURI + " glycan:has_glycosequence ?" + WurcsGlycanSequenceURI + " .\n"
		+ "?" + WurcsGlycanSequenceURI + " glycan:has_sequence ?" + WurcsSequence + " .\n"
		+ "?" + WurcsGlycanSequenceURI + " glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .\n"
				+ "}";
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.state(getPrefix() != null, "A ident is required");
		Assert.state(getSelect() != null, "A select is required");
	}
}