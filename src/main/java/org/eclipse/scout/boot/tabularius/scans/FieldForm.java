package org.eclipse.scout.boot.tabularius.scans;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.scout.boot.tabularius.scans.model.Field;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.AbstractTabBox;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection;
import org.eclipse.scout.rt.svg.client.svgfield.AbstractSvgField;
import org.w3c.dom.svg.SVGDocument;

public class FieldForm extends AbstractForm {

	protected Field field;
	private SVGDocument document;

	public FieldForm(Field field, SVGDocument svgDocument) {
		super(false);
		this.field = field;
		this.document = svgDocument;
		callInitializer();
	}

	public class MainBox extends AbstractGroupBox {

		@Order(10)
		public class ScannedFieldTabBox extends AbstractTabBox {

			@Override
			protected void execInitField() {
				setStatusVisible(false);
			}

			public class ScannedFieldBox extends AbstractGroupBox {

				@Override
				protected void execInitField() {
					setLabel("Scanned Field");
					setStatusVisible(false);
					setGridColumnCountHint(1);
				}

				@Order(10)
				public class DocumentSvgField extends AbstractSvgField {

					@Override
					protected void execInitField() {
						setLabelVisible(false);
						setStatusVisible(false);
						setSvgDocument(document);
					}
					
					@Override
					protected int getConfiguredGridH() {
						return 2;
					}

					@Override
					protected double getConfiguredGridWeightY() {
						return 0d;
					}
				}
			}
		}

		@Order(20)
		public class EvaluatedTabBox extends AbstractTabBox {

			@Override
			protected void execInitField() {
				setStatusVisible(false);
			}

			public class EvaluatedFieldBox extends AbstractGroupBox {
				@Override
				protected void execInitField() {
					setLabel("Evaluated Field");
					setStatusVisible(false);
					setGridColumnCountHint(1);
				}

				public class CharacterBox extends AbstractSequenceBox {

					@Override
					protected void execInitField() {
						setLabelVisible(false);
						setAutoCheckFromTo(false);
					}

					@Override
					protected void injectFieldsInternal(OrderedCollection<IFormField> fields) {
						AtomicInteger counter = new AtomicInteger(0);
						field.input.numbers.forEach(n -> {
							AbstractStringField stringField = new AbstractStringField() {
								@Override
								public String classId() {
									return UUID.randomUUID().toString();
								}
							};
							stringField.setValue(String.valueOf(field.evaluation.eval.get(n).character));
							stringField.setEnabled(false);
							stringField.setFocusable(false);

							stringField.setLabelVisible(false);
							stringField.setStatusVisible(false);
							stringField.setOrder(counter.incrementAndGet());
							fields.addLast(stringField);
						});

						super.injectFieldsInternal(fields);
					}
				}
			}
		}
	}
}