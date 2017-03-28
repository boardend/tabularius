package org.eclipse.scout.boot.tabularius.anagnostes;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.eclipse.scout.boot.tabularius.TabulariusDesktop;
import org.eclipse.scout.boot.tabularius.anagnostes.model.Eval;
import org.eclipse.scout.boot.tabularius.anagnostes.util.LeNetMnistAccessor;
import org.eclipse.scout.boot.tabularius.numbers.model.Contributor;
import org.eclipse.scout.boot.tabularius.numbers.model.ScannedNumber;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;

public class AnagnostesForm extends AbstractForm {

	public class MainBox extends AbstractGroupBox {

		protected String evalDemo() {
			final StringBuilder sb = new StringBuilder();

			final Contributor contributor = getRandomContributor();
			sb.append("Contributor: ");
			sb.append(contributor.name);
			sb.append("\n");
			
			for (int i = 0; i < 10; i++) {
				int n = ThreadLocalRandom.current().nextInt(0, 10);
				Eval number = getRandomNumber(contributor, n);
				
				sb.append(n);
				sb.append(" / ");
				sb.append(number.character);
				sb.append(" (");
				sb.append(new DecimalFormat("0.00").format(number.confidence));
				sb.append(")");
				sb.append("\n");
			}

			return sb.toString();
		}

		protected Contributor getRandomContributor() {
			final TabulariusDesktop desktop = ((TabulariusDesktop) getDesktop());
			final int i = new Random().nextInt(desktop.getContributors().size());
			return desktop.getContributors().toArray(new Contributor[desktop.getContributors().size()])[i];
		}
		
		protected Eval getRandomNumber(Contributor contributor, int digit) {
			final Set<ScannedNumber> possibleNumbers = contributor.getNumbers().stream()
					.filter(n -> n.digit == digit)
					.collect(Collectors.toSet());
			final int i = new Random().nextInt(possibleNumbers.size());
			final ScannedNumber number = possibleNumbers.toArray(new ScannedNumber[possibleNumbers.size()])[i];
			return BEANS.get(LeNetMnistAccessor.class).eval("WebContent/res/numbers" + number.path);
		}

		public class TestField extends AbstractStringField {

			@Override
			protected void execInitField() {
				setLabel("Anagnostes");
				setMultilineText(true);
			}

			@Override
			protected double getConfiguredGridWeightY() {
				return 1d;
			}
		}

		public class EvalButton extends AbstractButton {

			@Override
			protected void execInitField() {
				setLabel("Eval");
			}

			@Override
			protected void execClickAction() {
				AnagnostesForm.this.getFieldByClass(TestField.class).setValue(evalDemo());
			}
		}
	}
}
