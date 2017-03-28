package org.eclipse.scout.boot.tabularius.scans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.scout.boot.tabularius.scans.DocumentForm.MainBox.MainVerticalSplitBox.LeftTabBox.DocumentTab.DocumentSvgField;
import org.eclipse.scout.boot.tabularius.scans.DocumentForm.MainBox.MainVerticalSplitBox.LeftTabBox.DocumentTab.FieldsTableField;
import org.eclipse.scout.boot.tabularius.scans.DocumentForm.MainBox.MainVerticalSplitBox.LeftTabBox.SvgTab.SvgSourceField;
import org.eclipse.scout.boot.tabularius.scans.model.Document;
import org.eclipse.scout.boot.tabularius.scans.model.Field;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractObjectColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.ISplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.AbstractTabBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.form.fields.wrappedform.AbstractWrappedFormField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.svg.client.SVGUtility;
import org.eclipse.scout.rt.svg.client.svgfield.AbstractSvgField;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public class DocumentForm extends AbstractForm {

	protected Document document = Document.generate();

	protected void execPostLoad() {
		getFieldByClass(FieldsTableField.class).getTable().selectFirstRow();
	}

	@Override
	protected void execInitForm() {
		setTitle("Document");
	}

	public SvgSourceField getSvgSourceField() {
		return getFieldByClass(SvgSourceField.class);
	}

	public DocumentSvgField getDocumentSvgField() {
		return getFieldByClass(DocumentSvgField.class);
	}

	public class MainBox extends AbstractGroupBox {

		public class MainVerticalSplitBox extends AbstractSplitBox {

			@Override
			protected void execInitField() {
				setSplitterPositionType(ISplitBox.SPLITTER_POSITION_TYPE_ABSOLUTE_FIRST);
				setSplitterPosition(450);
			}

			@Order(10)
			public class LeftTabBox extends AbstractTabBox {

				@Override
				protected void execInitField() {
					setStatusVisible(false);
				}

				@Order(10)
				public class DocumentTab extends AbstractGroupBox {

					@Override
					protected void execInitField() {
						setStatusVisible(false);
						setGridColumnCountHint(1);
					}

					@Override
					protected String getConfiguredLabel() {
						return "Document";
					}

					@Override
					protected double getConfiguredGridWeightY() {
						return 1d;
					}

					@Order(10)
					public class DocumentSvgField extends AbstractSvgField {

						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setStatusVisible(false);
						}

						@Override
						protected double getConfiguredGridWeightY() {
							return 1d;
						}

						@Override
						protected void execAppLinkAction(String ref) {
							if (ref.startsWith("fieldMarker")) {
								DocumentForm.this.getFieldByClass(FieldsTableField.class).getTable()
										.selectRow(Integer.valueOf(ref.replace("fieldMarker", "")));
							}
						}
					}

					@Order(20)
					public class FieldsTableField extends AbstractTableField<FieldsTableField.Table> {

						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setStatusVisible(false);

							document.fields.forEach(f -> {
								ITableRow row = getTable().createRow();
								row.setCellValue(0, f);
								row.setCellValue(1, f.spec.name);
								row.setCellValue(2, f.spec.pattern);
								getTable().addRow(row);
							});
						}

						@Override
						protected int getConfiguredGridH() {
							return 5;
						}

						@Override
						protected double getConfiguredGridWeightY() {
							return 0d;
						}

						public class Table extends AbstractTable {

							@Override
							protected void execInitTable() {
								setMultiSelect(false);
							}

							@Override
							protected void execRowsSelected(List<? extends ITableRow> rows) {
								clearErrorStatus();
								try {
									Field field = (Field) rows.get(0).getCell(0).getValue();
									String svg = DocumentForm.this.getFieldByClass(SvgSourceField.class).getValue();
									SVGDocument doc = parseDocument(svg);
									DocumentForm.this.getFieldByClass(DocumentSvgField.class)
											.setSvgDocument(transformLeft(doc, field));
									DocumentForm.this.getFieldByClass(RightGroupBox.class).execDisplayField(field,
											transformRight(doc, field));
								} catch (Exception e) {
									e.printStackTrace();
									addErrorStatus(e.getMessage());
								}
							}

							@Order(0)
							public class FieldBeanColumn extends AbstractObjectColumn {

							}

							@Order(10)
							public class FieldNameColumn extends AbstractStringColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return "Field Name";
								}

								@Override
								protected int getConfiguredWidth() {
									return 200;
								}
							}

							@Order(20)
							public class PatternColumn extends AbstractStringColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return "Pattern";
								}

								@Override
								protected int getConfiguredWidth() {
									return 200;
								}
							}
						}
					}
				}

				@Order(20)
				public class SvgTab extends AbstractGroupBox {

					@Override
					protected String getConfiguredLabel() {
						return "Svg Source";
					}

					@Override
					protected double getConfiguredGridWeightY() {
						return 1d;
					}

					@Order(20)
					public class SvgSourceField extends AbstractStringField {

						@Override
						protected double getConfiguredGridWeightY() {
							return 1d;
						}

						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setMultilineText(true);
							setMaxLength(1000000);

							clearErrorStatus();
							try {
								SVGDocument doc = generateDocument();
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								SVGUtility.writeSVGDocument(doc, out, StandardCharsets.UTF_8.toString());
								setValue(prettyPrint(new String(out.toByteArray())));
							} catch (Exception e) {
								e.printStackTrace();
								addErrorStatus(e.getMessage());
							}
						}
					}
				}
			}

			@Order(20)
			public class RightGroupBox extends AbstractGroupBox {

				@Override
				protected void execInitField() {
					setBorderVisible(false);
				}

				@Order(10)
				public class GroupFormField extends AbstractWrappedFormField<FieldForm> {

				}

				public void execDisplayField(Field field, SVGDocument svgDocument) {
					clearErrorStatus();
					try {
						FieldForm form = new FieldForm(field, svgDocument);
						DocumentForm.this.getFieldByClass(GroupFormField.class).setInnerForm(form);
					} catch (Exception e) {
						e.printStackTrace();
						addErrorStatus(e.getMessage());
					}
				}
			}
		}

		protected SVGDocument loadSvgDocument() throws IOException {
			return SVGUtility.readSVGDocument(this.getClass().getResourceAsStream("/WebContent/res/document.svg"));
		}

		protected SVGDocument generateDocument() throws IOException {
			final int fieldOffsetY = 600;
			final int fieldHeight = 100;
			final int fieldSpaceY = 33;

			final int dimension = 28;
			final int space = 5;

			final int labelOffsetX = 65;
			final int labelWidth = 160;

			final int digitOffsetY = 36;
			final int digitOffsetX = 240;

			SVGDocument doc = loadSvgDocument();

			Deque<String> idStack = new ArrayDeque<String>();
			idStack.add("background");

			AtomicInteger fieldCounter = new AtomicInteger(0);
			document.fields.stream().forEach(f -> {
				int fieldNumber = fieldCounter.getAndIncrement();

				int fieldBaseY = fieldOffsetY + fieldNumber * (fieldHeight + fieldSpaceY);

				Element textbox = doc.createElementNS("http://www.w3.org/2000/svg", "rect");
				textbox.setAttribute("id", UUID.randomUUID().toString());
				textbox.setAttribute("x", "" + labelOffsetX);
				textbox.setAttribute("y", "" + (fieldBaseY + digitOffsetY));
				textbox.setAttribute("width", "" + labelWidth);
				textbox.setAttribute("height", "" + dimension);
				textbox.setAttribute("fill", "rgb(242,229,0)");
				textbox.setAttribute("opacity", "0.2");

				Element text = doc.createElementNS("http://www.w3.org/2000/svg", "text");
				text.setAttribute("id", UUID.randomUUID().toString());
				text.setAttribute("x", "" + labelOffsetX);
				text.setAttribute("y", "" + (fieldBaseY + digitOffsetY - 100));
				text.setAttribute("font-family", "monospace");
				text.setAttribute("font-size", "14");
				text.setAttribute("opacity", "0.8");
				text.setAttribute("dominant-baseline", "hanging");
				text.setTextContent(f.spec.name.toUpperCase() + ":");

				doc.getElementById("background").getParentNode().appendChild(textbox);
				idStack.add(textbox.getAttribute("id"));
				doc.getElementById("background").getParentNode().appendChild(text);
				idStack.add(text.getAttribute("id"));

				AtomicInteger digitCounter = new AtomicInteger(0);
				f.input.numbers.forEach(n -> {
					int digitNumber = digitCounter.getAndIncrement();

					int digitBaseX = digitOffsetX + digitNumber * (dimension + space);
					int digitBaseY = fieldBaseY + digitOffsetY;

					Element box = doc.createElementNS("http://www.w3.org/2000/svg", "rect");
					box.setAttribute("id", "box_" + fieldNumber + "_" + digitNumber);
					box.setAttribute("x", "" + digitBaseX);
					box.setAttribute("y", "" + digitBaseY);
					box.setAttribute("width", "" + dimension);
					box.setAttribute("height", "" + dimension);
					box.setAttribute("fill", "rgb(242,229,0)");
					box.setAttribute("opacity", "0.2");

					Element number = doc.createElementNS("http://www.w3.org/2000/svg", "image");
					number.setAttribute("id", "number_" + fieldNumber + "_" + digitNumber);
					number.setAttribute("x", "" + (digitBaseX + 2));
					number.setAttribute("y", "" + (digitBaseY + 2));
					number.setAttribute("width", "" + (dimension - 4));
					number.setAttribute("height", "" + (dimension - 4));
					number.setAttribute("xlink:href", "/res/numbers" + n.path);
					number.setAttribute("xlink:type", "simple");
					number.setAttribute("xlink:actuate", "onLoad");
					number.setAttribute("preserveAspectRatio", "xMidYMid meet");
					number.setAttribute("show", "embed");

					doc.getElementById("background").getParentNode().appendChild(box);
					idStack.add(box.getAttribute("id"));

					doc.getElementById("background").getParentNode().appendChild(number);
					idStack.add(number.getAttribute("id"));
				});
			});

			Element filter = doc.createElementNS("http://www.w3.org/2000/svg", "filter");
			filter.setAttribute("id", "blendit");
			Element background = doc.getElementById("background");
			background.getParentNode().appendChild(filter);

			final AtomicInteger idStackCounter1 = new AtomicInteger(1);
			idStack.forEach(id -> {
				int idStackNumber = idStackCounter1.getAndIncrement();
				Element feImage = doc.createElementNS("http://www.w3.org/2000/svg", "feImage");
				feImage.setAttribute("xlink:href", "#" + id);
				feImage.setAttribute("result", "" + idStackNumber);
				feImage.setAttribute("x", "0");
				feImage.setAttribute("y", "0");

				Element filterElement = doc.getElementById("blendit");
				filterElement.appendChild(feImage);
			});

			final AtomicInteger idStackCounter2 = new AtomicInteger(1);
			idStack.forEach(id -> {
				int idStackNumber = idStackCounter2.getAndIncrement();
				Element feBlend = doc.createElementNS("http://www.w3.org/2000/svg", "feBlend");
				StringBuilder before = new StringBuilder();
				for (int i = 1; i <= idStackNumber; i++) {
					if (before.length() > 0) {
						before.append("_");
					}
					before.append("" + i);
				}
				feBlend.setAttribute("mode", "multiply");
				feBlend.setAttribute("in", "" + before.toString());
				feBlend.setAttribute("in2", "" + (idStackNumber + 1));
				feBlend.setAttribute("result", before.toString() + "_" + (idStackNumber + 1));
				Element filterElement = doc.getElementById("blendit");
				filterElement.appendChild(feBlend);
			});
			return doc;
		}

		protected SVGDocument transformLeft(SVGDocument input, Field field) throws IOException {
			SVGDocument doc = (SVGDocument) input.cloneNode(true);

			AtomicInteger counter = new AtomicInteger(0);
			document.fields.forEach(f -> {
				int number = counter.getAndIncrement();

				int height = 100;
				int space = 33;

				int base = 600 + number * (height + space);

				Element fieldMarker = doc.createElementNS("http://www.w3.org/2000/svg", "polygon");
				fieldMarker.setAttribute("points", "-10000," + base + " -10000," + (base + height) + " 10000,"
						+ (base + height) + " 10000," + base);
				fieldMarker.setAttribute("id", "fieldMarker" + number);
				fieldMarker.setAttribute("data-ref", "fieldMarker" + number);
				fieldMarker.setAttribute("class", "app-link");
				fieldMarker.setAttribute("fill", "#014786");
				fieldMarker.setAttribute("fill-opacity", "0.0");

				Element set = doc.createElementNS("http://www.w3.org/2000/svg", "set");
				set.setAttribute("attributeName", "fill-opacity");
				set.setAttribute("to", "0.1");
				set.setAttribute("begin", "fieldMarker" + number + ".mouseover");
				set.setAttribute("end", "fieldMarker" + number + ".mouseout");
				fieldMarker.appendChild(set);

				Element documentElement = doc.getElementById("document");
				documentElement.getParentNode().appendChild(fieldMarker);
			});

			return doc;
		}

		protected SVGDocument transformRight(SVGDocument input, Field field) throws IOException {
			SVGDocument doc = (SVGDocument) input.cloneNode(true);

			int height = 100;
			int space = 33;

			int base = 605 + document.fields.indexOf(field) * (height + space);

			doc.getElementById("svg").setAttribute("viewBox", "-10 " + base + " 861 " + (base + height));
			doc.getElementById("svg").setAttribute("preserveAspectRatio", "xMinYMin slice");
			return doc;
		}

		protected SVGDocument parseDocument(String svgData) throws IOException {
			if (StringUtility.isNullOrEmpty(svgData)) {
				return null;
			}
			return SVGUtility
					.readSVGDocument(new ByteArrayInputStream(svgData.getBytes(StandardCharsets.UTF_8.name())));
		}

		protected String prettyPrint(String xml) {
			return xml.replace(">\n", "PRESERVE").replace(">", ">\n").replace("PRESERVE", ">\n");
		}
	}
}