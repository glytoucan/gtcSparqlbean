package org.glycoinfo.rdf.glycan;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.springframework.stereotype.Component;

/**
 * 
 * SelectSparql for retrieving a saccharide based on identifier.
 * The filter removes any existing sequences in the getTo() of the GlyConvert.
 * 
 * For instance: Retrieving of original glycoct by using
 * org.glycoinfo.conversion.wurcs.GlycoctToWurcsConverter.
 * 
 * @author aoki
 *
 */
@Component
public class SaccharideSelectSparql extends SelectSparqlBean implements Saccharide {
	public static final String SaccharideURI = Saccharide.URI;
	public static final String Sequence = "Sequence";
	public static final String GlycanSequenceURI = "GlycanSequenceURI";
	public static final String AccessionNumber = Saccharide.PrimaryId;

	public SaccharideSelectSparql(String sparql) {
		super(sparql);
	}

	public SaccharideSelectSparql() {
		super();
		this.prefix = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n";
		this.select = "DISTINCT ?" + SaccharideURI + "\n?" + PrimaryId + "\n";
//		this.from = "FROM <http://rdf.glytoucan.org/core>\n";
	}
	
	public String getPrimaryId() {
		return "\"" + getSparqlEntity().getValue(Saccharide.PrimaryId) + "\"";
	}

	@Override
	public String getWhere() throws SparqlException {
		String lWhere = "?" + SaccharideURI + " a glycan:saccharide .\n?" + SaccharideURI + " glytoucan:has_primary_id ?" + PrimaryId + " .\n";
		if (null != getSparqlEntity() && StringUtils.isNotBlank(getSparqlEntity().getValue(PrimaryId)))
				lWhere += "?" + SaccharideURI + " glytoucan:has_primary_id " + getPrimaryId() + " .\n";
		return lWhere;
	}

	protected Log logger = LogFactory.getLog(getClass());
}