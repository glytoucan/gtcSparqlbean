package org.glycoinfo.rdf.service;

import java.util.List;

import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.service.exception.ContributorException;


public interface ContributorProcedure {
	
	/**
	 * 
	 * Add a contributor with String name.
	 * First executes searchContributor to confirm it doesn't exist.
	 * If it exists, returns the id of the existing user, otherwise the id of the new user.
	 * 
	 * @param name
	 * @return
	 * @throws ContributorException
	 */
	String addContributor(String name, String email) throws ContributorException;
	
	SparqlEntity searchContributor(String email) throws ContributorException;

	/**
	 * 
	 * Retrieve the glycan database literal named individual in which the contributor is a member of.
	 * 
	 * @param contributorId
	 * @return
	 * @throws ContributorException
	 */
	SparqlEntity selectDatabaseByContributor(String contributorId) throws ContributorException;
	
	
	/**
	 * @param entries
	 * @param id
	 * @return
	 * @throws ContributorException
	 */
//	List<SparqlEntity> insertResourceEntry(List<SparqlEntity> entries, String id) throws ContributorException;
	
	/**
	 * 
	 * Set contributor to become a member of a database.  
	 * In other words, insert the user to be an foaf:member of the database 
	 * which glycan:has_abbreviation of the passed dbAbbreviation.
	 * 
	 * @param contributorId contributor Id of the user
	 * @param dbAbbreviation database abbreviation
	 * @throws UserException
	 */
	public void memberDb(String contributorId, String dbAbbreviation) throws ContributorException;

  void deleteMember(String email, String dbAbbreviation) throws ContributorException;
}