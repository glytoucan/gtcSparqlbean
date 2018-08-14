package org.glycoinfo.rdf.glycan.wurcs;

import java.util.LinkedList;
import java.util.TreeSet;

import org.glycoinfo.WURCSFramework.util.WURCSException;
import org.glycoinfo.WURCSFramework.util.WURCSFactory;
import org.glycoinfo.WURCSFramework.util.array.WURCSExporter;
import org.glycoinfo.WURCSFramework.util.array.WURCSFormatException;
import org.glycoinfo.WURCSFramework.util.array.WURCSImporter;
import org.glycoinfo.WURCSFramework.wurcs.array.MS;
import org.glycoinfo.WURCSFramework.wurcs.array.UniqueRES;
import org.glycoinfo.WURCSFramework.wurcs.array.WURCSArray;
import org.glycoinfo.WURCSFramework.wurcs.rdf.WURCSRDFModelMS;
import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WurcsRDFMSInsertSparql extends InsertSparqlBean {
	
	private static final Logger logger = LoggerFactory.getLogger(WurcsRDFMSInsertSparql.class);

	
	public WurcsRDFMSInsertSparql() {
		super();
		this.prefix="PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> \nPREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#> \n";
	}
	
	@Override
	public String getInsert() throws SparqlException  {
		
		// Output prefix
		Boolean t_bPrefix = false;

//		WURCSImporter ws = new WURCSImporter();
//		WURCSExporterRDF rdf = new WURCSExporterRDF();
		String sequence = getSparqlEntity().getValue(GlycoSequence.Sequence);
//		String id = getSparqlEntity().getValue(Saccharide.PrimaryId);
//		WURCSArray wurcs = null;
//		try {
//			wurcs = ws.extractWURCSArray(sequence);
//		} catch (WURCSFormatException e) {
//			e.printStackTrace();
//			logger.debug(e.getMessage());
//		}
//		rdf.setWURCSrdfTriple(id, wurcs, false);
//		String wurcsrdf = rdf.getWURCS_monosaccharide_RDF();
//		logger.debug(wurcsrdf);
//		String withPrefix = prefix + wurcsrdf;
//		return withPrefix;
//		sbMS.append(rdf.getWURCS_monosaccharide_RDF());

		LinkedList<MS> t_aUniqueMSs = new LinkedList<MS>();
		TreeSet<String> t_setUniqueMSs = new TreeSet<String>();
//		// Collect unique MSs
		WURCSExporter t_oExport = new WURCSExporter();
		WURCSFactory t_oFactory;
		try {
			t_oFactory = new WURCSFactory(sequence);
		} catch (WURCSException e) {
			e.printStackTrace();
			throw new SparqlException(e);
		}
		WURCSArray t_oArray = t_oFactory.getArray();
		WURCSImporter t_oImport = new WURCSImporter();

		for ( UniqueRES t_oURES : t_oArray.getUniqueRESs() ) {
			String t_strMS = t_oExport.getUniqueRESString(t_oURES);
			if ( t_setUniqueMSs.contains(t_strMS) ) continue;
			t_setUniqueMSs.add(t_strMS);
		}

		for ( String t_strMS : t_setUniqueMSs )
			try {
				t_aUniqueMSs.add( t_oImport.extractMS(t_strMS) );
			} catch (WURCSFormatException e) {
				e.printStackTrace();
				throw new SparqlException(e);
			}

		System.out.println("Total MS:"+t_setUniqueMSs.size());
		
		WURCSRDFModelMS t_oMSExport = new WURCSRDFModelMS(t_aUniqueMSs, t_bPrefix);
		String wurcsrdf = t_oMSExport.get_RDF("TURTLE");
		logger.debug("MS data:>" + wurcsrdf);

		return wurcsrdf;
	}
	
//	@Override
//	public String getFormat() {
//		return InsertSparql.Turtle;
//	}
}