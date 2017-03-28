package org.eclipse.scout.boot.tabularius.scans.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scout.boot.tabularius.numbers.model.ScannedNumber;

public class FieldCorrection implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public Map<ScannedNumber, Short> correction = new HashMap<>();
}