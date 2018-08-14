package org.glycoinfo.rdf.glycan;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.WURCSFramework.util.WURCSException;
import org.glycoinfo.WURCSFramework.util.WURCSFactory;
import org.glycoinfo.convert.GlyConvert;
import org.glycoinfo.rdf.DeleteSparql;
import org.glycoinfo.rdf.DeleteSparqlBean;
import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.virt.VirtSesameTransactionConfig;
import org.glycoinfo.rdf.glycan.ContributorInsertSparqlTest;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.GlycoSequenceInsertSparql;
import org.glycoinfo.rdf.glycan.GlycoSequenceSelectSparql;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SparqlDAOImplTest.class, VirtSesameTransactionConfig.class })
@EnableAutoConfiguration
public class SparqlDAOImplTest {

	public static Log logger = (Log) LogFactory
			.getLog("org.glytoucan.registry.dao.test.SchemaDAOImplTest");

	@Autowired
	SparqlDAO schemaDAO;
 
	public static final String prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"
			+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> \n"
			+ "PREFIX dcterms: <http://purl.org/dc/terms/> \n"
			+ "PREFIX dbpedia2: <http://dbpedia.org/property/> \n"
			+ "PREFIX dbpedia: <http://dbpedia.org/> \n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n"
			+ "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> \n"
			+ "PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#> \n";

	public static final String from = "from <http://rdf.glytoucan.org/core>\n"
			+ "from <http://glytoucan.org/rdf/demo/msdb/7>\n"
			+ "from <http://purl.jp/bio/12/glyco/glycan/ontology/0.18>\n"
			+ "from <http://www.glytoucan.org/glyco/owl/glytoucan>\n";

	public static final String using = "USING <http://glytoucan.org/rdf/demo/0.8>\n"
			+ "USING <http://glytoucan.org/rdf/demo/msdb/8>\n"
			+ "USING <http://purl.jp/bio/12/glyco/glycan/ontology/0.18>\n"
			+ "USING <http://www.glytoucan.org/glyco/owl/glytoucan>\n";

	@Test
	@Transactional
	public void testQuery() {
//		String query = "SELECT ?s ?v ?type WHERE { ?s ?v ?type . } LIMIT 100";
		String query = "SELECT  ?s ?v ?o WHERE\n" +
                "  { ?s ?v ?o . }\n" +
                "LIMIT   5\n" +
                "";
		try {
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			SparqlEntity row = list.get(0);
			logger.debug("Node:>" + row.getValue("s"));
			logger.debug("graph:>" + row.getValue("type"));
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}
	}

	@Test
	@Transactional
	public void testQuery2() {
		String query = "SELECT distinct ?s WHERE  {[] a ?s}  LIMIT 100";
		try {
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			SparqlEntity row = list.get(0);
			logger.debug("Node:>" + row.getValue("s"));
			logger.debug("graph:>" + row.getValue("graph"));
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}
	}

