package org.glycoinfo.rdf.glycan;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.rdf.InsertSparql;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.virt.SparqlDAOVirtSesameImpl;
import org.glycoinfo.rdf.dao.virt.VirtSesameTransactionConfig;
import org.glycoinfo.rdf.glycan.ContributorInsertSparql;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aoki
 *
 * This work is licensed under the Creative Commons Attribution 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.
 *
 */
@SpringBootTest
@Import({TestConfig.class })
@EnableAutoConfiguration
public class ContributorInsertSparqlTest {
	private static final Log logger = LogFactory
			.getLog(ContributorInsertSparqlTest.class);

	@Autowired
	SparqlDAO sparqlDAO;
	
	@Autowired
	InsertSparql insertSparql;

	@Test
	@Transactional
	public void insertSparql() throws SparqlException {
		sparqlDAO.insert(insertSparql);
		
		List<SparqlEntity> list = sparqlDAO.query(new SelectSparqlBean(insertSparql.getPrefix()
				+ "select ?name from <http://test> where { ?s a foaf:Person . ?s dcterms:identifier \"1234\"^^xsd:int . ?s foaf:name ?name .}"));
		
		for (SparqlEntity sparqlEntity : list) {
			String output = sparqlEntity.getValue("name");
			Assert.assertEquals("testname", output);
		}
	}
	
	
}