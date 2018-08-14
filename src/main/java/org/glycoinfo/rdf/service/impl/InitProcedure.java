package org.glycoinfo.rdf.service.impl;

import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlDAO;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.scint.ClassHandler;
import org.glycoinfo.rdf.scint.InsertScint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class InitProcedure implements org.glycoinfo.rdf.service.InitProcedure {

	String[] requiredFields = {"description", "name"};
	
	@Autowired
	SparqlDAO sparqlDAO;
	
//	@Autowired
//	@Qualifier(value = "selectscintperson")
//	SelectScint selectScintPerson;

//	@Autowired
//	@Qualifier(value = "selectscintregisteraction")
//	SelectScint selectScintRegisterAction;

	@Autowired(required=false)
	@Qualifier(value = "insertscintorganization")
	InsertScint insertScintOrganization;

	ClassHandler getServiceClassHandler() throws SparqlException {
		ClassHandler scint = new ClassHandler("schema", "http://schema.org/", "Service");
		scint.setSparqlDAO(sparqlDAO);
		return scint; 
	}
	
	ClassHandler getOrganizationClassHandler() throws SparqlException {
		ClassHandler scint = new ClassHandler("schema", "http://schema.org/", "Organization");
		scint.setSparqlDAO(sparqlDAO);
		return scint;
	}
	
	ClassHandler getDateTimeClassHandler() throws SparqlException {
		ClassHandler scint = new ClassHandler("schema", "http://schema.org/", "DateTime");
		scint.setSparqlDAO(sparqlDAO);
		return scint; 
	}
	
	SparqlEntity se;
	
	public void initialize() throws SparqlException {
		SparqlEntity se = new SparqlEntity();
		se.setValue(SelectSparql.PRIMARY_KEY, "glytoucan");
		se.setValue("name", "glytoucan");
		se.setValue("description", "The International Glycan Repository");

//		Set<String> columns = se.getColumns();
//		if (!columns.containsAll(Arrays.asList(requiredFields))) {
//			throw new SparqlException("not all required fields are supplied");
//		}
		
		insertScintOrganization.getSparqlBean().setSparqlEntity(se);
		sparqlDAO.insert(insertScintOrganization.getSparqlBean());
	}

	@Override
	public void setSparqlEntity(SparqlEntity s) {
		this.se=s;
	}

	@Override
	public SparqlEntity getSparqlEntity() {
		return se;
	}
	
//	public String[] getRequiredFields() {
//		return requiredFields;
//	}
//
//	public void setRequiredFields(String[] requiredFields) {
//		this.requiredFields = requiredFields;
//	}
}