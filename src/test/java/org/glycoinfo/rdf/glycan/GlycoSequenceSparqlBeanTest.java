package org.glycoinfo.rdf.glycan;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.glycoinfo.convert.GlyConvert;
import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.virt.VirtSesameTransactionConfig;
import org.glycoinfo.rdf.glycan.wurcs.WurcsGlycoSequenceSelectSparql;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { GlycoSequenceSparqlBeanTest.class , VirtSesameTransactionConfig.class })
@Configuration
@EnableAutoConfiguration
public class GlycoSequenceSparqlBeanTest {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(GlycoSequenceSparqlBeanTest.class);
	
	@Autowired
	SparqlDAO sparqlDAO;

	@Autowired
  SelectSparql glycosequenceSelect;
	
	@Bean
	WurcsGlycoSequenceSelectSparql getGlycoSequenceSparql() {
		WurcsGlycoSequenceSelectSparql ins = new WurcsGlycoSequenceSelectSparql();
		SparqlEntity sparqlentity = new SparqlEntity();
		sparqlentity.setValue(Saccharide.PrimaryId, "G00009BX");
		ins.setSparqlEntity(sparqlentity);
		return ins;
	}

	 @Bean
	  SelectSparql glycosequenceSelect() {
	   SelectSparql ins = new GlycoSequenceSelectSparql();
	   ins.setFrom(ins.getFrom() + "\nFROM <http://rdf.glytoucan.org>");
	    SparqlEntity sparqlentity = new SparqlEntity();
	    ins.setSparqlEntity(sparqlentity);
	    return ins;
	  }

	
//	@Test
	public void testSelectSparql() throws SparqlException {
		logger.debug(getGlycoSequenceSparql().getSparql());
		
		assertEquals(
				"INSERT INTO\n"
				+ "{ insertsacharideuri a glycosequenceuri .\n"
				+ "insertsacharideuri glytoucan:has_primary_id primaryid .\n"
				+ " }\n",
				getGlycoSequenceSparql().getSparql());
	}
	
	@Test
	 @Transactional
	public void testRunSelectSparql() throws SparqlException {
	  
		SparqlEntity se = new SparqlEntity();
		String glycoct = "RES\n1b:a-dgal-HEX-1:5\n2s:n-acetyl\n3b:b-dgal-HEX-1:5\n4b:b-dglc-HEX-1:5\n5s:n-acetyl\n6b:b-dgal-HEX-1:5\n7b:a-lgal-HEX-1:5|6:d\n8b:b-dglc-HEX-1:5\n9s:n-acetyl\n10b:b-dglc-HEX-1:5\n11s:n-acetyl\n12b:b-dgal-HEX-1:5\n13b:a-lgal-HEX-1:5|6:d\nLIN\n1:1d(2+1)2n\n2:1o(3+1)3d\n3:3o(3+1)4d\n4:4d(2+1)5n\n5:4o(4+1)6d\n6:6o(2+1)7d\n7:3o(6+1)8d\n8:8d(2+1)9n\n9:1o(6+1)10d\n10:10d(2+1)11n\n11:10o(4+1)12d\n12:12o(2+1)13d";
		String input = glycoct.replaceAll("(?:\\r\\n|\\n)", "\\\\n");
		se.setValue(GlycoSequence.Sequence, input);
		se.setValue(GlycoSequence.Format, GlyConvert.GLYCOCT);
		glycosequenceSelect.setSparqlEntity(se);
		logger.debug(glycosequenceSelect.getSparql());

		List<SparqlEntity> list = sparqlDAO.query(glycosequenceSelect);
		
		logger.debug(""+list);
		Assert.assertNotEquals(list.size(), 0);
	}
	
	@Test
	 @Transactional
	public void testRunSelectToWurcsSparqlG00031MO() throws SparqlException {
	  SparqlEntity se = new SparqlEntity();
		String glycoct = "RES\n" + 
		    "1b:a-dgal-HEX-1:5\n" + 
		    "2s:n-acetyl\n" + 
		    "3b:b-dgal-HEX-1:5\n" + 
		    "LIN\n" + 
		    "1:1d(2+1)2n\n" + 
		    "2:1o(3+1)3d";
		
    String sparqlSequence = glycoct.replaceAll("(?:\\r\\n|\\n)", "\\\\n");
    se.setValue(GlycoSequence.Sequence, sparqlSequence);
    se.setValue(GlycoSequence.Format, GlyConvert.GLYCOCT);
		glycosequenceSelect.setSparqlEntity(se);

		List<SparqlEntity> list = sparqlDAO.query(glycosequenceSelect);
		
		logger.debug(""+list);
		Assert.assertNotEquals(list.size(), 0);
	}
}