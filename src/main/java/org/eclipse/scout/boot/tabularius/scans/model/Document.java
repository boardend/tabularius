package org.eclipse.scout.boot.tabularius.scans.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.eclipse.scout.boot.tabularius.TabulariusDesktop;
import org.eclipse.scout.boot.tabularius.anagnostes.model.Eval;
import org.eclipse.scout.boot.tabularius.anagnostes.util.LeNetMnistAccessor;
import org.eclipse.scout.boot.tabularius.numbers.model.Contributor;
import org.eclipse.scout.boot.tabularius.numbers.model.ScannedNumber;
import org.eclipse.scout.rt.platform.BEANS;

public class Document implements Serializable {
	private static final long serialVersionUID = 1L;

	protected static List<FieldSpec> FIELD_SPECS = null;
	{
		Map<String, String> fields = new HashMap<>();
		fields.put("Credit card", "#### #### #### ####");
		fields.put("Telephone", "+## ## ### ## ##");
		fields.put("Date of birth", "##.##.####");

		FIELD_SPECS = fields.entrySet().stream().map(e -> {
			FieldSpec spec = new FieldSpec();
			spec.name = e.getKey();
			spec.pattern = e.getValue();
			return spec;
		}).collect(Collectors.toList());
	}

	public String name;
	public List<Field> fields = new ArrayList<>();

	public static Document generate() {
		Document document = new Document();
		document.name = UUID.randomUUID().toString();
		final Contributor contributor = getRandomContributor();
		FIELD_SPECS.forEach(s -> {
			Field field = new Field();
			field.spec = s;
			
			field.input = new FieldInput();
			s.pattern.chars().mapToObj(c -> (char) c).filter(c -> ('#' == c)).forEach(c -> {
				field.input.numbers.add(getRandomScannedNumber(contributor));
			});
			
			field.evaluation = new FieldEval();
			field.input.numbers.forEach(n -> {
				field.evaluation.eval.put(n, eval(n));
			});
			
			field.correction = new FieldCorrection();
			
			document.fields.add(field);
		});
		return document;
	}

	protected static Contributor getRandomContributor() {
		final TabulariusDesktop desktop = (TabulariusDesktop) TabulariusDesktop.CURRENT.get();
		final int i = new Random().nextInt(desktop.getContributors().size());
		return desktop.getContributors().toArray(new Contributor[desktop.getContributors().size()])[i];
	}

	protected static ScannedNumber getScannedNumber(Contributor contributor, int digit) {
		final Set<ScannedNumber> possibleNumbers = contributor.getNumbers().stream().filter(n -> n.digit == digit)
				.collect(Collectors.toSet());
		final int i = new Random().nextInt(possibleNumbers.size());
		return possibleNumbers.toArray(new ScannedNumber[possibleNumbers.size()])[i];
	}

	protected static ScannedNumber getRandomScannedNumber(Contributor contributor) {
		return getScannedNumber(contributor, ThreadLocalRandom.current().nextInt(0, 10));
	}

	protected static Eval eval(ScannedNumber number) {
		return BEANS.get(LeNetMnistAccessor.class).eval("WebContent/res/numbers" + number.path);
	}
}