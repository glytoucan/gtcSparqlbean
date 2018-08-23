package org.glycoinfo.rdf.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.atlas.logging.Log;
import org.glycoinfo.convert.GlyConvertConfig;
import org.glycoinfo.convert.error.ConvertException;
import org.glycoinfo.rdf.DuplicateException;
import org.glycoinfo.rdf.InsertSparql;
import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.virt.VirtSesameTransactionConfig;
import org.glycoinfo.rdf.glycan.ContributorInsertSparql;
import org.glycoinfo.rdf.glycan.ContributorNameSelectSparql;
import org.glycoinfo.rdf.glycan.DatabaseSelectSparql;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.GlycoSequenceInsertSparql;
import org.glycoinfo.rdf.glycan.ResourceEntry;
import org.glycoinfo.rdf.glycan.ResourceEntryInsertSparql;
import org.glycoinfo.rdf.glycan.ResourceEntrySelectSparql;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.glycoinfo.rdf.glycan.SaccharideInsertSparql;
import org.glycoinfo.rdf.glycan.SaccharideSelectSparql;
import org.glycoinfo.rdf.glycan.mass.MassInsertSparql;
//import org.glycoinfo.rdf.glycan.msdb.MSInsertSparql;
import org.glycoinfo.rdf.glycan.wurcs.GlycoSequenceResourceEntryContributorSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.MonosaccharideSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.MotifSequenceSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.SubstructureSearchSparql;
import org.glycoinfo.rdf.glycan.wurcs.WurcsRDFInsertSparql;
import org.glycoinfo.rdf.glycan.wurcs.WurcsRDFMSInsertSparql;
import org.glycoinfo.rdf.scint.ClassHandler;
import org.glycoinfo.rdf.scint.InsertScint;
import org.glycoinfo.rdf.scint.SelectScint;
import org.glycoinfo.rdf.service.exception.ContributorException;
import org.glycoinfo.rdf.service.exception.GlycanException;
import org.glycoinfo.rdf.service.exception.InvalidException;
import org.glycoinfo.rdf.service.impl.ContributorProcedureConfig;
import org.glycoinfo.rdf.service.impl.ContributorProcedureRdf;
import org.glycoinfo.rdf.service.impl.GlycanProcedureConfig;
import org.glycoinfo.rdf.utils.NumberGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GlycanProcedureTest.class, VirtSesameTransactionConfig.class, GlycanProcedureConfig.class, GlyConvertConfig.class, ContributorProcedureConfig.class})
//@ComponentScan(basePackages = {"org.glycoinfo.rdf.service", "org.glycoinfo.rdf.scint"})
//@ComponentScan(basePackages = {"org.glycoinfo.rdf"}, excludeFilters={
//		  @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=Configuration.class)})
@Configuration
@EnableAutoConfiguration
public class GlycanProcedureTest {
	public static Logger logger = (Logger) LoggerFactory
			.getLogger(GlycanProcedureTest.class);
	
	@Autowired
	SparqlDAO sparqlDAO;
	
	private static final String graph = "http://rdf.glytoucan.org";
	
	@Bean(name = "selectscintperson")
	SelectScint getSelectPersonScint() throws SparqlException {
		SelectScint select = new SelectScint("schema", "http://schema.org/", "Person");
		
//		select.setClassHandler(getPersonClassHandler());
		return select;
	}

	@Bean(name = "insertscintperson")
	InsertScint getInsertPersonScint() throws SparqlException {
		InsertScint insert = new InsertScint("http://rdf.glytoucan.org/users", "schema", "http://schema.org/", "Person");
//		insert.getSparqlBean().setGraph("http://rdf.glytoucan.org/users");
//		insert.setClassHandler(getSelectPersonScint());
		return insert;
	}
	
	@Bean(name = "selectscintregisteraction")
	SelectScint getSelectRegisterActionScint() throws SparqlException {
		SelectScint select = new SelectScint("schema", "http://schema.org/", "RegisterAction");
//		select.setClassHandler(getRegisterActionClassHandler());
		return select;
	}

	@Bean(name = "insertscintregisteraction")
	InsertScint getInsertRegisterActionScint() throws SparqlException {
		InsertScint insert = new InsertScint("http://rdf.glytoucan.org/users", "schema", "http://schema.org/", "RegisterAction");
//		insert.getSparqlBean().setGraph("http://rdf.glytoucan.org/users");
//		insert.setClassHandler(getRegisterActionClassHandler());
		return insert;
	}
	
//	@Bean
//	ClassHandler getPersonClassHandler() throws SparqlException {
//		ClassHandler ch = new ClassHandler("schema", "http://schema.org/", "Person");
//		ch.setSparqlDAO(sparqlDAO);
//		return ch; 
//	}
	
	ClassHandler getRegisterActionClassHandler() throws SparqlException {
		ClassHandler ch = new ClassHandler("schema", "http://schema.org/", "RegisterAction");
		ch.setSparqlDAO(sparqlDAO);
		return ch; 
	}
	
	ClassHandler getDateTimeClassHandler() throws SparqlException {
		ClassHandler ch = new ClassHandler("schema", "http://schema.org/", "DateTime");
		ch.setSparqlDAO(sparqlDAO);
		return ch; 
	}
	
	@Autowired
	GlycanProcedure glycanProcedure;

	@Autowired
	ContributorProcedure contributorProcedure;

	@Bean(name = "glycanProcedure")
	@Scope("prototype")
	GlycanProcedure getGlycanProcedure() throws SparqlException {
		GlycanProcedure glycan = new org.glycoinfo.rdf.service.impl.GlycanProcedure();
		return glycan;
	}
	
	@Bean
	SaccharideInsertSparql getSaccharideInsertSparql() {
		SaccharideInsertSparql sis = new SaccharideInsertSparql();
		sis.setGraph(graph);
		return sis;
	}
	
	@Bean(name = "contributorProcedure")
	ContributorProcedure getContributorProcedure() throws SparqlException {
		ContributorProcedure cp = new ContributorProcedureRdf();
		return cp;
	}

	@Bean
	ContributorInsertSparql getContributorInsertSparql() {
		ContributorInsertSparql c = new ContributorInsertSparql();
		c.setGraph(graph);
		return c;
	}
	
	@Bean
	ContributorNameSelectSparql getContributorNameSelectSparql() {
		ContributorNameSelectSparql selectbyNameContributor = new ContributorNameSelectSparql();
		selectbyNameContributor.setFrom("FROM <" + graph + ">");
		return selectbyNameContributor;
	}
	
