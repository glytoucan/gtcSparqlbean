package org.glycoinfo.rdf.glycan.relation;

import org.glycoinfo.rdf.InsertSparqlBean;
import org.glycoinfo.rdf.glycan.SaccharideInsertSparql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * <SaccharideURI> rogs:hasLinkageIsomer <SaccharideURI>
 * 
 * @author aoki
 *
 */
public class LinkageIsomerInsertSparql extends InsertSparqlBean implements LinkageIsomer {

	public static Logger logger = (Logger) LoggerFactory
			.getLogger(LinkageIsomerInsertSparql.class);
	
	public LinkageIsomerInsertSparql() {
		this.prefix = "PREFIX rogs: <http://http://www.glycoinfo.org/glyco/owl/relation#>\n";
		this.graph = "http://rdf.glytoucan.org/isomer";
	}
	
	@Override
	public String getInsert() {
//		[SchemaEntity [columns=[PrimaryId, SaccharideURI, Sequence, LinkageIsomerAccessionNumber, glycan], 
//		data={PrimaryId=G95616YE, SaccharideURI=null, 
//		Sequence=WURCS=2.0/5,7,6/[u2122h_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a2122h-1b_1-5_2*NCC/3=O][a2112h-1b_1-5]/1-2-3-4-5-4-5/a4-b1_b3-c1_c2-d1_c4-f1_d4-e1_f4-g1, 
//		LinkageIsomerAccessionNumber=http://rdf.glycoinfo.org/glycan/G95616YE, 
//		glycan=http://rdf.glycoinfo.org/glycan/G95616YE}, graph=null], SchemaEntity [columns=[PrimaryId, SaccharideURI, Sequence, LinkageIsomerAccessionNumber, glycan], data={PrimaryId=G95616YE, SaccharideURI=null, Sequence=WURCS=2.0/5,7,6/[u2122h_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a2122h-1b_1-5_2*NCC/3=O][a2112h-1b_1-5]/1-2-3-4-5-4-5/a4-b1_b3-c1_c2-d1_c4-f1_d4-e1_f4-g1, LinkageIsomerAccessionNumber=http://rdf.glycoinfo.org/glycan/G29965VW, glycan=http://rdf.glycoinfo.org/glycan/G29965VW}, graph=null]]
		
//		2015-11-12 14:44:04 DEBUG SparqlListProcessor:99 - passing onto insert:>[SchemaEntity [columns=[PrimaryId, Sequence, LinkageIsomerAccessionNumber, glycan], 
//		data={PrimaryId=G95715NY, Sequence=WURCS=2.0/4,7,6/[a2112h-1x_1-5_2*NCC/3=O][a2112h-1b_1-5][a2122h-1b_1-5_2*NCC/3=O][Aad21122h-2a_2-6_5*NCCO/3=O]/1-2-3-2-4-3-2/a3-b1_c4-d1_d3-e2_f4-g1_b?-c1_b?-f1, 
//		LinkageIsomerAccessionNumber=http://rdf.glycoinfo.org/glycan/G95715NY, glycan=http://rdf.glycoinfo.org/glycan/G95715NY}, graph=null], 
//		SchemaEntity [columns=[PrimaryId, Sequence, LinkageIsomerAccessionNumber, glycan], 
//		data={PrimaryId=G95715NY, Sequence=WURCS=2.0/4,7,6/[a2112h-1x_1-5_2*NCC/3=O][a2112h-1b_1-5][a2122h-1b_1-5_2*NCC/3=O][Aad21122h-2a_2-6_5*NCCO/3=O]/1-2-3-2-4-3-2/a3-b1_c4-d1_d3-e2_f4-g1_b?-c1_b?-f1, 
//		LinkageIsomerAccessionNumber=http://rdf.glycoinfo.org/glycan/G36094GH, glycan=http://rdf.glycoinfo.org/glycan/G36094GH}, graph=null], SchemaEntity [columns=[PrimaryId, Sequence, LinkageIsomerAccessionNumber, glycan], data={PrimaryId=G95715NY, Sequence=WURCS=2.0/4,7,6/[a2112h-1x_1-5_2*NCC/3=O][a2112h-1b_1-5][a2122h-1b_1-5_2*NCC/3=O][Aad21122h-2a_2-6_5*NCCO/3=O]/1-2-3-2-4-3-2/a3-b1_c4-d1_d3-e2_f4-g1_b?-c1_b?-f1, LinkageIsomerAccessionNumber=http://rdf.glycoinfo.org/glycan/G60844ZM, glycan=http://rdf.glycoinfo.org/glycan/G60844ZM}, graph=null]]<
		SaccharideInsertSparql sis = new SaccharideInsertSparql();
		sis.setSparqlEntity(getSparqlEntity());
		
		return "<" + sis.getUri() + "> rogs:hasLinkageIsomer <" + getSparqlEntity().getValue(LinkageIsomer.URI) + "> .";
	}
}