	public void testQueryOptional() {
		String query = prefix
				+ "SELECT DISTINCT  ?AccessionNumber ?img ?Mass ?Motif ?ID ?Contributor ?time\n"
				+ from
				+ "WHERE {\n"
				+ "?s a glycan:saccharide .\n"
				+ "?s glytoucan:has_primary_id ?AccessionNumber .\n"
				+ "?s glycan:has_image ?img .\n"
				+ "?img a glycan:image .\n"
				+ "?img dc:format \"image/png\"^^xsd:string .\n"
				+ "?img glycan:has_symbol_format glycan:symbol_format_cfg .\n"
				+ "?s glytoucan:has_derivatized_mass ?dmass.\n"
				+ "?dmass a glytoucan:derivatized_mass .\n"
				+ "?dmass glytoucan:has_derivatization_type glytoucan:derivatization_type_none .\n"
				+ "?dmass glytoucan:has_mass ?Mass .\n" + "OPTIONAL{\n"
				+ "?s glycan:has_motif ?motif .\n"
				+ "?motif a glycan:glycan_motif .\n"
				+ "?motif foaf:name ?Motif .\n"
				+ "?motif glytoucan:has_primary_id ?ID .\n" + "}\n"
				+ "?s glycan:has_resource_entry ?entry.\n"
				+ "?entry a glycan:resource_entry .\n"
				+ "?entry glytoucan:contributor ?contributor .\n"
				+ "?contributor foaf:name ?Contributor .\n"
				+ "?entry glytoucan:date_registered ?time .\n" + "} LIMIT 10";
		try {
			logger.debug("query:>" + query + "<");
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			if (list.size() > 0) {
				SparqlEntity row = list.get(0);
				logger.debug("Node:>" + row.getValue("s"));
				logger.debug("graph:>" + row.getValue("graph"));
			} else {
				fail();
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}
	}

	public void testInsertUnicode() throws SparqlException {
		schemaDAO
				.insert(new InsertSparqlBean("insert data { \n"
						+ "graph <http://bluetree.jp/nobutest> {\n"
						+ "<http://bluetree.jp/nobutest/aa\\u007c> <http://bluetree.jp/nobutest/b\\u0026b> \"c?c\" . \n"
//						+ "<http://bluetree.jp/nobutest/xx" + unicodeEscaped('?') + "f=s"+ unicodeEscaped('|') + "f> <http://bluetree.jp/nobutest/yy> <http://bluetree.jp/nobutest/zz> . \n"
						+ "<http://bluetree.jp/nobutest/mm> <http://bluetree.jp/nobutest/nn> \"Some\\nlong\\nliteral\\nwith language\" . \n"
						+ "<http://bluetree.jp/nobutest/oo> <http://bluetree.jp/nobutest/pp> \"12345\"^^<http://www.w3.org/2001/XMLSchema#int>\n  "
						+ "}\n"
						+ "}"));
		String query = prefix + "SELECT ?s ?v ?o\n" + "from <http://bluetree.jp/nobutest>\n"
				+ "WHERE { ?s ?v ?o } limit 10";

		try {
			logger.debug("query:>" + query);
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			if (list.size() > 0) {
				SparqlEntity row = list.get(0);
				logger.debug("s:>" + row.getValue("s"));
				logger.debug("v:>" + row.getValue("v"));
				logger.debug("o:>" + row.getValue("o"));
			} else
				fail();
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}
	}
	
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
	
	@Test
	@Transactional
	public void testInsert() throws SparqlException {
		schemaDAO
				.insert(new InsertSparqlBean("insert data { graph <http://bluetree.jp/nobutest> { <http://bluetree.jp/nobutest/aa> <http://bluetree.jp/nobutest/bb> \"cc\" . \n"
								+ "<http://bluetree.jp/nobutest/xx> <http://bluetree.jp/nobutest/yy> <http://bluetree.jp/nobutest/zz> . \n"
								+ "<http://bluetree.jp/nobutest/mm> <http://bluetree.jp/nobutest/nn> \"Some\\nlong\\nliteral\\nwith language\" . \n"
								+ "<http://bluetree.jp/nobutest/oo> <http://bluetree.jp/nobutest/pp> \"12345\"^^<http://www.w3.org/2001/XMLSchema#int>\n  } }"));
		String query = prefix + "SELECT ?s ?v ?o\n" + "from <http://bluetree.jp/nobutest>\n"
				+ "WHERE { ?s ?v ?o } limit 10";

		try {
			logger.debug("query:>" + query);
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			if (list.size() > 0) {
				SparqlEntity row = list.get(0);
				logger.debug("s:>" + row.getValue("s"));
				logger.debug("v:>" + row.getValue("v"));
				logger.debug("o:>" + row.getValue("o"));
			} else
				fail();
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}
	}

	@Test
	 @Transactional
	public void testConvertQuery() {
		String query = prefix
				+ "SELECT DISTINCT ?s ?AccessionNumber ?Seq ?type\n" 
				+ "WHERE {" + "?s a glycan:saccharide . "
				+ "?s glytoucan:has_primary_id ?AccessionNumber . "
				+ "?s glycan:has_glycosequence ?gseq . "
				+ "?gseq glycan:has_sequence ?Seq . \n"
				+ "?gseq glycan:in_carbohydrate_format ?type} LIMIT 10";

		try {
			logger.debug("query:>" + query);
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			if (list.size() > 0) {
				SparqlEntity row = list.get(0);
				logger.debug("s:>" + row.getValue("s"));
				logger.debug("Seq:>" + row.getValue("Seq"));
				logger.debug("type:>" + row.getValue("type"));
				logger.debug("AccessionNumber:>"
						+ row.getValue("AccessionNumber"));
			} else
				fail();
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}
	}

//	@Test
	@Transactional
	public void testInsertConvert() throws SparqlException {
		schemaDAO
				.insert(new InsertSparqlBean("insert into graph <nobutest>  {"
						+ "<http://www.glycoinfo.org/rdf/glycan/G63838JW/sequence>"
								+ "        a                              glycan:glycosequence ;\n"
								+ "        glycan:has_sequence            "
								+ "\"ENTRY         CT-1             Glycan"
								+ "NODE  10"
								+ "     1  GlcNAc   0   0"
								+ "     2  GlcNAc   -8   0"
								+ "     3  GlcNAc   -32   4"
								+ "     4  GlcNAc   -32   8"
								+ "     5  Man   -24   6"
								+ "     6  Man   -32   -8"
								+ "     7  Gal   -40   8"
								+ "     8  Man   -16   0"
								+ "     9  Man   -32   -4"
								+ "     10  Man   -24   -6"
								+ "EDGE  9"
								+ "     1  7:b1     4:4"
								+ "     2  10:a1     8:6"
								+ "     3  6:a1     10:3"
								+ "     4  9:a1     10:6"
								+ "     5  2:b1     1:4"
								+ "     6  8:b1     2:4"
								+ "     7  5:a1     8:3"
								+ "     8  3:b1     5:2"
								+ "     9  4:b1     5:4"
								+ "///\""
								+ "^^xsd:string ;\n"
								+ "        glycan:in_carbohydrate_format  glycan:carbohydrate_format_kcf .\n }"));

		String query = "SELECT ?s ?v ?o\n" + "from <nobutest>\n"
				+ "WHERE { ?s ?v ?o }";

		try {
			logger.debug("query:>" + query);
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			if (list.size() > 0) {
				SparqlEntity row = list.get(0);
				logger.debug("s:>" + row.getValue("s"));
				logger.debug("v:>" + row.getValue("v"));
				logger.debug("o:>" + row.getValue("o"));
			} else
				fail();
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}

	}

	public void testKCFQuery() {
		String query = prefix
				+ "SELECT DISTINCT ?s ?name ?AccessionNumber ?Seq\n"
				+ "from <http://rdf.glytoucan.org/core>\n"
				+ "from <http://glytoucan.org/rdf/demo/0.2/kcf>\n"
				+ "from <http://glytoucan.org/rdf/demo/msdb/7>\n"
				+ "from <http://purl.jp/bio/12/glyco/glycan/ontology/0.18>\n"
				+ "from <http://www.glytoucan.org/glyco/owl/glytoucan>\n"
				+ " WHERE { ?s a glycan:glycan_motif .\n"
				+ "?s foaf:name ?name .\n"
				+ "        ?s glytoucan:has_primary_id ?AccessionNumber .\n"
				+ "       ?s glycan:has_glycosequence ?gseq .\n"
				+ "        ?gseq glycan:has_sequence ?Seq .\n"
				+ "        ?gseq glycan:in_carbohydrate_format glycan:carbohydrate_format_kcf }\n"
				+ "order by ?AccessionNumber LIMIT 10";
		try {
			List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
			SparqlEntity row = list.get(0);

			logger.debug("Node:>" + row.getValue("s"));
			logger.debug("graph:>" + row.getValue("name"));
			logger.debug("Seq:>" + row.getValue("Seq"));
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while querying schema.", true);
		}
	}

	@Test
	@Transactional
	public void testDelete() throws SparqlException {
		schemaDAO
				.delete(new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> "
						+ "DELETE DATA { graph <http://bluetree.jp/nobutest> {"
						+ "<http://bluetree.jp/nobutest/aa> <http://bluetree.jp/nobutest/bb> \"cc\" . } }"));
		
//		sparql PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
//			PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>
//			PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>
//			#{ GRAPH <http://rdf.glytoucan.org/sequence/glycoct>
//			DELETE DATA {
//			graph <http://rdf.glytoucan.org/sequence/glycoct> {
//			<http://rdf.glycoinfo.org/glycan/G92195EH> glycan:has_glycosequence <http://rdf.glycoinfo.org/glycan/G92195EH/glycoct> .
//			<http://rdf.glycoinfo.org/glycan/G92195EH/glycoct> glycan:has_sequence "RES\n1b:x-dglc-HEX-1:5\n2b:b-dgal-HEX-1:5\n3b:b-dglc-HEX-1:5\n4s:n-acetyl\n5b:b-dgal-HEX-1:5\n6b:a-lgal-HEX-1:5|6:d\n7b:a-lgal-HEX-1:5|6:d\n8b:b-dglc-HEX-1:5\n9s:n-acetyl\n10b:b-dgal-HEX-1:5\n11b:b-dglc-HEX-1:5\n12s:n-acetyl\n13b:b-dgal-HEX-1:5\nLIN\n1:1o(4+1)2d\n2:2o(3+1)3d\n3:3d(2+1)4n\n4:3o(3+1)5d\n5:5o(2+1)6d\n6:3o(4+1)7d\n7:2o(6+1)8d\n8:8d(2+1)9n\n9:8o(4+1)10d\n10:10o(3+1)11d\n11:11d(2+1)12n\n12:11o(3+1)13d\n"^^xsd:string .
//			<http://rdf.glycoinfo.org/glycan/G92195EH/glycoct> rdfs:label "RES\n1b:x-dglc-HEX-1:5\n2b:b-dgal-HEX-1:5\n3b:b-dglc-HEX-1:5\n4s:n-acetyl\n5b:b-dgal-HEX-1:5\n6b:a-lgal-HEX-1:5|6:d\n7b:a-lgal-HEX-1:5|6:d\n8b:b-dglc-HEX-1:5\n9s:n-acetyl\n10b:b-dgal-HEX-1:5\n11b:b-dglc-HEX-1:5\n12s:n-acetyl\n13b:b-dgal-HEX-1:5\nLIN\n1:1o(4+1)2d\n2:2o(3+1)3d\n3:3d(2+1)4n\n4:3o(3+1)5d\n5:5o(2+1)6d\n6:3o(4+1)7d\n7:2o(6+1)8d\n8:8d(2+1)9n\n9:8o(4+1)10d\n10:10o(3+1)11d\n11:11d(2+1)12n\n12:11o(3+1)13d\n"^^xsd:string .
//			<http://rdf.glycoinfo.org/glycan/G92195EH/glycoct> glycan:in_carbohydrate_format glycan:carbohydrate_format_glycoct .
//			}
//			}

	}
	
	