	@Bean
	ResourceEntryInsertSparql getResourceEntryInsertSparql() {
		ResourceEntryInsertSparql resourceEntryInsertSparql = new ResourceEntryInsertSparql();
		SparqlEntity se = new SparqlEntity();
		se.setValue(ResourceEntryInsertSparql.Database, "glytoucan");
		resourceEntryInsertSparql.setSparqlEntity(se);
		resourceEntryInsertSparql.setGraph(graph);
		return resourceEntryInsertSparql;
	}
	
//	@Bean
//	@Scope("prototype")
//	SelectSparql glycoSequenceContributorSelectSparql() {
//		GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
//		sb.setFrom("FROM <" + graph + ">\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>");
//		return sb;
//	}
	
	@Bean
	WurcsRDFInsertSparql wurcsRDFInsertSparql() {
		WurcsRDFInsertSparql wrdf = new WurcsRDFInsertSparql();
		wrdf.setSparqlEntity(new SparqlEntity());
		wrdf.setGraph("http://rdf.glytoucan.org/sequence/wurcs");
		return wrdf;
	}
	
	@Bean
	InsertSparql glycoSequenceInsert() {
		GlycoSequenceInsertSparql gsis = new GlycoSequenceInsertSparql();
		gsis.setSparqlEntity(new SparqlEntity());
		gsis.setGraph(graph);
		return gsis;
	}

	@Bean
	MassInsertSparql massInsertSparql() {
		MassInsertSparql mass = new MassInsertSparql();
		mass.setGraphBase(graph);
		return mass;
	}
	
	@Bean
	SelectSparql listAllGlycoSequenceContributorSelectSparql() {
		GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
		sb.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>");
		return sb;
	}

	@Bean
	MotifSequenceSelectSparql motifSequenceSelectSparql() {
		MotifSequenceSelectSparql select = new MotifSequenceSelectSparql();
//		select.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>");
		return select;
	}
	
	@Bean
	SubstructureSearchSparql substructureSearchSparql() {
		SubstructureSearchSparql ssb = new SubstructureSearchSparql();
		return ssb;
	}
	
	@Bean
	WurcsRDFMSInsertSparql wurcsRDFMSInsertSparql() {
		WurcsRDFMSInsertSparql wrdf = new WurcsRDFMSInsertSparql();
		wrdf.setGraph("http://rdf.glytoucan.org/wurcs/ms");
		return wrdf;
	}
	
	@Bean
	SaccharideSelectSparql saccharideSelectSparql() {
		SaccharideSelectSparql select = new SaccharideSelectSparql();
		select.setFrom("FROM <http://rdf.glytoucan.org/core>\n");
		return select;
	}

	
//	@Bean
//	MSdbClient msdbClient() {
//		return new MSdbClient();
//	}
	
	@Bean
	MonosaccharideSelectSparql monosaccharideSelectSparql() {
		MonosaccharideSelectSparql sb = new MonosaccharideSelectSparql();
		sb.setFrom(sb.getFrom() + "FROM <http://rdf.glytoucan.org/core>\n");
		return sb;
	}
	
//	@Bean
//	public MSInsertSparql msInsertSparql() {
//		MSInsertSparql wrss = new MSInsertSparql();
//		wrss.setGraph("http://rdf.glytoucan.org/msdb");
//		return wrss;
//	}
	
	@Autowired
	DatabaseSelectSparql databaseSelectSparql;
	
//	@Test(expected=SparqlException.class)
//	public void testInsufficientUser() throws SparqlException {
//		SparqlEntity se = new SparqlEntity();
//		se.setValue("id", "person456");
//		userProcedure.setSparqlEntity(se);
//		userProcedure.addUser();
//	}
	
	/*
	 * RES
1b:b-dglc-HEX-1:5
2s:n-acetyl
3b:b-dgal-HEX-1:5
LIN
1:1d(2+1)2n
2:1o(4+1)3d
	 */
	@Test
  @Transactional
	public void testSearch() throws SparqlException, ConvertException {
		String sequence = "RES\n" +
				"1b:b-dglc-HEX-1:5\n" +
				"2s:n-acetyl\n"
				+ "3b:b-dgal-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n\n"
				+ "2:1o(4+1)3d";
		
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);
		
