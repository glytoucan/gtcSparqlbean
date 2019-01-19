package org.glycoinfo.rdf.glycan;

import org.glycoinfo.rdf.InsertSparql;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.dao.virt.SparqlDAOVirtSesameImpl;
import org.springframework.context.annotation.Bean;

public class TestConfig {
	
	@Bean
	SparqlDAO getSparqlDAO() {
		return new SparqlDAOVirtSesameImpl();
	}
	
	@Bean
	InsertSparql getInsertSparql() {
		ContributorInsertSparql ins = new ContributorInsertSparql();
		SparqlEntity sparqlentity = new SparqlEntity();
		sparqlentity.setValue(ContributorInsertSparql.UserId, "1234");
		sparqlentity.setValue(ContributorInsertSparql.ContributorName, "testname");
		ins.setSparqlEntity(sparqlentity);
		ins.setGraph("http://test");
		return ins;
	}

}
