package org.eclipse.scout.boot.tabularius.scans.model;

import java.io.Serializable;

public class Field implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public FieldSpec spec;
	public FieldInput input;
	public FieldEval evaluation;
	public FieldCorrection correction;
}