	@Test
	@Transactional
	public void testDelete2() throws SparqlException {
	  schemaDAO.insert(new InsertSparqlBean("insert data { graph <http://bluetree.jp/nobutest> { <1> <2> \"3\" . \n } }"));
	  String query = prefix + "SELECT ?s ?v ?o\n" + "from <http://bluetree.jp/nobutest>\n"
      + "WHERE { <1> ?v ?o } limit 10";

  try {
    logger.debug("query:>" + query);
    List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
    if (list.size() > 0) {
      SparqlEntity row = list.get(0);
      logger.debug("s:>" + row.getValue("s"));
      logger.debug("v:>" + row.getValue("v"));
      logger.debug("o:>" + row.getValue("o"));
    } else
      fail();
  } catch (Exception e) {
    e.printStackTrace();
    assertFalse("Exception occurred while querying schema.", true);
  }

  
	    schemaDAO
	        .delete(new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> "
	            + "DELETE DATA { graph <http://bluetree.jp/nobutest> {"
	            + "<1> <2> \"3\" . } }"));
	    
	    try {
	      logger.debug("query:>" + query);
	      List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
	      if (list.size() > 0) {
	        SparqlEntity row = list.get(0);
	        logger.debug("s:>" + row.getValue("s"));
	        logger.debug("v:>" + row.getValue("v"));
	        logger.debug("o:>" + row.getValue("o"));
	        fail();
	      } else
	        return;
	    } catch (Exception e) {
	      e.printStackTrace();
	      assertFalse("Exception occurred while querying schema.", true);
	    }

	  }
	
	 @Test
	  @Transactional
	  public void testDelete3() throws SparqlException {
	    schemaDAO.insert(new InsertSparqlBean("insert data { graph <http://bluetree.jp/nobutest> { <1> <2> \"3\" . \n } }"));
	    String query = prefix + "SELECT ?v ?o\n" + "from <http://bluetree.jp/nobutest>\n"
	      + "WHERE { <1> ?v ?o } limit 10";

	  try {
	    logger.debug("query:>" + query);
	    List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
	    if (list.size() > 0) {
	      SparqlEntity row = list.get(0);
//	      logger.debug("s:>" + row.getValue("s"));
	      logger.debug("v:>" + row.getValue("v"));
	      logger.debug("o:>" + row.getValue("o"));
	    } else
	      fail();
	  } catch (Exception e) {
	    e.printStackTrace();
	    assertFalse("Exception occurred while querying schema.", true);
	  }

	  
	      schemaDAO
	          .delete(new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> "
	              + "DELETE WHERE { graph <http://bluetree.jp/nobutest> {"
	              + "<1> ?v ?o . } }"));
	      
	      try {
	        logger.debug("query:>" + query);
	        List<SparqlEntity> list = schemaDAO.query(new SelectSparqlBean(query));
	        if (list.size() > 0) {
	          SparqlEntity row = list.get(0);
//	          logger.debug("s:>" + row.getValue("s"));
	          logger.debug("v:>" + row.getValue("v"));
	          logger.debug("o:>" + row.getValue("o"));
	          fail();
	        } else
	          return;
	      } catch (Exception e) {
	        e.printStackTrace();
	        assertFalse("Exception occurred while querying schema.", true);
	      }

	    }
	
	
