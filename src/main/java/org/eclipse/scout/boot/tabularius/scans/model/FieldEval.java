package org.eclipse.scout.boot.tabularius.scans.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scout.boot.tabularius.anagnostes.model.Eval;
import org.eclipse.scout.boot.tabularius.numbers.model.ScannedNumber;

public class FieldEval implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public Map<ScannedNumber, Eval> eval = new HashMap<>();
}