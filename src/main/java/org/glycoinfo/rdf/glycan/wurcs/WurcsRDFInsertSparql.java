package org.glycoinfo.rdf.glycan.wurcs;


import java.util.TreeSet;

import org.glycoinfo.WURCSFramework.util.WURCSException;
import org.glycoinfo.WURCSFramework.util.WURCSFactory;
import org.glycoinfo.WURCSFramework.util.array.WURCSExporter;
import org.glycoinfo.WURCSFramework.util.array.WURCSImporter;
import org.glycoinfo.WURCSFramework.wurcs.array.UniqueRES;
import org.glycoinfo.WURCSFramework.wurcs.array.WURCSArray;
import org.glycoinfo.WURCSFramework.wurcs.rdf.WURCSExporterRDF;
import org.glycoinfo.WURCSFramework.wurcs.rdf.WURCSRDFModelGlycan051;
import org.glycoinfo.WURCSFramework.wurcs.rdf.WURCSSequence2ExporterRDFModel;
import org.glycoinfo.WURCSFramework.wurcs.sequence2.WURCSSequence2;
import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WurcsRDFInsertSparql extends InsertSparqlBean {
	
	
	private static final Logger logger = LoggerFactory.getLogger(WurcsRDFInsertSparql.class);

	
	public WurcsRDFInsertSparql() {
		super();
		this.prefix="PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> \nPREFIX wurcs: <http://www.glycoinfo.org/glyco/owl/wurcs#> \n";
	}
	
	@Override
	public String getInsert() throws SparqlException  {
		WURCSImporter ws = new WURCSImporter();
		WURCSExporterRDF rdf = new WURCSExporterRDF();
		String sequence = getSparqlEntity().getValue(GlycoSequence.Sequence);
		String id = getSparqlEntity().getValue(Saccharide.PrimaryId);

		WURCSFactory t_oFactory;
		try {
			t_oFactory = new WURCSFactory(sequence);
		} catch (WURCSException e) {
			e.printStackTrace();
			throw new SparqlException(e);
		}
		TreeSet<String> t_setUniqueMSs = new TreeSet<String>();

		// Collect unique MSs
		WURCSExporter t_oExport = new WURCSExporter();
		WURCSArray t_oArray = t_oFactory.getArray();
		for ( UniqueRES t_oURES : t_oArray.getUniqueRESs() ) {
			String t_strMS = t_oExport.getUniqueRESString(t_oURES);
			if ( t_setUniqueMSs.contains(t_strMS) ) continue;
			t_setUniqueMSs.add(t_strMS);
		}

		System.out.println(sequence);
		String t_strAccessionNumber = id;

		// Generate RDF strings (ver 0.5.1)
		WURCSRDFModelGlycan051 t_oRDFExport2 = new WURCSRDFModelGlycan051( t_strAccessionNumber, t_oArray, false );
		
		
		String wurcsrdf = t_oRDFExport2.get_RDF("TURTLE") ;
		
		
//		wurcsrdf = wurcsrdf.replaceAll("[|]", unicodeEscaped('|'));

		// For using WURCSSequence
		WURCSSequence2 t_oSeq2 = t_oFactory.getSequence();

		WURCSSequence2ExporterRDFModel t_oSeq2Export = new WURCSSequence2ExporterRDFModel( id, t_oSeq2, false );

		String wurcsseq2 = t_oSeq2Export.get_RDF("TURTLE");
		
//		wurcsseq2 = wurcsseq2.replaceAll("[|]", unicodeEscaped('|'));
		wurcsrdf += wurcsseq2;
		logger.debug(wurcsrdf);
		
		
//		String withPrefix = prefix + wurcsrdf;
//		return withPrefix;
//		sbMS.append(rdf.getWURCS_monosaccharide_RDF());
		return wurcsrdf;
	}
	
//	@Override
//	public String getFormat() {
//		return InsertSparql.Turtle;
//	}
	
	  public static String unicodeEscaped(char ch) {
	      if (ch < 0x10) {
	          return "\\u000" + Integer.toHexString(ch);
	      } else if (ch < 0x100) {
	          return "\\u00" + Integer.toHexString(ch);
	      } else if (ch < 0x1000) {
	          return "\\u0" + Integer.toHexString(ch);
	      }
	      return "\\u" + Integer.toHexString(ch);
	  }
}