//	@Test
	@Transactional
	public void testDeleteG88203PI() throws SparqlException {
	  
//	  image for G88203PI sequence:>WURCS=2.0/2,4,3/[a2112h-1x_1-5_2*NCC/3=O][a2122h-1x_1-5_2*NCC/3=O]/1-1-2-2/a?-b1_b?-c1_c?-d1<
	  
	  String G00029MOGlycoCTToDelete="RES\n" + 
	      "1b:x-dgal-HEX-1:5\n" + 
	      "2b:x-dgal-HEX-1:5\n" + 
	      "3b:x-dman-HEX-1:5\n" + 
	      "4b:x-dman-HEX-1:5\n" + 
	      "5b:x-dglc-HEX-1:5\n" + 
	      "6s:n-acetyl\n" + 
	      "7s:n-acetyl\n" + 
	      "LIN\n" + 
	      "1:1o(-1+1)2d\n" + 
	      "2:2o(-1+1)3d\n" + 
	      "3:3o(-1+1)4d\n" + 
	      "4:4o(-1+1)5d\n" + 
	      "5:2d(2+1)6n\n" + 
	      "6:1d(2+1)7n";
	  
	  String G00029MOGlycoCTToKeep = "RES\n" + 
	      "1b:x-dgal-HEX-1:5\n" + 
	      "2b:x-dgal-HEX-1:5\n" + 
	      "3b:x-dglc-HEX-1:5\n" + 
	      "4b:x-dglc-HEX-1:5\n" + 
	      "5s:n-acetyl\n" + 
	      "6s:n-acetyl\n" + 
	      "7s:n-acetyl\n" + 
	      "8s:n-acetyl\n" + 
	      "LIN\n" + 
	      "1:1o(-1+1)2d\n" + 
	      "2:2o(-1+1)3d\n" + 
	      "3:3o(-1+1)4d\n" + 
	      "4:4d(2+1)5n\n" + 
	      "5:3d(2+1)6n\n" + 
	      "6:2d(2+1)7n\n" + 
	      "7:1d(2+1)8n";
	  
	  String G00029MOGlycoCTToKeepFormatted = G00029MOGlycoCTToKeep.replaceAll("(?:\\r\\n|\\n)", "\\\\n");
	  
    String G00029MOGlycoCTToDeleteFormatted = G00029MOGlycoCTToDelete.replaceAll("(?:\\r\\n|\\n)", "\\\\n");

    String G00029MOWurcsToKeep="WURCS=2.0/2,4,3/[a2112h-1x_1-5_2*NCC/3=O][a2122h-1x_1-5_2*NCC/3=O]/1-1-2-2/a?-b1_b?-c1_c?-d1";
    
    String G00029MOWurcsToDelete="WURCS=2.0/3,5,4/[a2112h-1x_1-5_2*NCC/3=O][a1122h-1x_1-5][a2122h-1x_1-5]/1-1-2-2-3/a?-b1_b?-c1_c?-d1_d?-e1";
	  
	  GlycoSequenceSelectSparql gsss = new GlycoSequenceSelectSparql();
	  gsss.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/glycoct>");
	  
	  SparqlEntity se = new SparqlEntity();
	  se.setValue(Saccharide.PrimaryId, "G88203PI");
	  se.setValue(GlycoSequence.Format, GlyConvert.GLYCOCT);
	  
	  gsss.setSparqlEntity(se);

	  List<SparqlEntity> results = schemaDAO.query(gsss);
	  
	  Assert.assertNotNull(results);
    Assert.assertEquals(2, results.size());
	  SparqlEntity first = results.iterator().next();

	  String queriedResultSequence = first.getValue("Sequence");
	  
//    Assert.assertEquals(G00029MOGlycoCTToDelete, queriedResultSequence);
//    String queriedResultSequenceFormatted = queriedResultSequence.replaceAll("(?:\\r\\n|\\n)", "\\\\n");
//    Assert.assertEquals(G00029MOGlycoCTToDeleteFormatted, queriedResultSequenceFormatted);
	  
    SparqlBean simpleQuery = new SelectSparqlBean("select * \n" + 
        "FROM <http://rdf.glytoucan.org/sequence/glycoct> "
        + "WHERE  {"
        + "?glycosequence ?v \"" + G00029MOGlycoCTToDeleteFormatted + "\"^^<http://www.w3.org/2001/XMLSchema#string> . "
        + "} ");
    results = schemaDAO.query(simpleQuery);
    Assert.assertNotNull(results);
    Assert.assertEquals(2, results.size());
    
    DeleteSparqlBean dsb = new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n" + 
        "DELETE WHERE { "
        + "GRAPH <http://rdf.glytoucan.org/sequence/glycoct> {"
            + "?glycosequence ?v \"" + G00029MOGlycoCTToDeleteFormatted + "\"^^<http://www.w3.org/2001/XMLSchema#string> . "
            + "}"
            + "}"
        );

    schemaDAO.delete(dsb);
	  
	    results = schemaDAO.query(simpleQuery);
	    Assert.assertNotNull(results);
	    Assert.assertEquals(0, results.size());
	  
	  results = schemaDAO.query(gsss);
	  Assert.assertNotNull(results);
	  Assert.assertEquals(1, results.size());
	  
    se.setValue(GlycoSequence.AccessionNumber, "G88203PI");
    se.setValue(GlycoSequence.Format, GlyConvert.WURCS);
    
    gsss.setSparqlEntity(se);
    gsss.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>");
    
      results = schemaDAO.query(gsss);
      Assert.assertNotNull(results);
      Assert.assertEquals(2, results.size());  

	    SparqlBean simpleQueryWurcs = new SelectSparqlBean("select * \n" + 
	        "FROM <http://rdf.glytoucan.org/sequence/wurcs> "
	        + "WHERE  {"
	        + "?glycosequence ?v \"" + G00029MOWurcsToDelete + "\"^^<http://www.w3.org/2001/XMLSchema#string> . "
	        + "}");

	    results = schemaDAO.query(simpleQueryWurcs);
	    Assert.assertNotNull(results);
	    Assert.assertEquals(2, results.size()); 
	    
	    dsb = new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n" + 
	        "DELETE WHERE { graph <http://rdf.glytoucan.org/sequence/wurcs> {\n" + 
          "?glycoseq ?v \"" + G00029MOWurcsToDelete + "\"^^<http://www.w3.org/2001/XMLSchema#string> . " +
	        "} }");
	    
	    schemaDAO.delete(dsb);
	    
	    results = schemaDAO.query(simpleQueryWurcs);
	    Assert.assertNotNull(results);
      Assert.assertEquals(0, results.size());
      
      results = schemaDAO.query(gsss);
      Assert.assertNotNull(results);
      Assert.assertEquals(1, results.size());  // issue with /2.0 still exists, plus label
      
	}
	

