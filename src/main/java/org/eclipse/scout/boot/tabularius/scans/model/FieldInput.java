package org.eclipse.scout.boot.tabularius.scans.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.boot.tabularius.numbers.model.ScannedNumber;

public class FieldInput implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<ScannedNumber> numbers = new ArrayList<>();
}
