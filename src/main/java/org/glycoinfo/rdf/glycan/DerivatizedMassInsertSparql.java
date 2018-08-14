package org.glycoinfo.rdf.glycan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.UriProvider;
//
public class DerivatizedMassInsertSparql extends InsertSparqlBean implements DerivatizedMass {

	String type = "a glycan:saccharide";
	String hasPrimaryId = "glytoucan:has_primary_id";

	public DerivatizedMassInsertSparql() {
		this.prefix="PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>\n";
	}
	
	public String getInsert() throws SparqlException {
// <http://rdf.glycoinfo.org/glycan/G00054MO> glytoucan:has_derivatized_mass <http://rdf.glycoinfo.org/derivatization_type_none/820.2960859799999> .
// <http://rdf.glycoinfo.org/derivatization_type_none/820.2960859799999> a	glytoucan:derivatized_mass .
// <http://rdf.glycoinfo.org/derivatization_type_none/820.2960859799999> glytoucan:has_derivatization_type	glytoucan:derivatization_type_none ;
// <http://rdf.glycoinfo.org/derivatization_type_none/820.2960859799999> glytoucan:has_mass "820.2960859799999"^^xsd:double .
		
		Object saccharideUri = getSparqlEntity().getObjectValue(Saccharide.URI);
		if (null == saccharideUri)
			throw new SparqlException("Saccharide.URI required");

		String saccharideRelation = null;
		if (saccharideUri instanceof UriProvider) {		
			if (null != getSparqlEntity().getValue(Saccharide.URI)) {
				saccharideRelation = "<" + ((UriProvider)saccharideUri).getUri() + ">";
			}
		} else if (saccharideUri instanceof String) {
			saccharideRelation = getSaccharideURI();
		}
		
		String rdf = saccharideRelation + " glytoucan:has_derivatized_mass " + getURI() + " .\n" +
				getURI() + " a glytoucan:derivatized_mass .\n" +
				getURI() + " glytoucan:has_derivatization_type glytoucan:derivatization_type_none .\n"
						+ getURI() + " rdfs:label \"" + getSparqlEntity().getValue(MassLabel) + "\"^^xsd:string .\n";

				if (getSparqlEntity().getValue(Mass) != null)
					rdf += getURI() + " glytoucan:has_mass \"" + getSparqlEntity().getValue(Mass) + "\"^^xsd:double .\n";
				
		return rdf;
	}

	public String getSaccharideURI() {
		return "<" + getSparqlEntity().getValue(Saccharide.URI) + ">";
	}

	public String getURI() {
		try {
			return "<http://rdf.glycoinfo.org/derivatization_type_node/" + URLEncoder.encode(getSparqlEntity().getValue(MassLabel), "UTF-8") + ">";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
}