//  @Test
  @Transactional
  public void testDeleteG00029MO() throws SparqlException {
    
//    [31mapi_1            |[0m 2016-07-12 22:09:14.104 DEBUG 1 --- [p-nio-80-exec-8] o.g.r.dao.SparqlDAOVirtSesameImpl        :   Sequence="WURCS=2.0/4,7,6/[a2122h-1x_1-5_2*NCC/3=O][a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5]/1-2-3-4-2-4-4/a4-b1_b4-c1_c3-d1_c6-f1_e1-d2|d4_g1-f3|f6"^^<http://www.w3.org/2001/XMLSchema#string>
//      [31mapi_1            |[0m 2016-07-12 22:09:14.104 DEBUG 1 --- [p-nio-80-exec-8] o.g.r.dao.SparqlDAOVirtSesameImpl        : RESULT 2: 
//      [31mapi_1            |[0m 2016-07-12 22:09:14.104 DEBUG 1 --- [p-nio-80-exec-8] o.g.r.dao.SparqlDAOVirtSesameImpl        :  SaccharideURI=http://rdf.glycoinfo.org/glycan/G00029MO
//      [31mapi_1            |[0m 2016-07-12 22:09:14.104 DEBUG 1 --- [p-nio-80-exec-8] o.g.r.dao.SparqlDAOVirtSesameImpl        :  PrimaryId="G00029MO"^^<http://www.w3.org/2001/XMLSchema#string>
//      [31mapi_1            |[0m 2016-07-12 22:09:14.104 DEBUG 1 --- [p-nio-80-exec-8] o.g.r.dao.SparqlDAOVirtSesameImpl        :  Sequence="WURCS=2.0/4,6,5/[u2122h_2*NCC/3=O][a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5]/1-2-3-4-2-4/a4-b1_b4-c1_c3-d1_c6-f1_e1-d2|d4"^^<http://www.w3.org/2001/XMLSchema#string>
//      [31mapi_1            |[0m 2016-07-12 22:09:14.105 DEBUG 1 --- [p-nio-80-exec-8] o.g.api.controller.GlycanController      : image for G00029MO sequence:>WURCS=2.0/4,7,6/[a2122h-1x_1-5_2*NCC/3=O][a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5]/1-2-3-4-2-4-4/a4-b1_b4-c1_c3-d1_c6-f1_e1-d2|d4_g1-f3|f6<
    
    String G00029MOGlycoCTToDelete="RES\n" + 
        "1b:x-dglc-HEX-x:x\n" + 
        "2s:n-acetyl\n" + 
        "3b:b-dglc-HEX-1:5\n" + 
        "4s:n-acetyl\n" + 
        "5b:b-dman-HEX-1:5\n" + 
        "6b:a-dman-HEX-1:5\n" + 
        "7b:b-dglc-HEX-1:5\n" + 
        "8s:n-acetyl\n" + 
        "9b:a-dman-HEX-1:5\n" + 
        "LIN\n" + 
        "1:1d(2+1)2n\n" + 
        "2:1o(4+1)3d\n" + 
        "3:3d(2+1)4n\n" + 
        "4:3o(4+1)5d\n" + 
        "5:5o(3+1)6d\n" + 
        "6:6o(2|4+1)7d\n" + 
        "7:7d(2+1)8n\n" + 
        "8:5o(6+1)9d";
    
    String G00029MOGlycoCTToKeep = "RES\n" + 
        "1b:x-dglc-HEX-1:5\n" + 
        "2s:n-acetyl\n" + 
        "3b:b-dglc-HEX-1:5\n" + 
        "4s:n-acetyl\n" + 
        "5b:b-dman-HEX-1:5\n" + 
        "6b:a-dman-HEX-1:5\n" + 
        "7b:b-dglc-HEX-1:5\n" + 
        "8s:n-acetyl\n" + 
        "9b:a-dman-HEX-1:5\n" + 
        "10b:a-dman-HEX-1:5\n" + 
        "LIN\n" + 
        "1:1d(2+1)2n\n" + 
        "2:1o(4+1)3d\n" + 
        "3:3d(2+1)4n\n" + 
        "4:3o(4+1)5d\n" + 
        "5:5o(3+1)6d\n" + 
        "6:6o(2|4+1)7d\n" + 
        "7:7d(2+1)8n\n" + 
        "8:5o(6+1)9d\n" + 
        "9:9o(3|6+1)10d";
    
    String G00029MOGlycoCTToKeepFormatted = G00029MOGlycoCTToKeep.replaceAll("(?:\\r\\n|\\n)", "\\\\n");
    
    String G00029MOGlycoCTToDeleteFormatted = G00029MOGlycoCTToDelete.replaceAll("(?:\\r\\n|\\n)", "\\\\n");

    String G00029MOWurcsToKeep="WURCS=2.0/4,7,6/[a2122h-1x_1-5_2*NCC/3=O][a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5]/1-2-3-4-2-4-4/a4-b1_b4-c1_c3-d1_c6-f1_e1-d2|d4_g1-f3|f6";
    
    String G00029MOWurcsToDelete="WURCS=2.0/4,6,5/[u2122h_2*NCC/3=O][a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5]/1-2-3-4-2-4/a4-b1_b4-c1_c3-d1_c6-f1_e1-d2|d4";
    
    GlycoSequenceSelectSparql gsss = new GlycoSequenceSelectSparql();
    gsss.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/glycoct>");
    
    SparqlEntity se = new SparqlEntity();
    se.setValue(Saccharide.PrimaryId, "G00029MO");
    se.setValue(GlycoSequence.Format, GlyConvert.GLYCOCT);
    
    gsss.setSparqlEntity(se);

    List<SparqlEntity> results = schemaDAO.query(gsss);
    
    Assert.assertNotNull(results);
    Assert.assertEquals(2, results.size());
    SparqlEntity first = results.iterator().next();

    String queriedResultSequence = first.getValue("Sequence");
    
    Assert.assertEquals(G00029MOGlycoCTToDelete, queriedResultSequence);
    String queriedResultSequenceFormatted = queriedResultSequence.replaceAll("(?:\\r\\n|\\n)", "\\\\n");
    Assert.assertEquals(G00029MOGlycoCTToDeleteFormatted, queriedResultSequenceFormatted);
    
    SparqlBean simpleQuery = new SelectSparqlBean("select * \n" + 
        "FROM <http://rdf.glytoucan.org/seglycan:has_sequence quence/glycoct> "
        + "WHERE  {"
        + "?glycosequence ?v \"" + G00029MOGlycoCTToDeleteFormatted + "\"^^<http://www.w3.org/2001/XMLSchema#string> . "
        + "} ");
    results = schemaDAO.query(simpleQuery);
    Assert.assertNotNull(results);
    Assert.assertEquals(2, results.size());
    
    DeleteSparqlBean dsb = new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n" + 
        "DELETE WHERE { "
        + "GRAPH <http://rdf.glytoucan.org/sequence/glycoct> {"
            + "?glycosequence ?v \"" + G00029MOGlycoCTToDeleteFormatted + "\"^^<http://www.w3.org/2001/XMLSchema#string> . "
            + "}"
            + "}"
        );

    schemaDAO.delete(dsb);
    
      results = schemaDAO.query(simpleQuery);
      Assert.assertNotNull(results);
      Assert.assertEquals(0, results.size());
    
    results = schemaDAO.query(gsss);
    Assert.assertNotNull(results);
    Assert.assertEquals(1, results.size());
    
    se.setValue(GlycoSequence.AccessionNumber, "G00029MO");
    se.setValue(GlycoSequence.Format, GlyConvert.WURCS);
    
    gsss.setSparqlEntity(se);
    gsss.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>");
    
      results = schemaDAO.query(gsss);
      Assert.assertNotNull(results);
      Assert.assertEquals(4, results.size());  // issue with /2.0 still exists, plus label

      SparqlBean simpleQueryWurcs = new SelectSparqlBean("select * \n" + 
          "FROM <http://rdf.glytoucan.org/sequence/wurcs> "
          + "WHERE  {"
          + "?glycosequence ?v \"" + G00029MOWurcsToDelete + "\"^^<http://www.w3.org/2001/XMLSchema#string> . "
          + "}");

      results = schemaDAO.query(simpleQueryWurcs);
      Assert.assertNotNull(results);
      Assert.assertEquals(4, results.size()); 
      
      dsb = new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n" + 
          "DELETE WHERE { graph <http://rdf.glytoucan.org/sequence/wurcs> {\n" + 
          "?glycoseq ?v \"" + G00029MOWurcsToDelete + "\"^^<http://www.w3.org/2001/XMLSchema#string> . " +
          "} }");
      
      schemaDAO.delete(dsb);
      
      results = schemaDAO.query(simpleQueryWurcs);
      Assert.assertNotNull(results);
      Assert.assertEquals(0, results.size());
      
      results = schemaDAO.query(gsss);
      Assert.assertNotNull(results);
      Assert.assertEquals(2, results.size());  // issue with /2.0 still exists, plus label
      
  }
  
  /*
   * 
   */
  @Test
