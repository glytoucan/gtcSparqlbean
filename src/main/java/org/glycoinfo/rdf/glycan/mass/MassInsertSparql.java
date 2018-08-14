package org.glycoinfo.rdf.glycan.mass;

import org.glycoinfo.rdf.glycan.DerivatizedMassInsertSparql;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 
 * An abstract class used to specify the insert command for when a
 * GlycanSequence is to be inserted after conversion. For example: glycoct
 * converted into wurcs.
 * 
 * @author aoki
 *
 */
public class MassInsertSparql extends DerivatizedMassInsertSparql implements InitializingBean {
	
	public MassInsertSparql() {
		super();
	}

	@Override
	public String getGraph() {
		return getGraphBase() + "/mass";
	}
	
//	// TODO: put in subclass
//	public String getFailInsert() {
//		String rdf = getInsert();
//		return rdf += getGlycanSequenceUri() + " rdfs:label \" ERROR IN "
//				+ getGlyConvert().getFromFormat() + " to "
//				+ getGlyConvert().getToFormat()
//				+ " CONVERSION\"^^xsd:string .\n";
//	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.state(getGraphBase() != null, "A graphbase is required");
	}
}