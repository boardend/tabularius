package org.eclipse.scout.boot.tabularius.numbers.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Contributor implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String name;
	
	public short id;
	public String country;
	public short age;
	public char sex;
	
	public Set<ScannedNumber> numbers = new HashSet<>();
	
	public Set<ScannedNumber> getNumbers() {
		return numbers;
	}
}
