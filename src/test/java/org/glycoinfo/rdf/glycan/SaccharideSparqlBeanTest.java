package org.glycoinfo.rdf.glycan;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.virt.VirtSesameTransactionConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * Test the sparql string and actual data with rollback.
 * 
 * @author aoki
 *
 * This work is licensed under the Creative Commons Attribution 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Import({SaccharideSparqlBeanTest.class, VirtSesameTransactionConfig.class })
@Configuration
@EnableAutoConfiguration
public class SaccharideSparqlBeanTest {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(SaccharideSparqlBeanTest.class);
	
	@Autowired
	SparqlDAO sparqlDAO;

	@Bean
	SaccharideInsertSparql getSaccharideInsertSparql() {
		SaccharideInsertSparql ins = new SaccharideInsertSparql();
		SparqlEntity sparqlentity = new SparqlEntity();
		sparqlentity.setValue(Saccharide.URI, "insertsacharideuri");
		sparqlentity.setValue(GlycoSequence.URI, "glycosequenceuri");
		sparqlentity.setValue(Saccharide.PrimaryId, "primaryid");
		ins.setSparqlEntity(sparqlentity);
		ins.setGraph("http://test");
		return ins;
	}

	@Bean
	SaccharideSelectSparql getSaccharideSelectSparql() {
		SaccharideSelectSparql sis = new SaccharideSelectSparql();
		SparqlEntity sparqlentity = new SparqlEntity();
		sparqlentity.setValue(Saccharide.PrimaryId, "primaryid");
		sis.setSparqlEntity(sparqlentity);
		return sis;
	}
	
//	@Test
	public void testInsertSparql() throws SparqlException {
		logger.debug(getSaccharideInsertSparql().getSparql());
		
		assertEquals(
				"prefix glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n"
				+ "INSERT DATA\n"
				+ "{ GRAPH <http://test>\n"
				+ "{ <http://rdf.glycoinfo.org/glycan/primaryid> a glycan:saccharide .\n"
				+ "<http://rdf.glycoinfo.org/glycan/primaryid> glytoucan:has_primary_id \"primaryid\"^^xsd:string .\n"
						+ " }\n"
						+ "}\n",
				getSaccharideInsertSparql().getSparql());
	}
	
	@Test
	@Transactional
	public void insertSparql() throws SparqlException {
		sparqlDAO.insert(getSaccharideInsertSparql());
		
		List<SparqlEntity> list = sparqlDAO.query(new SelectSparqlBean("prefix glycan: <http://purl.jp/bio/12/glyco/glycan#>\n"
				+ "select ?s where { <http://rdf.glycoinfo.org/glycan/primaryid> a ?s }"));
		
		for (SparqlEntity sparqlEntity : list) {
			String output = sparqlEntity.getValue("s");
			Assert.assertEquals("http://purl.jp/bio/12/glyco/glycan#saccharide", output);
		}
	}

	@Test
	@Transactional
	public void insertSelectSparql() throws SparqlException {
		sparqlDAO.insert(getSaccharideInsertSparql());
		
		List<SparqlEntity> list = sparqlDAO.query(getSaccharideSelectSparql());
		if (list.iterator().hasNext()) {
			SparqlEntity se = list.iterator().next();
			logger.debug(se.getValue(SaccharideSelectSparql.SaccharideURI));
		}
	}
	
	@Test
	@Transactional
	public void selectSparql() throws SparqlException {
		SaccharideSelectSparql sss = getSaccharideSelectSparql();
		SparqlEntity se = sss.getSparqlEntity();
		se.setValue(Saccharide.PrimaryId, "G00031MO");
		
		sss.setSparqlEntity(se);
		
		List<SparqlEntity> list = sparqlDAO.query(sss);
		if (list.iterator().hasNext()) {
			se = list.iterator().next();
			logger.debug(se.getValue(SaccharideSelectSparql.SaccharideURI));
		}
	}
}