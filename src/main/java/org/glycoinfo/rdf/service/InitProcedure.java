package org.glycoinfo.rdf.service;

import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlEntity;

public interface InitProcedure {
	public void initialize() throws SparqlException;
	
	public void setSparqlEntity(SparqlEntity s);
	
	public SparqlEntity getSparqlEntity();
}