		logger.debug(se.getValue(GlycoSequence.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.Image));
		logger.debug(se.getValue(GlycoSequence.Sequence));
		Assert.assertNotNull(se.getValue(GlycoSequence.AccessionNumber));
		Assert.assertNotNull(se.getValue(GlycanProcedure.Image));
		Assert.assertNotNull(se.getValue(GlycoSequence.Sequence));
		Assert.assertEquals("G00055MO", se.getValue(GlycoSequence.AccessionNumber));
	}
	
	@Test
  @Transactional
	public void testSearchG00031MO() throws SparqlException, ConvertException {
		// ne
//		data-wurcs="WURCS=2.0/2,2,1/[a2112h-1a_1-5_2*NCC/3=O][a2112h-1b_1-5]/1-2/a3-b1" >
	
//test
//		data-wurcs="WURCS=2.0/2,2,1/[22112h-1a_1-5_2*NCC/3=O][12112h-1b_1-5]/1-2/a3-b1">
//		data-wurcs="WURCS=2.0/2,2,1/[22112h-1a_1-5_2*NCC/3=O][12112h-1b_1-5]/1-2/a3-b1">
		String sequence = "RES\n"
				+ "1b:a-dgal-HEX-1:5\n"
				+ "2s:n-acetyl\n"
				+ "3b:b-dgal-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n\n"
				+ "2:1o(3+1)3d";
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);

		logger.debug(se.getValue(GlycoSequence.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.Image));
		logger.debug(se.getValue(GlycoSequence.Sequence));
		Assert.assertNotNull(se.getValue(GlycoSequence.AccessionNumber));
		Assert.assertNotNull(se.getValue(GlycanProcedure.Image));
		Assert.assertNotNull(se.getValue(GlycoSequence.Sequence));
		Assert.assertEquals("G00031MO", se.getValue(GlycoSequence.AccessionNumber));
	}

	
	@Test
	public void testHash() {
		String sequence="RES\n"
				+ "1b:x-dglc-HEX-1:5|1:a\n"
				+ "2b:b-dgal-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1o(4+1)2d";
		String hashtext = DigestUtils.md5Hex(sequence);
		Assert.assertEquals("e06b141de8d13adfa0c3ad180b9eae06", hashtext);
		hashtext = DigestUtils.md5Hex("WURCS=2.0/4,4,3/[u2122h][a2112h-1b_1-5][a2112h-1a_1-5][a2112h-1b_1-5_2*NCC/3=O]/1-2-3-4/a4-b1_b3-c1_c3-d1");
		Assert.assertEquals("497ea4c9a0680f9aa7d6541dca211967", hashtext);
		logger.debug(hashtext);

		//		WURCS=2.0/4,4,3/[u2122h_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5_2*OSO/3=O/3=O][a2122h-1b_1-5_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5]/1-2-3-4/a4-b1_b4-c1_c4-d1
		hashtext = DigestUtils.md5Hex("WURCS=2.0/4,4,3/[u2122h_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5_2*OSO/3=O/3=O][a2122h-1b_1-5_2*NCC/3=O_6*OSO/3=O/3=O_?*OSO/3=O/3=O][a1212A-1a_1-5]/1-2-3-4/a4-b1_b4-c1_c4-d1");
		logger.debug(hashtext);
		Assert.assertEquals("331ebfcfc29a997790a7a4f1671a9882", hashtext);
	}
	
	@Test(expected=SparqlException.class)
  @Transactional
	public void testRegisterNew() throws SparqlException, NoSuchAlgorithmException, ConvertException {
		
		String sequence="WURCS=2.0/4,4,3/[u2122h][a2112h-1b_1-5][a2112h-1a_1-5][a2112h-1b_1-5_2*NCC/3=O]/1-2-3-4/a4-b1_b3-c1_c3-d1";

//		glycanProcedure.setSequence(sequence);
//		glycanProcedure.setContributorId("testname");
		String se = glycanProcedure.register(sequence, "testname");

		/*
		PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>
			PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>
			SELECT distinct ?glycoseq ?accessionNo ?sequence 
			#?WURCS_label  
			?res
			#?Contributor
			WHERE {
			       ?s a glycan:saccharide .
			       ?s glytoucan:has_primary_id ?accessionNo .
			       ?s glytoucan:has_primary_id "G03828HN" .
			       ?s glycan:has_glycosequence ?glycoseq .
			       ?glycoseq glycan:has_sequence ?sequence .
			#       ?glycoseq rdfs:label ?WURCS_label .
			       ?glycoseq glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .
			        ?s glycan:has_resource_entry ?res .
			#        ?res a glycan:resource_entry ;
			#        glytoucan:date_registered ?ContributionTime ;
			#         glytoucan:contributor ?c .
			#        ?c foaf:name ?Contributor .
			}*/
		logger.debug(se);
		String query = "PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n"
				+ "SELECT distinct ?glycoseq ?accessionNo ?sequence\n"
				+ "?WURCS_label\n"
				+ "?res\n"
				+ "?Contributor\n"
				+ "WHERE {\n"
				+ "?s a glycan:saccharide .\n"
				+ "?s glytoucan:has_primary_id ?accessionNo .\n"
				+ "?s glytoucan:has_primary_id \"" + se + "\" .\n"
				+ "?s glycan:has_glycosequence ?glycoseq .\n"
				+ "?glycoseq glycan:has_sequence ?sequence .\n"
				+ "?glycoseq rdfs:label ?WURCS_label .\n"
				+ "?glycoseq glycan:in_carbohydrate_format glycan:carbohydrate_format_wurcs .\n"
				+ "?s glycan:has_resource_entry ?res .\n"
				+ "?res a glycan:resource_entry ;\n"
				+ "glytoucan:date_registered ?ContributionTime ;\n"
				+ "glytoucan:contributor ?c .\n"
				+ "?c foaf:name ?Contributor .}";

		List<SparqlEntity> results = sparqlDAO.query(new SelectSparqlBean(query));
		Assert.assertTrue(results.size()>0);
		SparqlEntity first = results.iterator().next();
		logger.debug(first.getValue("glycoseq"));
		logger.debug(first.getValue("accessionNo"));
		logger.debug(first.getValue("Contributor"));
//		Assert.assertNotNull(se);
	}
	
	@Test
  @Transactional
	public void testSequenceScope() throws SparqlException, NoSuchAlgorithmException {
		SparqlEntity se = glycanProcedure.searchByAccessionNumber("G00026MO");
		logger.debug(se.getValue("Mass"));
		se = glycanProcedure.searchByAccessionNumber("G00031MO");
		logger.debug(se.getValue("Mass"));
//		Assert.assertNotNull(se);
	}
	
	@Test
	@Transactional
	public void testRegisterNew3() throws SparqlException, NoSuchAlgorithmException, ConvertException {
		String sequence = "RES\n"
				+ "1b:x-dglc-HEX-1:5\n"
				+ "2b:x-dglc-HEX-1:5\n"
				+ "3b:x-dglc-HEX-1:5\n"
				+ "4s:n-acetyl\n"
				+ "5s:n-acetyl\n"
				+ "LIN\n"
				+ "1:1o(-1+1)2d\n"
				+ "2:2o(-1+1)3d\n"
				+ "3:3d(2+1)4n\n"
				+ "4:1d(2+1)5n\n";
//		String sequence = "RES\n"
//				+ "1b:b-dglc-HEX-1:5\n"
//				+ "2s:n-acetyl\n"
//				+ "3b:b-dgal-HEX-1:5\n"
//				+ "LIN\n"
//				+ "1:1d(2+1)2n\n"
//				+ "2:1o(4+1)3d";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setSequence(sequence);
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);

		logger.debug(se.getValue(GlycoSequence.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.ResultSequence));
		Assert.assertNotNull(se.getValue(GlycoSequence.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.FromSequence));

		String wurcs = se.getValue(GlycanProcedure.ResultSequence);
		
		logger.debug("wurcs:>" + wurcs + "<");
//		glycanProcedure.setContributorId("test");
		
//		glycanProcedure.setSequence(sequence);
		String id = glycanProcedure.register(sequence, "14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");
		logger.debug("searching with id:>" + id + "<");
		se = glycanProcedure.searchByAccessionNumber(id);
		
		Assert.assertNotNull(se.getValue("Mass"));

		logger.debug(se.toString());
	}
	
//	@Test
//	@Transactional
	public void testRegisterG98132BH() throws SparqlException, NoSuchAlgorithmException, ConvertException {
		String sequence = "RES\\n"
				+ "1b:x-dglc-HEX-1:5\\n"
				+ "2s:n-acetyl\\n"
				+ "3b:b-dglc-HEX-1:5\\n"
				+ "4s:n-acetyl\\n"
				+ "5b:b-dman-HEX-1:5\\n"
				+ "6b:a-dman-HEX-1:5\\n"
				+ "7b:b-dglc-HEX-1:5\\n"
				+ "8s:n-acetyl\\n"
				+ "9b:b-dgal-HEX-1:5\\n"
				+ "10b:b-dglc-HEX-1:5\\n"
				+ "11s:n-acetyl\\n"
				+ "12b:b-dgal-HEX-1:5\\n"
				+ "13b:a-dman-HEX-1:5\\n"
				+ "14b:b-dglc-HEX-1:5\\n"
				+ "15s:n-acetyl\\n"
				+ "16b:a-lgal-HEX-1:5|6:d\\n"
				+ "LIN\\n"
				+ "1:1d(2+1)2n\\n"
				+ "2:1o(4+1)3d\\n"
				+ "3:3d(2+1)4n\\n"
				+ "4:3o(4+1)5d\\n"
				+ "5:5o(3+1)6d\\n"
				+ "6:6o(2+1)7d\\n"
				+ "7:7d(2+1)8n\\n"
				+ "8:7o(4+1)9d\\n"
				+ "9:6o(4+1)10d\\n"
				+ "10:10d(2+1)11n\\n"
				+ "11:10o(4+1)12d\\n"
				+ "12:5o(6+1)13d\\n"
				+ "13:13o(2|6+1)14d\\n"
				+ "14:14d(2+1)15n\\n"
				+ "15:1o(6+1)16d\\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setSequence(sequence);
		SparqlEntity se = glycanProcedure.searchBySequence(sequence);

		logger.debug(se.getValue(GlycoSequence.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.ResultSequence));
		Assert.assertNotNull(se.getValue(GlycoSequence.AccessionNumber));
		logger.debug(se.getValue(GlycanProcedure.FromSequence));

		String wurcs = se.getValue(GlycanProcedure.ResultSequence);
		
		logger.debug("wurcs:>" + wurcs + "<");
//		glycanProcedure.setContributorId("test");
		
//		glycanProcedure.setSequence(sequence);
		String id = glycanProcedure.register(sequence, "test");
		se = glycanProcedure.searchByAccessionNumber(id);
		Assert.assertNotNull(se.getValue("Mass"));
		
		logger.debug(se.toString());
	}
	
