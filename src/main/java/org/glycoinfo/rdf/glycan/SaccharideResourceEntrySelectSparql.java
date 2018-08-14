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
public class SaccharideResourceEntrySelectSparql extends SaccharideSelectSparql implements ResourceEntry  {

	public SaccharideResourceEntrySelectSparql(String sparql) {
		super(sparql);
	}

	public SaccharideResourceEntrySelectSparql() {
		super();
		this.select = super.getSelect() + "?" + Identifier + "\n";
	}

	@Override
	public String getWhere() throws SparqlException {
		String lWhere = super.getWhere() + "?" + SaccharideURI + " glycan:has_resource_entry ?" + ResourceEntryURI + " .\n"
				+ "?" + ResourceEntryURI + " dcterms:identifier ?" + Identifier + " .\n";
		if (StringUtils.isNotBlank(getSparqlEntity().getValue(Identifier)))
			lWhere += "?" + ResourceEntryURI + " dcterms:identifier \"" + getSparqlEntity().getValue(Identifier) + "\" .\n";
		return lWhere;
	}

	protected Log logger = LogFactory.getLog(getClass());
}