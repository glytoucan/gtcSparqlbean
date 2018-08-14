package org.glycoinfo.rdf.glycan;

import org.glycoinfo.rdf.ClassLabel;

public interface GlycoSequence extends ClassLabel {
	public static final String Format = "GlycoSequenceFormat";
	public static final String Sequence = "Sequence";
	public static final String Label = "Label";
	public static final String URI = "GlycoSequenceURI";
	public static final String AccessionNumber = Saccharide.PrimaryId;
	public static final String SaccharideURI = Saccharide.URI;
	public static final String GlycanSequenceURI = "GlycanSequenceURI";
	public static final String IdentifiersToIgnore = "GlycanSequenceIdentifiersToIgnore";
	public static final String FilterMass = "GlycanSequenceFilterMass";

}