//  @Transactional
  public void testDeleteDuplicateLimitedRepeatWurcs() throws SparqlException, WURCSException {
    String select = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n" + 
        "PREFIX rogs: <http://http://www.glycoinfo.org/glyco/owl/relation#>\n" + 
        "PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>\n" + 
        " SELECT DISTINCT ?PrimaryId \n"
//        + "#count(distinct ?Sequence)  ?date ?contrib\n" 
        + " FROM <http://rdf.glytoucan.org/core>\n" + 
//        "#FROM <http://rdf.glytoucan.org/isomer>\n" + 
        "FROM <http://rdf.glytoucan.org/sequence/wurcs>\n" + 
        " WHERE {\n" + 
        "?SaccharideURI a glycan:saccharide .\n" + 
        "?SaccharideURI glytoucan:has_primary_id ?PrimaryId .\n" + 
        "?SaccharideURI glycan:has_glycosequence ?GlycanSequenceURI .\n" + 
        "?GlycanSequenceURI glycan:has_sequence ?Sequence .\n" + 
        "?GlycanSequenceURI glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .\n" +
//        "} group by ?PrimaryId order by ?PrimaryId limit 100";
        "} group by ?PrimaryId having (count(distinct ?Sequence) > 1) order by ?PrimaryId";
    
    SelectSparql sparql = new SelectSparqlBean(select);

    List<SparqlEntity> list = schemaDAO.query(sparql);
    
    for (SparqlEntity sparqlEntity : list) {
      String primaryId = sparqlEntity.getValue("PrimaryId");
      logger.debug("primaryId->"+primaryId + "<");
      
      GlycoSequenceSelectSparql gsss = new GlycoSequenceSelectSparql();
      sparqlEntity.setValue(GlycoSequence.Format, "wurcs");
      gsss.setSparqlEntity(sparqlEntity);
      List<SparqlEntity> wurcsList = schemaDAO.query(gsss);
      
      for (SparqlEntity sparqlEntity2 : wurcsList) {
        String sequence = sparqlEntity2.getValue(GlycoSequence.Sequence);
        logger.debug("sequence:\t" + sequence + "<");
        
        WURCSFactory wurcsFactory = new WURCSFactory(sequence);
        String wurcs = wurcsFactory.getWURCS();
        logger.debug("wurcs\t:" + wurcs + "<");
        
        if (!wurcs.equals(sequence)) {
          logger.debug(">" + wurcs + "<!=");
          logger.debug(">" + sequence + "<");          
          schemaDAO.delete(new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> DELETE DATA { GRAPH <http://rdf.glytoucan.org/sequence/wurcs> { <http://rdf.glycoinfo.org/glycan/" + primaryId + "/wurcs/2.0> glycan:has_sequence \"" + sequence + "\"^^<http://www.w3.org/2001/XMLSchema#string> } }"));
          schemaDAO.delete(new DeleteSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> DELETE DATA { GRAPH <http://rdf.glytoucan.org/sequence/wurcs> { <http://rdf.glycoinfo.org/glycan/" + primaryId + "/wurcs> glycan:has_sequence \"" + sequence + "\"^^<http://www.w3.org/2001/XMLSchema#string> } }"));
          List<SparqlEntity> checkList = schemaDAO.query(new SelectSparqlBean("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#> Select count(*) as ?count FROM <http://rdf.glytoucan.org/sequence/wurcs> WHERE { <http://rdf.glycoinfo.org/glycan/" + primaryId + "/wurcs> glycan:has_sequence \"" + sequence + "\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" +
              "<http://rdf.glycoinfo.org/glycan/" + primaryId + "/wurcs/2.0> glycan:has_sequence \"" + sequence + "\"^^<http://www.w3.org/2001/XMLSchema#string> .\n}"));
          String count = checkList.iterator().next().getValue("count");
          Assert.assertEquals("0", count);
        }
      }
    }
  }
	
//  private Model createDefaultModel(){
//    Model model = ModelFactory.createDefaultModel();
//    model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
//    model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
//    model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
//    model.setNsPrefix("bibo", "http://purl.org/ontology/bibo/");
//    model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
//    model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
//    model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//    model.setNsPrefix("glycan", "http://purl.jp/bio/12/glyco/glycan#");
//    model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
//    model.setNsPrefix("glytoucan", "http://www.glytoucan.org/glyco/owl/glytoucan#");
//    
////    model.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
////    model.setNsPrefix("edam", "http://edamontology.org/");
////    model.setNsPrefix("faldo", "http://biohackathon.org/resource/faldo#");
////    model.setNsPrefix("obo", "http://purl.obolibrary.org/obo/");
////    model.setNsPrefix("uniprot", "http://purl.uniprot.org/core/");
////    model.setNsPrefix("glyco", "http://purl.jp/bio/12/glyco/glycan#");
//    return model;
//  }

  @Test
	@Transactional
	public void testDeleteBean() throws SparqlException {
		DeleteSparql ds = new DeleteSparqlBean();
		
		ds.setPrefix(prefix);
		ds.setDelete("<http://bluetree.jp/nobutest/aa> <http://bluetree.jp/nobutest/bb> \"cc\"");
		ds.setGraph("http://bluetree.jp/nobutest");
		schemaDAO.delete(ds);
	}

//	@Test
	@Transactional
	public void testClearGraph() throws SparqlException {
		schemaDAO
				.execute(new InsertSparqlBean("clear graph <http://bluetree.jp/nobutest>"));
	}
	
	public void testInsertWurcs() throws SparqlException {
		schemaDAO
				.insert(new InsertSparqlBean("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
						+ "		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
						+ "		PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>"
						+ "		PREFIX glytoucan:  <http://www.glytoucan.org/glyco/owl/glytoucan#>"
						+ "		INSERT INTO"
						+ "		GRAPH <http://glytoucan.org/rdf/demo/0.3/wurcs>"
						+ "		{ <http://www.glycoinfo.org/rdf/glycan/G36373OJ> glycan:has_glycosequence <http://www.glycoinfo.org/rdf/glycan/G36373OJ/seq> ."
						+ "		<http://www.glycoinfo.org/rdf/glycan/G36373OJ/seq> glycan:has_sequence \"WURCS=2.0/5,4/[12211m-1a_1-5][12211m-1a_1-5][12211m-1a_1-5][12112m-1b_1-5][12122h-1b_1-5]a2-b1_b4-c1_c3-d1_c4-e1\"^^xsd:string ."
								+ "		<http://www.glycoinfo.org/rdf/glycan/G36373OJ/seq> glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs ."
								+ "		<http://www.glycoinfo.org/rdf/glycan/G36373OJ/seq> glytoucan:is_glycosequence_of <http://www.glycoinfo.org/rdf/glycan/G36373OJ> ."
								+ "		 }"));
	}
}