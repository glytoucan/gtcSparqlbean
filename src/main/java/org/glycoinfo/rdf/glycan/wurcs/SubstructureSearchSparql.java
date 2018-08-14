package org.glycoinfo.rdf.glycan.wurcs;

import org.apache.commons.lang.StringUtils;
import org.glycoinfo.WURCSFramework.util.array.WURCSFormatException;
import org.glycoinfo.WURCSFramework.util.array.WURCSImporter;
import org.glycoinfo.WURCSFramework.util.exchange.WURCSArrayToSequence2;
import org.glycoinfo.WURCSFramework.util.rdf.WURCSSequence2ExporterSPARQL;
import org.glycoinfo.WURCSFramework.wurcs.array.WURCSArray;
import org.glycoinfo.WURCSFramework.wurcs.sequence2.WURCSSequence2;
import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.Motif;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubstructureSearchSparql extends SelectSparqlBean {

	public static final String SubstructureSearchSaccharideURI = "glycan";
	public static final String LIMITID = "substructureSearchLimitFlag";
	
	public static Logger logger = (Logger) LoggerFactory
			.getLogger(SubstructureSearchSparql.class);

	private String graphtarget;

	private String graphms;
	private boolean includeItself;

	public boolean isFilterOutSelf() {
		return includeItself;
	}

	public void setFilterOutSelf(boolean includeItself) {
		this.includeItself = includeItself;
	}

	public String getGraphtarget() {
		return graphtarget;
	}

	public void setGraphtarget(String graphtarget) {
		this.graphtarget = graphtarget;
	}

	public String getGraphms() {
		return graphms;
	}

	public void setGraphms(String graphms) {
		this.graphms = graphms;
	}

	public SubstructureSearchSparql() {
		this.define = "DEFINE sql:select-option \"order\"";
		this.prefix = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#>";
		this.select = "DISTINCT ?" + SubstructureSearchSaccharideURI;
	}

	@Override
	public String getFrom() {
		return "FROM " + getGraphms() + "\n"
				+ "FROM " + getGraphtarget() + "\n"
				+ "FROM <http://rdf.glytoucan.org/core>\n";
	}

	@Override
	public String getWhere() throws SparqlException {
		this.where = "";
		String t_strSPARQL = "";
		String limitingIdFlag = getSparqlEntity().getValue(LIMITID);
		
		if (StringUtils.isNotBlank(limitingIdFlag)) {
			String limitingId = getSparqlEntity().getValue(GlycoSequence.AccessionNumber);
			t_strSPARQL = "?glycan glytoucan:has_primary_id \"" + limitingId + "\" .\n";
		}

		WURCSSequence2ExporterSPARQL t_oExport = getExporter(getSparqlEntity().getValue(GlycoSequence.Sequence));
		
		t_strSPARQL += t_oExport.getWhere();

		this.where += t_strSPARQL;
		
		if (isFilterOutSelf()) {
			this.where += "?glycan  glytoucan:has_primary_id ?primaryId .\n"
					+ "FILTER (?primaryId != \"" + getSparqlEntity().getValue(GlycoSequence.AccessionNumber) + "\")";
		}
		return where;
	}
	
	WURCSSequence2ExporterSPARQL getExporter(String sequence) throws SparqlException {
		WURCSImporter t_oImport = new WURCSImporter();
		WURCSArray t_oWURCS;
		try {
			t_oWURCS = t_oImport.extractWURCSArray(sequence);
		} catch (WURCSFormatException e) {
			e.printStackTrace();
			throw new SparqlException(e);
		}

		WURCSArrayToSequence2 t_oA2S = new WURCSArrayToSequence2();
		t_oA2S.start(t_oWURCS);
		
		WURCSSequence2 t_oSeq = t_oA2S.getSequence();

		WURCSSequence2ExporterSPARQL t_oExport = getExporter();
		
		t_oExport.start(t_oSeq);
		return t_oExport;
	}

	@Override
	public String getOrderBy() {
		try {
			return getExporter(getSparqlEntity().getValue(GlycoSequence.Sequence)).getOrderByString();
		} catch (SparqlException e) {
			logger.error(e.getMessage());
			return super.getOrderBy();
		}
	}

	WURCSSequence2ExporterSPARQL getExporter() {
		WURCSSequence2ExporterSPARQL t_oExport = new WURCSSequence2ExporterSPARQL();

		// Set option for SPARQL query generator
		t_oExport.setCountOption(true); // True: Count result
		t_oExport.addTargetGraphURI(getGraphtarget()); // Add your terget graph
		//t_oExport.setMSGraphURI("<http://rdf.glycoinfo.org/wurcs/0.5.1/ms>"); // Set your monosaccharide graph
		t_oExport.hideComments(true); // Hide all comments in query
		t_oExport.setSearchSupersumption(true); // Search supersumption of monosaccharide
		t_oExport.setMSGraphURI(getGraphms());
		t_oExport.setSearchSupersumption(false);

		logger.debug("set setSearchSupersumption false");
		
		String reducing = getSparqlEntity().getValue(Motif.ReducingEnd);
		if (StringUtils.isNotBlank(reducing) && reducing.equals(SelectSparql.TRUE))
			t_oExport.setSpecifyRootNode(true);
		return t_oExport;
	}
}