//	@Test
//	@Transactional
//	public void testRegisterG65696SL() throws SparqlException, ConvertException
//	{
////		GlycoCT:
////RES
////1b:x-dglc-HEX-1:5
////2b:b-dgal-HEX-1:5
////3b:b-dglc-HEX-1:5
////4s:n-acetyl
////5b:b-dgal-HEX-1:5
////6b:b-dglc-HEX-1:5
////7s:n-acetyl
////8b:b-dgal-HEX-1:5
////9b:b-dglc-HEX-1:5
////10s:n-acetyl
////11b:b-dgal-HEX-1:5
////12b:b-dglc-HEX-1:5
////13s:n-acetyl
////14b:b-dgal-HEX-1:5
////LIN
////1:1o(4+1)2d
////2:2o(3+1)3d
////3:3d(2+1)4n
////4:3o(4+1)5d
////5:2o(6+1)6d
////6:6d(2+1)7n
////7:6o(4+1)8d
////8:8o(3+1)9d
////9:9d(2+1)10n
////10:9o(3+1)11d
////11:8o(6+1)12d
////12:12d(2+1)13n
////13:12o(4+1)14d
//		String sequence = "RES\\n"
//				+ "1b:x-dglc-HEX-1:5\\n"
//				+ "2b:b-dgal-HEX-1:5\\n"
//				+ "3b:b-dglc-HEX-1:5\\n"
//				+ "4s:n-acetyl\\n"
//				+ "5b:b-dgal-HEX-1:5\\n"
//				+ "6b:b-dglc-HEX-1:5\\n"
//				+ "7s:n-acetyl\\n"
//				+ "8b:b-dgal-HEX-1:5\\n"
//				+ "9b:b-dglc-HEX-1:5\\n"
//				+ "10s:n-acetyl\\n"
//				+ "11b:b-dgal-HEX-1:5\\n"
//				+ "12b:b-dglc-HEX-1:5\\n"
//				+ "13s:n-acetyl\\n"
//				+ "14b:b-dgal-HEX-1:5\\n"
//				+ "LIN\\n"
//				+ "1:1o(4+1)2d\\n"
//				+ "2:2o(3+1)3d\\n"
//				+ "3:3d(2+1)4n\\n"
//				+ "4:3o(4+1)5d\\n"
//				+ "5:2o(6+1)6d\\n"
//				+ "6:6d(2+1)7n\\n"
//				+ "7:6o(4+1)8d\\n"
//				+ "8:8o(3+1)9d\\n"
//				+ "9:9d(2+1)10n\\n"
//				+ "10:9o(3+1)11d\\n"
//				+ "11:8o(6+1)12d\\n"
//				+ "12:12d(2+1)13n\\n"
//				+ "13:12o(4+1)14d\\n";
//
//		logger.debug("sequence:>" + sequence + "<");
////		glycanProcedure.setId("G65696SL");
////		glycanProcedure.setContributorId("5854");
//		String result = glycanProcedure.register(sequence, "5854");
//
//		Assert.assertNotNull(result);
//		
//	}
	
//	@Test
//	@Transactional
//	public void testRegisterG46627YI() throws SparqlException, ConvertException
//	{
////		GlycoCT:
////		RES
////		1b:x-dglc-HEX-1:5
////		2b:b-dgal-HEX-1:5
////		3b:b-dglc-HEX-1:5
////		4s:n-acetyl
////		5b:b-dgal-HEX-1:5
////		6b:b-dglc-HEX-1:5
////		7s:n-acetyl
////		8b:a-lgal-HEX-1:5|6:d
////		9b:b-dgal-HEX-1:5
////		10b:b-dglc-HEX-1:5
////		11s:n-acetyl
////		12b:b-dgal-HEX-1:5
////		13b:a-lgal-HEX-1:5|6:d
////		LIN
////		1:1o(4+1)2d
////		2:2o(3+1)3d
////		3:3d(2+1)4n
////		4:3o(3+1)5d
////		5:2o(6+1)6d
////		6:6d(2+1)7n
////		7:6o(3+1)8d
////		8:6o(4+1)9d
////		9:9o(3+1)10d
////		10:10d(2+1)11n
////		11:10o(3+1)12d
////		12:12o(2+1)13d
//		String sequence = "RES\\n" +
//				"1b:x-dglc-HEX-1:5\\n" +
//				"2b:b-dgal-HEX-1:5\\n" +
//				"3b:b-dglc-HEX-1:5\\n" +
//				"4s:n-acetyl\\n" +
//				"5b:b-dgal-HEX-1:5\\n" +
//				"6b:b-dglc-HEX-1:5\\n" +
//				"7s:n-acetyl\\n" +
//				"8b:a-lgal-HEX-1:5|6:d\\n" +
//				"9b:b-dgal-HEX-1:5\\n" +
//				"10b:b-dglc-HEX-1:5\\n" +
//				"11s:n-acetyl\\n" +
//				"12b:b-dgal-HEX-1:5\\n" +
//				"13b:a-lgal-HEX-1:5|6:d\\n" +
//				"LIN\\n" +
//				"1:1o(4+1)2d\\n" +
//				"2:2o(3+1)3d\\n" +
//				"3:3d(2+1)4n\\n" +
//				"4:3o(3+1)5d\\n" +
//				"5:2o(6+1)6d\\n" +
//				"6:6d(2+1)7n\\n" +
//				"7:6o(3+1)8d\\n" +
//				"8:6o(4+1)9d\\n" +
//				"9:9o(3+1)10d\\n" +
//				"10:10d(2+1)11n\\n" +
//				"11:10o(3+1)12d\\n" +
//				"12:12o(2+1)13d\\n";
//
//		logger.debug("sequence:>" + sequence + "<");
////		glycanProcedure.setId("G46627YI");
////		glycanProcedure.setContributorId("5854");
//		String result = glycanProcedure.register(sequence, "5854");
//
//		Assert.assertNotNull(result);
////		Assert.assertEquals("G46627YI", result);
//	}
	
