package org.glycoinfo.rdf.service.impl;

import org.glycoinfo.convert.GlyConvert;
import org.glycoinfo.convert.GlyConvertConfig;
import org.glycoinfo.rdf.DeleteSparql;
import org.glycoinfo.rdf.InsertSparql;
import org.glycoinfo.rdf.SelectSparql;
import org.glycoinfo.rdf.SelectSparqlBean;
import org.glycoinfo.rdf.SparqlException;
import org.glycoinfo.rdf.dao.SparqlEntity;
import org.glycoinfo.rdf.glycan.DatabaseSelectSparql;
import org.glycoinfo.rdf.glycan.GlycoSequence;
import org.glycoinfo.rdf.glycan.GlycoSequenceInsertSparql;
import org.glycoinfo.rdf.glycan.GlycoSequenceSelectSparql;
import org.glycoinfo.rdf.glycan.ResourceEntryDeleteSparql;
import org.glycoinfo.rdf.glycan.ResourceEntryInsertSparql;
import org.glycoinfo.rdf.glycan.Saccharide;
import org.glycoinfo.rdf.glycan.SaccharideInsertSparql;
import org.glycoinfo.rdf.glycan.SaccharideSelectSparql;
import org.glycoinfo.rdf.glycan.mass.MassInsertSparql;
//import org.glycoinfo.rdf.glycan.msdb.MSInsertSparql;
import org.glycoinfo.rdf.glycan.wurcs.GlycoSequenceResourceEntryContributorSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.MonosaccharideSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.MotifSequenceSelectSparql;
import org.glycoinfo.rdf.glycan.wurcs.SubstructureSearchSparql;
import org.glycoinfo.rdf.glycan.wurcs.WurcsRDFInsertSparql;
import org.glycoinfo.rdf.glycan.wurcs.WurcsRDFMSInsertSparql;
import org.glycoinfo.rdf.service.GlycanProcedure;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
//@Import( {ContributorProcedureConfig.class, GlyConvertConfig.class} )
@Import( {ContributorProcedureConfig.class} )
public class GlycanProcedureConfig implements GraphConfig {

	@Bean(name="SaccharideInsert")
	InsertSparql saccharideInsertSparql() {
		SaccharideInsertSparql sis = new SaccharideInsertSparql();
		sis.setGraph(graph + "/core");
		return sis;
	}
	
  @Bean(name="ResourceEntryDelete")
  DeleteSparql getResourceEntryDeleteSparql() {
    ResourceEntryDeleteSparql resourceEntryDeleteSparql = new ResourceEntryDeleteSparql();
    SparqlEntity se = new SparqlEntity();
    se.setValue(ResourceEntryInsertSparql.Database, "glytoucan");
    resourceEntryDeleteSparql.setSparqlEntity(se);
    resourceEntryDeleteSparql.setGraph(graph + "/core");
    return resourceEntryDeleteSparql;
	}
	
	@Bean(name="ResourceEntryInsert")
	InsertSparql getResourceEntryInsertSparql() {
		ResourceEntryInsertSparql resourceEntryInsertSparql = new ResourceEntryInsertSparql();
		SparqlEntity se = new SparqlEntity();
		se.setValue(ResourceEntryInsertSparql.Database, "glytoucan");
		resourceEntryInsertSparql.setSparqlEntity(se);
		resourceEntryInsertSparql.setGraph(graph + "/core");
		return resourceEntryInsertSparql;
	}

	@Bean(name="glycoSequenceContributorSelectSparql")
	SelectSparql glycoSequenceContributorSelectSparql() {
		GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
		sb.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>\nFROM <http://rdf.glytoucan.org/sequence/glycoct>\nFROM <http://rdf.glytoucan.org/users>\n");
		return sb;
	}
	
	@Bean(name="glycoSequenceResourceEntryContributorArchivedSelectSparql")
	SelectSparql glycoSequenceResourceEntryContributorArchivedSelectSparql() {
		GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
		sb.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/archive>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>\nFROM <http://rdf.glytoucan.org/sequence/glycoct>\nFROM <http://rdf.glytoucan.org/users>\n");
		return sb;
	}

	
	
