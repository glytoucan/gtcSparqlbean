package org.glycoinfo.rdf.glycan;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glycoinfo.WURCSFramework.util.WURCSException;
import org.glycoinfo.WURCSFramework.util.WURCSFactory;
import org.glycoinfo.WURCSFramework.wurcs.array.WURCSArray;
import org.glycoinfo.rdf.InsertSparql;
//import org.glycoinfo.rdf.ResourceProcessException;
//import org.glycoinfo.rdf.ResourceProcessParent;
//import org.glycoinfo.rdf.ResourceProcessResult;
import org.glycoinfo.rdf.utils.NumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//import jp.bluetree.log.LevelType;

//public abstract class SequenceResourceProcess extends ResourceProcessParent implements GlycoSequenceResourceProcess {
//
//	private static final Log logger = LogFactory.getLog(SequenceResourceProcess.class);
//
////	@Autowired
////	GlycanProcedure glycanProcedure;
////
////	@Autowired
////	InsertSparql saccharideInsertSparql;
////
////	@Autowired
////	InsertSparql resourceEntryInsertSparql;
////
//	@Autowired
//	@Qualifier("GlycosequenceInsert")
//	InsertSparql glycoSequenceInsert;
//
//	public ResourceProcessResult processGlycoSequence(String sequence, String contributorId)
//			throws ResourceProcessException {
//		
//		if (StringUtils.isBlank(contributorId)) {
//			throw new ResourceProcessException(
//					new ResourceProcessResult("Contributor id cannot be blank", LevelType.ERROR));
//		}
//
////		try {
//
//
//		// Need a unique method to identify this GlycoSequence, as it is not validated 
//			String uniqueSequence = NumberGenerator.generateHash(sequence, new Date());
//			logger.debug("Unique Sequence:>" + uniqueSequence + "<");
//
//			glycoSequenceInsert.getSparqlEntity().setValue(Saccharide.URI, SaccharideUtil.getURI(uniqueSequence));
//			glycoSequenceInsert.getSparqlEntity().setValue(GlycoSequence.Sequence, sequence);
////			glycoSequenceInsert.getSparqlEntity().setValue(GlycoSequence.Format, DetectFormat.detect(sequence));
//
////		} catch (SparqlException e) {
////			logger.error("Sparql exception processing:>" + sequence + "<");
////			String errorMessage = null;
////			if (e.getMessage() != null && e.getMessage().length() > 0)
////				errorMessage = e.getMessage();
////			throw new ResourceProcessException(new ResourceProcessResult(errorMessage, Status.ERROR));
////		}
////
////
////		try {
////			String sequence = data.getValue(GlycanProcedure.Sequence);
////			if (sequence.contains("\n")) {
////				sequence = sequence.replaceAll("(?:\\r\\n|\\n)", "\\\\n");
////			}
////			
////			logger.debug("adding:" + data.getValue(Saccharide.PrimaryId) + " with >" + sequence + "< in " + data.getValue(GlycanProcedure.Format) + " format.");
////			glycoSequenceInsert.getSparqlEntity().setValue(Saccharide.PrimaryId,  data.getValue(Saccharide.PrimaryId));
////			glycoSequenceInsert.getSparqlEntity().setValue(GlycoSequence.Sequence, sequence);
////			glycoSequenceInsert.getSparqlEntity().setValue(GlycoSequence.Format, DetectFormat.detect(sequence));
////
////			SaccharideInsertSparql sis = new SaccharideInsertSparql();
////			sis.setSparqlEntity(glycoSequenceInsert.getSparqlEntity());
////
////			glycoSequenceInsert.getSparqlEntity().setValue(Saccharide.URI, sis.getUri());
////			
////			sparqlDAO.insert(glycoSequenceInsert);
////			insertSaccharideBean.setPrefix("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n");
////			insertSaccharideBean.setInsert();
////			
////			resourceEntryInsertSparql.getSparqlEntity().setValue(Saccharide.PrimaryId, accessionNumber);
////			resourceEntryInsertSparql.getSparqlEntity().setValue(ResourceEntry.Identifier, accessionNumber);
////			resourceEntryInsertSparql.getSparqlEntity().setValue(ResourceEntry.Database,
////					ResourceEntry.Database_Glytoucan);
////
////			resourceEntryInsertSparql.getSparqlEntity().setValue(ResourceEntry.ContributorId, contributorId);
////			resourceEntryInsertSparql.getSparqlEntity().setValue(ResourceEntry.DataSubmittedDate, new Date());
////			getSparqlDAO().insert(resourceEntryInsertSparql);
////
////			// add WURCS RDF from wurcs RDF library.  this will add the WURCS GlycoSequence class.
////			glycanProcedure.addWurcs(sparqlentity);
////
////		} catch (SparqlException e) {
////			logger.error("Sparql exception processing:>" + sequence + "<");
////			String errorMessage = null;
////			if (e.getMessage() != null && e.getMessage().length() > 0)
////				errorMessage = e.getMessage();
////			throw new ResourceProcessException(new ResourceProcessResult(errorMessage, Status.ERROR));
////		}
//		
//		return new ResourceProcessResult(sequence, LevelType.INFO);
//	}
//
//	public String validateWurcs(String sequence) throws WURCSException {
//		sequence = sequence.trim();
//		logger.debug("validating:>" + sequence + "<");
//		WURCSFactory factory;
//		factory = new WURCSFactory(sequence);
//		WURCSArray t_oArray = factory.getArray();
//		// WURCSSequence2 t_oSeq2 = factory.getSequence();
//		String wurcs = factory.getWURCS();
//		return wurcs;
//	}
//}