//	@Test
//	@Transactional
//	public void testRegisterG92195EH() throws SparqlException, ConvertException
//	{
//
//		String sequence = "RES\\n" +
//				"1b:x-dglc-HEX-1:5\\n" +
//				"2b:b-dgal-HEX-1:5\\n" +
//				"3b:b-dglc-HEX-1:5\\n" +
//				"4s:n-acetyl\\n" +
//				"5b:b-dgal-HEX-1:5\\n" +
//				"6b:b-dglc-HEX-1:5\\n" +
//				"7s:n-acetyl\\n" +
//				"8b:a-lgal-HEX-1:5|6:d\\n" +
//				"9b:b-dgal-HEX-1:5\\n" +
//				"10b:b-dglc-HEX-1:5\\n" +
//				"11s:n-acetyl\\n" +
//				"12b:b-dgal-HEX-1:5\\n" +
//				"13b:a-lgal-HEX-1:5|6:d\\n" +
//				"LIN\\n" +
//				"1:1o(4+1)2d\\n" +
//				"2:2o(3+1)3d\\n" +
//				"3:3d(2+1)4n\\n" +
//				"4:3o(3+1)5d\\n" +
//				"5:2o(6+1)6d\\n" +
//				"6:6d(2+1)7n\\n" +
//				"7:6o(3+1)8d\\n" +
//				"8:6o(4+1)9d\\n" +
//				"9:9o(3+1)10d\\n" +
//				"10:10d(2+1)11n\\n" +
//				"11:10o(3+1)12d\\n" +
//				"12:12o(2+1)13d\\n";
//
//		logger.debug("sequence:>" + sequence + "<");
////		glycanProcedure.setId("G92195EH");
////		glycanProcedure.setContributorId("5854");
//		String result = glycanProcedure.register(sequence, "5854");
//
//		Assert.assertNotNull(result);
//		
//	}
	
	
	@Test
	@Transactional
	public void testListAll() throws SparqlException, NoSuchAlgorithmException {
		List<SparqlEntity> seList = glycanProcedure.getGlycans("100", "100");
    Assert.assertNotNull(seList);
		Assert.assertSame(100, seList.size());
		SparqlEntity se = seList.get(0);
		Assert.assertTrue(se.getValue(GlycoSequence.Sequence).contains("WURCS"));
	}
	
//	@Test
	public void testFindingMotifs() throws SparqlException {
		
		// structure should have motifs : G99992LL
		String acc = "WURCS=2.0/6,10,9/[a2122h-1x_1-5_2*NCC/3=O][a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a2112h-1b_1-5_2*NCC/3=O][Aad21122h-2a_2-6_5*NCC/3=O]/1-2-3-4-2-5-6-4-2-5/a4-b1_b4-c1_d2-e1_e4-f1_f6-g2_h2-i1_i4-j1_c?-d1_c?-h1";
		acc = "G95954RU";

		// find motifs for it
		ArrayList<SparqlEntity> list = glycanProcedure.findMotifs(acc);

		ArrayList<String> compare = new ArrayList<String>();
		for (SparqlEntity sparqlEntity : list) {
			String id = sparqlEntity.getValue(Saccharide.PrimaryId);
			compare.add(id);
		}
		
		// check results
		
		ArrayList<String> correct = new ArrayList<String>();
		correct.add("G00034MO");
		correct.add("G00042MO");
		correct.add("G00032MO");
		correct.add("G00055MO");
		correct.add("G00068MO");
		
		logger.debug(compare.toString());
		Assert.assertTrue(compare.containsAll(correct));
	}
	
	@Test
	public void testDatabaseInfo() throws SparqlException {
//		
//		ArrayList<SparqlEntity> list = glycanProcedure.getDatabaseInfo(acc);
//
//		ArrayList<String> compare = new ArrayList<String>();
//		for (SparqlEntity sparqlEntity : list) {
//			String id = sparqlEntity.getValue(Saccharide.PrimaryId);
//			compare.add(id);
//		}
//		
//		// check results
//		
//		ArrayList<String> correct = new ArrayList<String>();
//		correct.add("G00034MO");
//		correct.add("G00042MO");
//		correct.add("G00032MO");
//		correct.add("G00055MO");
//		correct.add("G00068MO");
//		
//		logger.debug(compare.toString());
//		Assert.assertTrue(compare.containsAll(correct));
	}
	
//	@Bean
//	SparqlEntityFactory sparqlEntityFactory() {
//		return new SparqlEntityFactory();
//	}
	
//	@Test
	@Transactional
	public void testRegisterUnicarb2237() throws SparqlException, ConvertException
	{

		String sequence = "RES\\n"
				+ "1b:o-dglc-HEX-0:0|1:aldi\\n"
				+ "2s:n-acetyl\\n"
				+ "3b:b-dglc-HEX-1:5\\n"
				+ "4s:n-acetyl\\n"
				+ "5b:a-lgal-HEX-1:5|6:d\\n"
				+ "LIN\\n"
				+ "1:1d(2+1)2n\\n"
				+ "2:1o(4+1)3d\\n"
				+ "3:3d(2+1)4n\\n"
				+ "4:1o(6+1)5d\\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "2237");

		Assert.assertNotNull(result);
		
	}
	
//	@Test
	@Transactional
	public void testRegisterWithCRLF() throws SparqlException, ConvertException
	{

		String sequence = "RES\r\n" + 
		    "1b:b-dglc-HEX-1:5\r\n" + 
		    "2s:n-acetyl\r\n" + 
		    "3b:b-dglc-HEX-1:5\r\n" + 
		    "4s:n-acetyl\r\n" + 
		    "5b:b-dman-HEX-1:5\r\n" + 
		    "6b:a-dman-HEX-1:5\r\n" + 
		    "7b:a-dman-HEX-1:5\r\n" + 
		    "8b:x-llyx-PEN-1:5\r\n" + 
		    "9b:x-lgal-HEX-1:5|6:d\r\n" + 
		    "LIN\r\n" + 
		    "1:1d(2+1)2n\r\n" + 
		    "2:1o(4+1)3d\r\n" + 
		    "3:3d(2+1)4n\r\n" + 
		    "4:3o(4+1)5d\r\n" + 
		    "5:5o(3+1)6d\r\n" + 
		    "6:5o(6+1)7d\r\n" + 
		    "7:7o(-1+1)8d\r\n" + 
		    "8:8o(-1+1)9d";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "999");

		Assert.assertNotNull(result);
	}
	
