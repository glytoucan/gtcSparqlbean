package org.glycoinfo.rdf.glycan.wurcs;

import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.virt.VirtSesameTransactionConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { GlycoSequenceResourceEntryContributorSelectSparqlTest.class , VirtSesameTransactionConfig.class })
@Configuration
@EnableAutoConfiguration
public class GlycoSequenceResourceEntryContributorSelectSparqlTest {
  @Autowired
  SparqlDAO sparqlDAO;

//  @Autowired
//  GlycoSequenceResourceEntryContributorSelectSparql glycoSequenceResourceEntryContributorSelectSparql;
  @Test
  @Transactional
  public void testGlycoSequenceResourceEntryContributorSelectSparql() throws Exception {
    GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
    sb.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>\nFROM <http://rdf.glytoucan.org/sequence/glycoct>\nFROM <http://rdf.glytoucan.org/users>\n");
    sb.setLimit("100");
    
    sparqlDAO.query(sb);
    
  }

}
