package org.eclipse.scout.boot.tabularius.scans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.scout.boot.tabularius.scans.DocumentForm.MainBox.MainVerticalSplitBox.LeftTabBox.DocumentTab.DocumentSvgField;
import org.eclipse.scout.boot.tabularius.scans.DocumentForm.MainBox.MainVerticalSplitBox.LeftTabBox.SvgTab.SvgSourceField;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.labelfield.AbstractLabelField;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.ISplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tabbox.AbstractTabBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.svg.client.SVGUtility;
import org.eclipse.scout.rt.svg.client.svgfield.AbstractSvgField;
import org.w3c.dom.svg.SVGCircleElement;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLength;

public class DocumentForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return "Document";
	}

	public SvgSourceField getSvgSourceField() {
		return getFieldByClass(SvgSourceField.class);
	}

	public DocumentSvgField getDocumentSvgField() {
		return getFieldByClass(DocumentSvgField.class);
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
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
						protected Class<? extends IValueField<?>> getConfiguredMasterField() {
							return DocumentForm.MainBox.MainVerticalSplitBox.LeftTabBox.SvgTab.SvgSourceField.class;
						}

						@Override
						protected void execChangedMasterValue(Object newMasterValue) {

							getSvgSourceField().clearErrorStatus();

							try {
								getDocumentSvgField().setSvgDocument(parseDocument((String) newMasterValue));
							} catch (Exception e) {
								e.printStackTrace();
								getSvgSourceField().addErrorStatus(e.getMessage());
							}
						}

						private SVGDocument liveUpdateCircle(SVGDocument doc, String id) {
							SVGCircleElement circle = (SVGCircleElement) doc.getElementById(id);
							SVGLength x = (SVGLength) circle.getCx().getBaseVal();
							x.setValue((x.getValue() + 40) % 400);
							return doc;
						}

						@Override
						protected void execAppLinkAction(String ref) {
							if ("circle2".equals(ref)) {
								SVGDocument doc = (SVGDocument) getSvgDocument().cloneNode(true);
								setSvgDocument(liveUpdateCircle(doc, "circle2"));
							} else {
								MessageBoxes.createOk().withHeader(TEXTS.get("SVGLink"))
										.withBody(TEXTS.get("SVGLinkMessage")).withBody(ref).show();
							}
						}
					}

					@Order(20)
					public class FieldsTableField extends AbstractTableField<FieldsTableField.Table> {
						
						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setStatusVisible(false);
						}

						@Override
						protected int getConfiguredGridH() {
							return 4;
						}
						
						public class Table extends AbstractTable {

							@Order(1000)
							public class FieldNameColumn extends AbstractStringColumn {
								
								@Override
								protected String getConfiguredHeaderText() {
									return "Field Name";
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
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
						protected void execInitField() {
							setLabelVisible(false);
							setMultilineText(true);
							setMaxLength(1000000);
							
							try {
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								SVGUtility.writeSVGDocument(loadDocument(), out, StandardCharsets.UTF_8.toString());
								setValue(new String(out.toByteArray()));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						@Override
						protected double getConfiguredGridWeightY() {
							return 1d;
						}
					}
				}
			}

			@Order(20)
			public class RightGroupBox extends AbstractGroupBox {

				public class HelloField extends AbstractLabelField {

					@Override
					protected String getConfiguredLabel() {
						return "Hello";
					}
				}
			}
		}
		
	    protected SVGDocument loadDocument() throws IOException {
	        return SVGUtility.readSVGDocument(this.getClass().getResourceAsStream("/WebContent/res/document.svg"));
	      }

	    protected SVGDocument parseDocument(String svgData) throws IOException {
			if (StringUtility.isNullOrEmpty(svgData)) {
				return null;
			}
			return SVGUtility.readSVGDocument(new ByteArrayInputStream(svgData.getBytes(StandardCharsets.UTF_8.name())));
		}
	}

	public class PageFormHandler extends AbstractFormHandler {
	}
}