//		@Test
	@Transactional
	public void testRegisterUnicarbDB() throws SparqlException, ConvertException
	{

		String sequence = "RES\n"
				+ "1b:o-dglc-HEX-0:0|1:aldi\n"
				+ "2s:n-acetyl\n"
				+ "3b:b-dglc-HEX-1:5\n"
				+ "4s:n-acetyl\n"
				+ "5b:b-dman-HEX-1:5\n"
				+ "6b:a-dman-HEX-1:5\n"
				+ "7b:b-dglc-HEX-1:5\n"
				+ "8s:n-acetyl\n"
				+ "9b:b-dgal-HEX-1:5\n"
				+ "10b:a-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n"
				+ "11s:n-acetyl\n"
				+ "12b:a-dman-HEX-1:5\n"
				+ "13b:a-dman-HEX-1:5\n"
				+ "14b:a-dman-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n\n"
				+ "2:1o(4+1)3d\n"
				+ "3:3d(2+1)4n\n"
				+ "4:3o(4+1)5d\n"
				+ "5:5o(3+1)6d\n"
				+ "6:6o(2+1)7d\n"
				+ "7:7d(2+1)8n\n"
				+ "8:7o(4+1)9d\n"
				+ "9:9o(6+2)10d\n"
				+ "10:10d(5+1)11n\n"
				+ "11:5o(6+1)12d\n"
				+ "12:12o(3+1)13d\n"
				+ "13:12o(6+1)14d\n";

		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "999");

		Assert.assertNotNull(result);
		
	}
	
		
		
//		@Test(expected=DuplicateException.class)
//	@Transactional
//	public void testRegisterUnicarbDB2() throws SparqlException, ConvertException
//	{
//
//		String sequence = "RES\n" +
//		"1b:o-dgal-HEX-0:0|1:aldi\n" +
//				"2s:n-acetyl\n" +
//		"3b:b-dgal-HEX-1:5\n" +
//				"4b:a-lgal-HEX-1:5|6:d\n" +
//		"5b:b-dglc-HEX-1:5\n" +
//				"6s:n-acetyl\n" +
//		"7b:b-dgal-HEX-1:5\n" +
//				"8b:a-lgal-HEX-1:5|6:d\n" +
//		"9b:b-dglc-HEX-1:5\n" +
//				"10s:n-acetyl\n" +
//		"11b:b-dgal-HEX-1:5\n" +
//				"12b:a-lgal-HEX-1:5|6:d\n" +
//		"LIN\n" +
//				"1:1d(2+1)2n\n" +
//		"2:1o(3+1)3d\n" +
//				"3:3o(2+1)4d\n" +
//		"4:3o(3+1)5d\n" +
//				"5:5d(2+1)6n\n" +
//		"6:5o(4+1)7d\n" +
//				"7:7o(2+1)8d\n" +
//		"8:1o(6+1)9d\n" +
//				"9:9d(2+1)10n\n" +
//		"10:9o(4+1)11d\n" +
//				"11:11o(2+1)12d";
//		logger.debug("sequence:>" + sequence + "<");
////		glycanProcedure.setId("G92195EH");
////		glycanProcedure.setContributorId("5854");
//		String result = glycanProcedure.register(sequence, "999");
//
//		Assert.assertNotNull(result);
//		
//	}

		@Test(expected=SparqlException.class)
	@Transactional
	public void testRegisterUnicarbDBInvalidCT() throws SparqlException, ConvertException
	{

		String sequence = "RES\n"
				+ "1b:o-dgal-HEX-0:0|1:aldi\n"
				+ "2s:n-acetyl\n"
				+ "3b:b-dgal-HEX-1:5\n"
				+ "4b:b-dglc-HEX-1:5\n"
				+ "5s:n-acetyl\n"
				+ "6b:b-dgal-HEX-1:5\n"
				+ "LIN\n"
				+ "1:1d(2+1)2n\n"
				+ "2:1o(3+1)3d\n"
				+ "3:1o(6+1)4d\n"
				+ "4:4d(2+1)5n\n"
				+ "5:4o(4+1)6d\n"
				+ "UND\n"
				+ "UND1:100.0:100.0\n"
				+ "ParentIDs:1|3|4|6\n"
				+ "SubtreeLinkageID1:u(-1+1)u\n"
				+ "RES\n"
				+ "7s:sulfate\n";
		logger.debug("sequence:>" + sequence + "<");
//		glycanProcedure.setId("G92195EH");
//		glycanProcedure.setContributorId("5854");
		String result = glycanProcedure.register(sequence, "999");

		Assert.assertNotNull(result);
		
	}

		
		@Test
		@Transactional
		public void testInvalidCheckExists() throws SparqlException, ConvertException
		{

			String number = "somethingweird";
			logger.debug("number :>" + number + "<");
//			glycanProcedure.setId("G92195EH");
//			glycanProcedure.setContributorId("5854");
			boolean result = glycanProcedure.checkExists(number);

			Assert.assertFalse(result);
			
		}
		
		@Test
		@Transactional
		public void testInvalidCheckExists2() throws SparqlException, ConvertException
		{

			String number = "G123123";
			logger.debug("number :>" + number + "<");
//			glycanProcedure.setId("G92195EH");
//			glycanProcedure.setContributorId("5854");
			boolean result = glycanProcedure.checkExists(number);

			Assert.assertFalse(result);
			
		}	
		
		@Test
		@Transactional
		public void testRegisterKcf() throws SparqlException, ConvertException
		{
			
//			http://rings.t.soka.ac.jp/cgi-bin/tools/utilities/convert/convertJson.pl?convert_to=Wurcs&in_data=ENTRY%20%20%20%20%20XYZ%20%20%20%20%20%20%20%20%20%20Glycan%0ANODE%20%20%20%20%20%205%0A%20%20%20%20%20%20%20%20%20%201%20%20%20%20%20GlcNAc%20%20%20%20%2015.0%20%20%20%20%207.0%0A%20%20%20%20%20%20%20%20%20%202%20%20%20%20%20GlcNAc%20%20%20%20%20%208.0%20%20%20%20%207.0%0A%20%20%20%20%20%20%20%20%20%203%20%20%20%20%20Man%20%20%20%20%20%20%20%20%201.0%20%20%20%20%207.0%0A%20%20%20%20%20%20%20%20%20%204%20%20%20%20%20Man%20%20%20%20%20%20%20%20-6.0%20%20%20%2012.0%0A%20%20%20%20%20%20%20%20%20%205%20%20%20%20%20Man%20%20%20%20%20%20%20%20-6.0%20%20%20%20%202.0%0AEDGE%20%20%20%20%20%204%0A%20%20%20%20%20%20%20%20%20%201%20%20%20%20%202:b1%20%20%20%20%20%20%201:4%0A%20%20%20%20%20%20%20%20%20%202%20%20%20%20%203:b1%20%20%20%20%20%20%202:4%0A%20%20%20%20%20%20%20%20%20%203%20%20%20%20%205:a1%20%20%20%20%20%20%203:3%0A%20%20%20%20%20%20%20%20%20%204%20%20%20%20%204:a1%20%20%20%20%20%20%203:6%0A///%0A
//			http://rings.t.soka.ac.jp/cgi-bin/tools/utilities/convert/convertJson.pl?convert_to=Wurcs&in_data=ENTRY     XYZ          Glycan\nNODE      5\n          1     GlcNAc     15.0     7.0\n          2     GlcNAc      8.0     7.0\n          3     Man         1.0     7.0\n          4     Man        -6.0    12.0\n          5     Man        -6.0     2.0\nEDGE      4\n          1     2:b1       1:4\n          2     3:b1       2:4\n          3     5:a1       3:3\n          4     4:a1       3:6\n///\n
			String sequence = "ENTRY     XYZ          Glycan\n"
			+ "NODE      5\n"
			+ "          1     GlcNAc     15.0     7.0\n"
			+ "          2     GlcNAc      8.0     7.0\n"
			+ "          3     Man         1.0     7.0\n"
			+ "          4     Man        -6.0    12.0\n"
			+ "          5     Man        -6.0     2.0\n"
			+ "EDGE      4\n"
			+ "          1     2:b1       1:4\n"
			+ "          2     3:b1       2:4\n"
			+ "          3     5:a1       3:3\n"
			+ "          4     4:a1       3:6\n"
			+ "///\n";
			logger.debug("sequence:>" + sequence + "<");
//			glycanProcedure.setId("G92195EH");
//			glycanProcedure.setContributorId("5854");
			String result = glycanProcedure.register(sequence, "14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");

			Assert.assertNotNull(result);
			
		}
		
		