	@Bean(name="listAllIdSelectSparql")
	SelectSparql listAllIdSelectSparql() throws SparqlException {
	  GlycoSequenceSelectSparql gsss = new GlycoSequenceSelectSparql();
	  SparqlEntity se = new SparqlEntity();
	  se.setValue(GlycoSequence.Format, GlyConvert.WURCS);
	  gsss.setSparqlEntity(se);
//	  SelectSparql ss = new SelectSparqlBean();
//	  ss.setPrefix("PREFIX glycan: <http://purl.jp/bio/12/glyco/glycan#>\n" + 
//	  "PREFIX glytoucan: <http://www.glytoucan.org/glyco/owl/glytoucan#>\n");
//	  ss.setSelect("distinct ?" + Saccharide.PrimaryId);
//	  ss.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/core>");
//	  ss.setWhere("?s a glycan:saccharide .\n" + 
//	  "?s glytoucan:has_primary_id ?" + Saccharide.PrimaryId + " .\n");
	  return gsss;
	}
	
  @Bean(name="listAllGlycoSequenceContributorSelectSparql")
  SelectSparql listAllGlycoSequenceContributorSelectSparql() {
    GlycoSequenceResourceEntryContributorSelectSparql sb = new GlycoSequenceResourceEntryContributorSelectSparql();
    sb.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>\nFROM <http://rdf.glytoucan.org/mass>\nFROM <" + graph + "/users>\nFROM <http://rdf.glytoucan.org/sequence/glycoct>\nFROM <http://rdf.glytoucan.org/users>\n");
    return sb;
  }
	
	@Bean(name="massInsertSparql")
	MassInsertSparql massInsertSparql() {
		MassInsertSparql mass = new MassInsertSparql();
		mass.setGraphBase(graph);
		return mass;
	}
	
	@Bean(name="wurcsRDFInsertSparql")
	WurcsRDFInsertSparql wurcsRDFInsertSparql() {
		WurcsRDFInsertSparql wrdf = new WurcsRDFInsertSparql();
		wrdf.setSparqlEntity(new SparqlEntity());
		wrdf.setGraph("http://rdf.glytoucan.org/sequence/wurcs");
		return wrdf;
	}
	
	@Bean(name="wurcsRDFMSInsertSparql")
	WurcsRDFMSInsertSparql wurcsRDFMSInsertSparql() {
		WurcsRDFMSInsertSparql wrdf = new WurcsRDFMSInsertSparql();
		wrdf.setSparqlEntity(new SparqlEntity());
		wrdf.setGraph("http://rdf.glytoucan.org/wurcs/ms");
		return wrdf;
	}
	
	@Bean(name="glycoSequenceInsert")
	InsertSparql glycoSequenceInsert() {
		GlycoSequenceInsertSparql gsis = new GlycoSequenceInsertSparql();
		gsis.setSparqlEntity(new SparqlEntity());
		gsis.setGraphBase(graph);
		return gsis;
	}
	
	@Bean
	SelectSparql motifSequenceSelectSparql() {
		MotifSequenceSelectSparql select = new MotifSequenceSelectSparql();
//		select.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/sequence/wurcs>");
		return select;
	}
	
	@Bean
	SaccharideSelectSparql saccharideSelectSparql() {
		SaccharideSelectSparql select = new SaccharideSelectSparql();
		select.setFrom("FROM <http://rdf.glytoucan.org/core>\nFROM <http://rdf.glytoucan.org/core>");
		return select;
	}
	
	@Bean
	MonosaccharideSelectSparql monosaccharideSelectSparql() {
		MonosaccharideSelectSparql sb = new MonosaccharideSelectSparql();
		return sb;
	}
	
//	@Bean
//	MSdbClient msdbClient() {
//		return new MSdbClient();
//	}
	
//	@Bean
//	public MSInsertSparql msInsertSparql() {
//		MSInsertSparql wrss = new MSInsertSparql();
//		wrss.setGraph("http://rdf.glytoucan.org/msdb/monosaccharide");
//		return wrss;
//	}
	
	@Bean
	GlycanProcedure glycanProcedure() {
		org.glycoinfo.rdf.service.impl.GlycanProcedure glycanProc = new org.glycoinfo.rdf.service.impl.GlycanProcedure();
		glycanProc.setBatch(true);
		return glycanProc; 
	}

	@Bean(name="substructureSearchSparql")
	SelectSparql substructureSearchSparql() {
		SubstructureSearchSparql sss = new SubstructureSearchSparql();
		sss.setGraphtarget("<http://rdf.glytoucan.org/sequence/wurcs>");
		sss.setGraphms("<http://rdf.glytoucan.org/wurcs/ms>");
		return sss;
	}
	
	@Bean
	public DatabaseSelectSparql databaseSelectSparql() {
		DatabaseSelectSparql dss = new DatabaseSelectSparql();
//		dss.setFrom();
		return dss;
	}
	
	@Bean(name="glycoSequenceSelectSparql")
	public GlycoSequenceSelectSparql glycoSequenceSelectSparql() {
	  return new GlycoSequenceSelectSparql();
  }
}