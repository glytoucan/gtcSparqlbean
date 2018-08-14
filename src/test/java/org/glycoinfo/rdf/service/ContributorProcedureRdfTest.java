package org.glycoinfo.rdf.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.convert.GlyConvertConfig;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.virt.SparqlDAOVirtSesameImpl;
import org.glycoinfo.rdf.dao.virt.VirtSesameTransactionConfig;
import org.glycoinfo.rdf.glycan.Contributor;
import org.glycoinfo.rdf.glycan.ContributorInsertSparql;
import org.glycoinfo.rdf.glycan.ContributorNameSelectSparql;
import org.glycoinfo.rdf.scint.SelectScint;
import org.glycoinfo.rdf.service.exception.ContributorException;
import org.glycoinfo.rdf.service.impl.ContributorProcedureConfig;
import org.glycoinfo.rdf.service.impl.ContributorProcedureRdf;
import org.glycoinfo.rdf.service.impl.GlycanProcedureConfig;
import org.glycoinfo.rdf.utils.NumberGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aoki
 *
 * This work is licensed under the Creative Commons Attribution 4.0 International License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/.
 *
 */
//@SpringApplicationConfiguration(classes = UserProcedureRdfTest.class)
//@ComponentScan(basePackages = {"org.glycoinfo.rdf.service"})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ContributorProcedureRdfTest.class, VirtSesameTransactionConfig.class, ContributorProcedureConfig.class, GlycanProcedureConfig.class, GlyConvertConfig.class})
//@ComponentScan(basePackages = {"org.glycoinfo.rdf.service"})
//@ComponentScan(basePackages = {"org.glycoinfo.rdf"}, excludeFilters={
//		  @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=Configuration.class)})
@Configuration
@EnableAutoConfiguration
public class ContributorProcedureRdfTest  {
  
  private static final Log logger = LogFactory.getLog(ContributorProcedureRdfTest.class);

	@Autowired
	ContributorProcedure contributorProcedure;
	
	@Autowired
	ContributorInsertSparql contributorSparql;
	
	 @Autowired
	  @Qualifier(value = "selectscintperson")
	  SelectScint selectScintPerson;
	 
	 @Autowired
	 SparqlDAO sparqlDAO;
	
	 @Bean(name = "selectscintperson")
	  SelectScint getSelectPersonScint() throws SparqlException {
	    SelectScint select = new SelectScint("schema", "http://schema.org/", "Person");
//	    select.setClassHandler(getPersonClassHandler());
	    return select;
	  }
	
	@Test
	@Transactional
	public void testAddContributor() throws ContributorException {
		String id = contributorProcedure.addContributor("test", "testglytoucan@gmail.com");
		Assert.assertNotNull(id);
	}
	
	@Test
	@Transactional
	public void testAddSearch() throws ContributorException {
		String id = contributorProcedure.addContributor("test2", "testglytoucan@gmail.com");
		Assert.assertNotNull(id);
		
		SparqlEntity idData = contributorProcedure.searchContributor("testglytoucan@gmail.com");
		
		Assert.assertEquals(id, idData.getValue(Contributor.ID));
	}
	
	@Test
	@Transactional
	public void testAddNewMember() throws ContributorException {
		String id = contributorProcedure.addContributor("testglytoucan", "testglytoucan@gmail.com");
		Assert.assertNotNull(id);
		
		contributorProcedure.memberDb("testglytoucan@gmail.com", "unicarb-db");
		SparqlEntity result = contributorProcedure.selectDatabaseByContributor(id);
		
		Assert.assertNotNull(result);
	}
	
	 
  @Test
  @Transactional
  public void testDeleteMember() throws ContributorException {
//    String id = contributorProcedure.addContributor("testglytoucan", "testglytoucan@gmail.com");
//    Assert.assertNotNull(id);
    
    contributorProcedure.deleteMember("aokinobu@gmail.com", "glycoepitope");
    SparqlEntity result = contributorProcedure.selectDatabaseByContributor("14e1d868cf50557143032041eef95cc7271b8c3a0bdc5a52fb849cdf29ef4aff");
    
//    Assert.assertNotNull(result);
  }
	
	@Test
	@Transactional
	public void testAddMembership254() throws ContributorException {
		String id = "aokinobu@gmail.com";
		contributorProcedure.memberDb(id, "glycoepitope");
		SparqlEntity result = contributorProcedure.selectDatabaseByContributor(NumberGenerator.generateSHA256Hash(id));
		
		Assert.assertNotNull(result);
	}
	
	@Test
	@Transactional
	public void testAddMembershipConfirm() throws ContributorException {
		String id = "aokinobu@gmail.com";
		contributorProcedure.memberDb(id, "unicarb-db");
		SparqlEntity result = contributorProcedure.selectDatabaseByContributor(NumberGenerator.generateSHA256Hash(id));
		
		Assert.assertNotNull(result);
	}
	
	@Test
	@Transactional
	public void testConvertToEmailHash() throws SparqlException, ContributorException {
	  // retrieve all emails from schema
	  selectScintPerson.update();
	  List<SparqlEntity> results = sparqlDAO.query(selectScintPerson.getSparqlBean());
	  for (Iterator<SparqlEntity> iterator = results.iterator(); iterator.hasNext();) {
      SparqlEntity sparqlEntity = (SparqlEntity) iterator.next();
      logger.debug(sparqlEntity.getValue("email"));
      // addContributor using first name
      
//      contributorProcedure.addContributor(sparqlEntity.getValue("givenName"), sparqlEntity.getValue("email"));
      String hash = NumberGenerator.generateSHA256Hash(sparqlEntity.getValue("email"));
      // insert the above data.
      SparqlEntity sparqlEntityPerson = new SparqlEntity(hash);
      sparqlEntityPerson.setValue(ContributorInsertSparql.ContributorName, sparqlEntity.getValue("givenName") + " " + sparqlEntity.getValue("familyName"));
//      sparqlEntityPerson.setValue(ContributorInsertSparql.UserId, sparqlEntity.getValue("alternateName"));
      sparqlEntityPerson.setValue(ContributorInsertSparql.ID, hash);
      contributorSparql.setSparqlEntity(sparqlEntityPerson);

    try {
      sparqlDAO.insert(contributorSparql);    
    } catch (SparqlException e) {
      throw new ContributorException(e);
    }

    }
	  
	  
	}
	
}