//		@Test
		@Transactional
		public void testUnicarb28780() throws SparqlException, ConvertException
		{

			String sequence = "RES\r\n"
					+ "1b:o-dgal-HEX-0:0|1:aldi\r\n"
					+ "2s:n-acetyl\r\n"
					+ "3b:b-dgal-HEX-1:5\r\n"
					+ "4b:a-lgal-HEX-1:5|6:d\r\n"
					+ "5b:b-dglc-HEX-1:5\r\n"
					+ "6s:n-acetyl\r\n"
					+ "7b:b-dgal-HEX-1:5\r\n"
					+ "8b:a-lgal-HEX-1:5|6:d\r\n"
					+ "9b:b-dglc-HEX-1:5\r\n"
					+ "10s:n-acetyl\r\n"
					+ "11b:b-dgal-HEX-1:5\r\n"
					+ "12b:a-lgal-HEX-1:5|6:d\r\n"
					+ "LIN\r\n"
					+ "1:1d(2+1)2n\r\n"
					+ "2:1o(3+1)3d\r\n"
					+ "3:3o(2+1)4d\r\n"
					+ "4:1o(6+1)5d\r\n"
					+ "5:5d(2+1)6n\r\n"
					+ "6:5o(4+1)7d\r\n"
					+ "7:7o(2+1)8d\r\n"
					+ "8:7o(3+1)9d\r\n"
					+ "9:9d(2+1)10n\r\n"
					+ "10:9o(4+1)11d\r\n"
					+ "11:11o(2+1)12d\r\n";
			logger.debug("sequence :>" + sequence + "<");
//			glycanProcedure.setId("G92195EH");
//			glycanProcedure.setContributorId("5854");
			String result = glycanProcedure.register(sequence, "14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");

			logger.debug("result :>" + result + "<");
			Assert.assertNotEquals(0, result.trim().length());
		}	
		
		
		@Test
		@Transactional
		public void testLinearCode() throws SparqlException, ConvertException
		{

			String sequence = "GNb2(Ab4GNb4)Ma3(Ab4GNb2(Fa3(Ab4)GNb6)Ma6)Mb4GNb4GN";
			logger.debug("sequence :>" + sequence + "<");
//			glycanProcedure.setId("G92195EH");
//			glycanProcedure.setContributorId("5854");
			String result = glycanProcedure.register(sequence, "14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");

			logger.debug("result :>" + result + "<");
			Assert.assertNotEquals(0, result.trim().length());
		}	
		
		@Test
		@Transactional
		public void testDescriptionQuery() throws InvalidException {
			SparqlEntity description = glycanProcedure.getDescription("G00055MO");
			String desc = description.getValue(org.glycoinfo.rdf.service.impl.GlycanProcedure.Description);
			logger.debug(desc);
			Assert.assertTrue(desc.contains("Gal(b1-4)GlcNAc(b1-"));
		}
		
		@Test(expected=InvalidException.class)
		@Transactional
		public void testInvalidNumber() throws InvalidException {
			SparqlEntity description = glycanProcedure.getDescription("GTESTING");
			String desc = description.getValue(org.glycoinfo.rdf.service.impl.GlycanProcedure.Description);
			logger.debug(desc);
		}
		
	  /**	
	   * 
	   * Check for retrieving description of structure right after registration.
	   * 
	   * This can occur when entry page referenced right after registering.  org.openrdf package has an assertion on null values, even though sparql can have optional fields.
	   * Thus assertions need to be turned off at jvm: -da:org.openrdf
	   * 
	   * @throws SparqlException
	   */
    @Test
    @Transactional
    public void testDescriptionQueryNewRegistration() throws InvalidException, SparqlException {
      // register a new structure
      String result = glycanProcedure.register("WURCS=2.0/5,7,6/[a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a1221m-1x_1-5][a2122h-1x_1-5_2*NCC/3=O]/1-1-2-3-3-4-5/a4-b1_b4-c1_c3-d1_c6-e1_e?-f1_f?-g1", "14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");
      
      SparqlEntity description = glycanProcedure.getDescription(result);
      String desc = description.getValue(org.glycoinfo.rdf.service.impl.GlycanProcedure.Description);
      logger.debug(desc);
      Assert.assertTrue(desc.contains(""));
    }
		
    @Test
    @Transactional
    public void testWurcs() throws SparqlException, ConvertException
    {

      String sequence = "WURCS=2.0/5,7,6/[a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a1221m-1x_1-5][a2122h-1x_1-5_2*NCC/3=O]/1-1-2-3-3-4-5/a4-b1_b4-c1_c3-d1_c6-e1_e?-f1_f?-g1";
      logger.debug("sequence :>" + sequence + "<");
      String result = glycanProcedure.register(sequence, "815e7cbca52763e5c3fbb5a4dccc176479a50e2367f920843c4c35dca112e33d");

      logger.debug("result :>" + result + "<");
      Assert.assertNotEquals(0, result.trim().length());
      SparqlEntity sparqlEntity = glycanProcedure.searchByAccessionNumber(result);
    }

