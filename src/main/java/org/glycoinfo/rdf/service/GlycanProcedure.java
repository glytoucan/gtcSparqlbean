package org.glycoinfo.rdf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.glycoinfo.WURCSFramework.util.WURCSException;
import org.glycoinfo.WURCSFramework.util.array.mass.WURCSMassException;
import org.glycoinfo.convert.error.ConvertException;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.glycoinfo.rdf.service.exception.ContributorException;
import org.glycoinfo.rdf.service.exception.GlycanException;
import org.glycoinfo.rdf.service.exception.InvalidException;

public interface GlycanProcedure {
	public static final String Image = "image"; 
	public static final String CouldNotConvertHeader = "Failed Conversion:";
	public static final String NotRegistered = "not registered";
	public static final String CouldNotConvert = "could not convert";
	public static final String AlreadyRegistered = "already registered";
	public static final String AccessionNumber = Saccharide.PrimaryId;
	public static final String Sequence = "Sequence";
	public static final String FromSequence = "FromSequence";
	public static final String Format = "Format";
	public static final String Id = "Id";
	public static final String ResultSequence = "ResultSequence";
	public static final String Description = "Description";
	
	public static final String CouldNotConvertErrorCode = "-100";
	
	public void setBatch(boolean batch);
	
	List<SparqlEntity> search(List<String> input) throws SparqlException;

	public String register(String sequence, String contributorId) throws SparqlException;

	public void deleteByAccessionNumber(String accessionNumber);
	
	public SparqlEntity searchByAccessionNumber(SparqlEntity accessionNumber) throws SparqlException;

	public SparqlEntity searchByAccessionNumber(String accessionNumber) throws SparqlException;

	public List<SparqlEntity> getGlycans(String offset, String limit) throws SparqlException;

	public List<SparqlEntity> substructureSearch(String sequence, String limit, String offset) throws SparqlException;

	public ArrayList<SparqlEntity> findMotifs(String wurcs) throws SparqlException;

	boolean checkExists(String id) throws SparqlException;

	SparqlEntity searchBySequence(String sequence) throws SparqlException,
			ConvertException;

	Map<String, String> register(List<String> inputs, String contributorId)
			throws SparqlException;

	void registerGlycoSequence(SparqlEntity data) throws SparqlException;

	String initialize(String sequence, String id) throws SparqlException;

	/**
	 * 
	 * Insert the resource entry value for a particular partner database id.  The database in which it is assocated to, is based upon the membership of the username.
	 * WARNING: There are no checks on the accession number.  It is assumed to be valid before executing this.  Otherwise unlinked data will be stored (and never used).
	 * 
	 * @param accessionNumber
	 * @param contributorId
	 * @param dbId
	 * @return
	 * @throws GlycanException in case of invalid accession Number
	 * @throws ContributorException in case of invalid contributor
	 */
	public String addResourceEntry(String accessionNumber, String contributorId, String dbId) throws GlycanException, ContributorException;

	String convertToWurcs(String sequence) throws ConvertException;

	String validateWurcs(String sequence) throws WURCSException;

	SparqlEntity getDescription(String accessionNumber) throws InvalidException;

  List<SparqlEntity> getGlycansAll(String offset, String limit) throws SparqlException;

  SparqlEntity searchSequenceByFormatAccessionNumber(String accessionNumber, String format)
      throws SparqlException;

  SparqlEntity getCount();

  String register(String sequence, String contributorId, String partnerId) throws GlycanException, ContributorException;

  SparqlEntity removeResourceEntry(String accessionNumber, String contributorId, String dbId) throws GlycanException, ContributorException;

SparqlEntity getDescriptionCore(String accessionNumber) throws InvalidException;

List<SparqlEntity> getArchivedAccessionNumbers(String offset, String limit) throws InvalidException;

SparqlEntity searchByAccessionNumberIncludingArchived(String accessionNumber) throws SparqlException;

SparqlEntity calculateMass(String sequence) throws SparqlException, WURCSMassException;
}