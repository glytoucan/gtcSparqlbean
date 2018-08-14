package org.glycoinfo.rdf.glycan;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.rdf.SparqlException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * Retrieves the sequence information for a saccharide with GlycoSequence.Format format.
 * 
 * The filter removes any existing sequences in the getTo() of the GlyConvert.
 * 
 * For instance: Retrieving of original glycoct by using
 * org.glycoinfo.conversion.wurcs.GlycoctToWurcsConverter.
 * 
 * @author aoki
 *
 */
@Component
public class GlycoSequenceSelectSparql extends SaccharideSelectSparql implements InitializingBean, GlycoSequence {
	public static final String SaccharideURI = Saccharide.URI;
	public static final String Sequence = "Sequence";
	public static final String GlycanSequenceURI = "GlycanSequenceURI";

	public GlycoSequenceSelectSparql(String sparql) {
		super(sparql);
	}

	public GlycoSequenceSelectSparql() {
		super();
		this.prefix = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>";
        this.select = super.getSelect() + " ?" + Sequence + " \n";
//        this.select = super.getSelect() + " ?" + Sequence + " ?" + GlycanSequenceURI + " \n";
//		this.select = super.getSelect() + " ?" + Sequence + " ?" + GlycanSequenceURI + " ?" + Label + " \n";
		this.from = "FROM <http://rdf.glytoucan.org/core>\n"
				+ "FROM <http://rdf.glytoucan.org/sequence/wurcs>";
	}
	
	public String getPrimaryId() {
		return "\"" + getSparqlEntity().getValue(Saccharide.PrimaryId) + "\"";
	}


	@Override
	public String getWhere() throws SparqlException {
		String whereTmp = super.getWhere();
		whereTmp += "?" + SaccharideURI + " glycan:has_glycosequence ?" + GlycanSequenceURI + " .\n";
		if (StringUtils.isNotBlank(getSparqlEntity().getValue(GlycoSequence.Sequence))) {
			whereTmp += "?" + GlycanSequenceURI + " glycan:has_sequence \"" + getSparqlEntity().getValue(GlycoSequence.Sequence) + "\"^^<http://www.w3.org/2001/XMLSchema#string> .\n"
			    + "?" + GlycanSequenceURI + " glycan:has_sequence ?" + Sequence + " .\n";
//			    + "?" + GlycanSequenceURI + " rdfs:label \"" + getSparqlEntity().getValue(GlycoSequence.Sequence) + "\"^^<http://www.w3.org/2001/XMLSchema#string> .\n"
//          + "?" + GlycanSequenceURI + " rdfs:label ?" + Label + " .\n";
		} else
			whereTmp += "?" + GlycanSequenceURI + " glycan:has_sequence ?" + Sequence + " .\n";

		if (StringUtils.isNotBlank(getSparqlEntity().getValue(GlycoSequence.Format))) {
			whereTmp += "?" + GlycanSequenceURI + " glycan:in_carbohydrate_format glycan:carbohydrate_format_" + getSparqlEntity().getValue(GlycoSequence.Format) + " .\n";
		}
		return whereTmp;
	}

	protected Log logger = LogFactory.getLog(getClass());

	String glycanUri;

	/**
	 * 
	 * the filter removes any sequences that already have a sequence in the
	 * GlyConvert.getTo() format.
	 * 
	 * @return
	 */
	public String getFilter() {
		if (getSparqlEntity().getValue(IdentifiersToIgnore) != null) {
//			FILTER (?primaryId != "G95801EZ")} 
			String filter = null;
			List<String> ignores = (List<String>) getSparqlEntity().getObjectValue(IdentifiersToIgnore);
			for (Iterator iterator = ignores.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				filter = " ?" + Saccharide.PrimaryId + " != \"" + string + "\" ";
				if (iterator.hasNext())
					filter += " && ";
			}
			return "FILTER (" + filter + ")";
		} else {
			return "FILTER NOT EXISTS {\n"
				+ "?" + SaccharideURI + " glytoucan:has_derivatized_mass ?existingmass .\n}";
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.state(getPrefix() != null, "A prefix is required");
		Assert.state(getSelect() != null, "A select is required");
	}
}