//    @Test
    @Transactional
    public void testTopologyDuplicates() throws SparqlException, ConvertException {
//      String sequence = "WURCS=2.0/2,3,2/[h44h][a2122h-1x_1-5]/1-2-2/a?-b1_b?-c1";
//      String result = glycanProcedure.register(sequence, "14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");
//      logger.debug(result);
      
      String sequence = "WURCS=2.0/5,7,6/[a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a221h-1x_1-5][a1221m-1x_1-5]/1-1-2-3-3-4-5/a4-b1_b4-c1_c3-d1_c6-e1_e?-f1_f?-g1";
      String result = glycanProcedure.register(sequence, "14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");
      logger.debug(result);
      
      Assert.assertNotNull(result);
    }
    
    @Test(expected=ContributorException.class)
    @Transactional
    public void testAddResourceInvalidUser() throws SparqlException, ConvertException, GlycanException, ContributorException {
    	glycanProcedure.addResourceEntry("G00030MO", "1929291475", "815e7cbca52763e5c3fbb5a4dccc176479a50e2367f920843c4c35dca112e33d");
    }
    
    
    @Test
    @Transactional
    public void testAddResourceG00030MO() throws SparqlException, ConvertException, GlycanException, ContributorException {
    	String id = contributorProcedure.addContributor("hiTesting", "testglytoucan@gmail.com");
    	contributorProcedure.memberDb("testglytoucan@gmail.com", "unicarb-db");
    	
    	String result = glycanProcedure.addResourceEntry("G00030MO", id, "123");
    	logger.debug("result:>" + result + "<");
    	Assert.assertTrue("contains unicarb-db", result.contains("unicarb-db"));
    }
    
    @Test
    @Transactional
    public void testAddResourceG00029MONobu() throws SparqlException, ConvertException, GlycanException, ContributorException {
      String result = glycanProcedure.addResourceEntry("G00029MO", NumberGenerator.generateSHA256Hash("aokinobu@gmail.com"), "29");
      logger.debug("result:>" + result + "<");
      Assert.assertTrue("contains glycoepitope", result.contains("glycoepitope"));
    }
    
    @Test
    @Transactional
    public void testAddResourceG16546ZZNobu() throws SparqlException, ConvertException, GlycanException, ContributorException {
      String result = glycanProcedure.addResourceEntry("G16546ZZ", NumberGenerator.generateSHA256Hash("aokinobu@gmail.com"), "12345");
      logger.debug("result:>" + result + "<");
      Assert.assertTrue("contains glycoepitope", result.contains("glycoepitope"));
      
      glycanProcedure.removeResourceEntry("G16546ZZ", NumberGenerator.generateSHA256Hash("aokinobu@gmail.com"), "12345");
      
      ResourceEntrySelectSparql ress = new ResourceEntrySelectSparql();
      ress.setFrom("FROM <http://rdf.glytoucan.org/partner/glycoepitope>\n");
      SparqlEntity sparqlentity = new SparqlEntity();
      sparqlentity.setValue(ResourceEntry.Identifier, "12345");
      ress.setSparqlEntity(sparqlentity);
      
      List<SparqlEntity> results = sparqlDAO.query(ress);
      
      Assert.assertTrue("results should be 0", results.size() == 0);
    }
    
    @Test
    @Transactional
//    WURCS=2.0/1,1,0/[Ad1zz12h_3-7_1*N(CC^ZCCCC^ZCCCC$7/6CC$3)_6*NCN(C^ECC^ECC^ZC$5)/7OC/3=O]/1/
    public void testRemoveResourceDollarMark() throws SparqlException, ConvertException, GlycanException, ContributorException {
    	
  String sequence ="WURCS=2.0/1,1,0/[Ad1zz12h_3-7_1*N(CC^ZCCCC^ZCCCC$7/6CC$3)_6*NCN(C^ECC^ECC^ZC$5)/7OC/3=O]/1/"; 
    	SparqlEntity sequenceSE = glycanProcedure.searchBySequence(sequence);
    	logger.debug(sequenceSE.getValue(GlycanProcedure.AccessionNumber));
    	
    	
//      glycanProcedure.removeResourceEntry(sequenceSE.getValue(GlycanProcedure.AccessionNumber), NumberGenerator.generateSHA256Hash("aokinobu@gmail.com"), "12345REMOVEME");
      glycanProcedure.removeResourceEntry(sequenceSE.getValue(GlycanProcedure.AccessionNumber), "82f22ca13e2ce280f13b346a7bd0cc37e8b9b557b43a081bd04b517e9acbc5c4", "CHEBI:100039");
      
      ResourceEntrySelectSparql ress = new ResourceEntrySelectSparql();
      ress.setFrom("FROM <http://rdf.glytoucan.org/partner/glyconavi>\n");
      SparqlEntity sparqlentity = new SparqlEntity();
      sparqlentity.setValue(ResourceEntry.Identifier, "CHEBI:100039");
      ress.setSparqlEntity(sparqlentity);
      
      List<SparqlEntity> results = sparqlDAO.query(ress);
      
      Assert.assertTrue("results should be 0", results.size() == 0);
    }
    
    @Test
    @Transactional
    public void testRemoveResourceG16546ZZNobu() throws SparqlException, ConvertException, GlycanException, ContributorException {
      glycanProcedure.removeResourceEntry("G00048MO", NumberGenerator.generateSHA256Hash("aokinobu@gmail.com"), "12345REMOVEME");
      
      ResourceEntrySelectSparql ress = new ResourceEntrySelectSparql();
      ress.setFrom("FROM <http://rdf.glytoucan.org/partner/glycoepitope>\n");
      SparqlEntity sparqlentity = new SparqlEntity();
      sparqlentity.setValue(ResourceEntry.Identifier, "12345REMOVEME");
      ress.setSparqlEntity(sparqlentity);
      
      List<SparqlEntity> results = sparqlDAO.query(ress);
      
      Assert.assertTrue("results should be 0", results.size() == 0);
    }
    
    
	@Test(expected=InvalidException.class)
	public void testGetDescriptionCore() throws InvalidException {
//		G68335WN will be archived so invalid exception expected
	  SparqlEntity description = glycanProcedure.getDescriptionCore("G68335WN");
      String desc = description.getValue(org.glycoinfo.rdf.service.impl.GlycanProcedure.Description);
      logger.debug(desc);
	  Assert.assertNull(desc);
	}
	
	@Test
	public void testArchived() throws InvalidException {
	  List<SparqlEntity> seList = glycanProcedure.getArchivedAccessionNumbers("0", "100");
		SparqlEntity se = new SparqlEntity();
		String id = null;

		if (seList.iterator().hasNext()) {
			se = seList.iterator().next();
			id = se.getValue("archivedId");
			logger.debug("archived ID:" + id);
		} else
			throw new InvalidException("Sparql Query Invalid");
	}
}