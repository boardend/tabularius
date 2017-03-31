package org.eclipse.scout.boot.tabularius.numbers;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.scout.boot.tabularius.TabulariusDesktop;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.FilterSplitBox.ContributorFilterBox.ContributorHeaderBox.ContributorStatusField;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.FilterSplitBox.ContributorFilterBox.ContributorTableField;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.FilterSplitBox.ContributorFilterBox.ContributorTableField.ContributorTable;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.FilterSplitBox.DigitFilterBox.DigitHeaderBox.DigitStatusField;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.FilterSplitBox.DigitFilterBox.DigitTableField;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.FilterSplitBox.DigitFilterBox.DigitTableField.DigitTable;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.NumbersBox.NumbersTableField;
import org.eclipse.scout.boot.tabularius.numbers.NumbersForm.MainBox.MainVerticalSplitBox.NumbersBox.NumbersTableField.NumbersTable;
import org.eclipse.scout.boot.tabularius.numbers.model.Contributor;
import org.eclipse.scout.boot.tabularius.numbers.model.ScannedNumber;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.TableEvent;
import org.eclipse.scout.rt.client.ui.basic.table.TableListener;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractObjectColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.labelfield.AbstractLabelField;
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.ISplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.html.HTML;

public class NumbersForm extends AbstractForm {

	private Stream<Contributor> getContributors() {
		TabulariusDesktop desktop = ((TabulariusDesktop) getDesktop());
		return desktop.getContributors().stream();
	}

	private Stream<ScannedNumber> getAllScannedNumber() {
		return getContributors().map(Contributor::getNumbers).flatMap(Collection::stream);
	}

	public class MainBox extends AbstractGroupBox {

		public class MainVerticalSplitBox extends AbstractSplitBox {

			@Override
			protected void execInitField() {
				setSplitterPositionType(ISplitBox.SPLITTER_POSITION_TYPE_ABSOLUTE_FIRST);
				setSplitterPosition(450);
			}

			@Order(1)
			public class FilterSplitBox extends AbstractSplitBox {

				@Override
				protected void execInitField() {
					setSplitHorizontal(false);
					setSplitterPosition(0.66);
				}

				@Order(1)
				public class ContributorFilterBox extends AbstractGroupBox {

					@Override
					protected void execInitField() {
						setGridColumnCountHint(1);
					}

					@Order(1)
					public class ContributorHeaderBox extends AbstractSequenceBox {

						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setStatusVisible(false);
						}

						@Order(1)
						public class ContributorTitleField extends AbstractLabelField {
							@Override
							protected void execInitField() {
								setLabelVisible(false);
								setStatusVisible(false);
								setHtmlEnabled(true);
								setValue(HTML.bold("Filter by \"Contributor\"").toHtml());
							}
						}

						@Order(2)
						public class ContributorStatusField extends AbstractLabelField {
							@Override
							protected void execInitField() {
								setLabelVisible(false);
								setStatusVisible(false);
								setHtmlEnabled(true);
							}

							public void execUpdateValue() {
								ContributorTable table = NumbersForm.this.getFieldByClass(ContributorTableField.class)
										.getTable();
								setValue(HTML.span(table.getCheckedRows().size() + " / " + table.getRowCount())
										.style("float: right").toHtml());
							}
						}
					}

					@Order(2)
					public class ContributorTableField extends AbstractTableField<ContributorTable> {

						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setStatusVisible(false);
						}

						public boolean filter(ScannedNumber n) {
							return getTable().getRows().stream().filter(r -> r.isChecked())
									.filter(r -> r.getCellValue(0).equals(n.contributor)).findAny().isPresent();
						}

						protected Stream<ITableRow> createNumbersTableRows() {
							return getContributors().map(c -> {
								ITableRow row = getTable().createRow();
								row.setCellValue(0, c);
								row.setCellValue(1, c.id);
								row.setCellValue(2, c.country);
								row.setCellValue(3, "~" + c.age + "0");
								row.setCellValue(4, c.sex);
								row.setCellValue(5, c.numbers.size());
								row.setChecked(true);
								return row;
							});
						}

						public class ContributorTable extends AbstractTable {

							@Override
							protected boolean getConfiguredCheckable() {
								return true;
							}

							@Override
							protected void execInitTable() {
								addRows(createNumbersTableRows().collect(Collectors.toList()));
								checkAllRows();
								addTableListener(new TableListener() {
									@Override
									public void tableChanged(TableEvent e) {
										switch (e.getType()) {
										case TableEvent.TYPE_ROWS_CHECKED:
											NumbersForm.this.getFieldByClass(ContributorStatusField.class)
													.execUpdateValue();
											NumbersForm.this.getFieldByClass(NumbersTableField.class)
													.execReloadTableData();
											break;
										}
									}

									@Override
									public void tableChangedBatch(List<? extends TableEvent> batch) {
									}
								});
								NumbersForm.this.getFieldByClass(ContributorStatusField.class).execUpdateValue();
							}

							@Order(0)
							public class KeyColumn extends AbstractObjectColumn {
								@Override
								protected boolean getConfiguredDisplayable() {
									return false;
								}
							}

							@Order(1)
							public class NameColumn extends AbstractIntegerColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return "ID";
								}

								@Override
								protected boolean getConfiguredSortAscending() {
									return true;
								}

								@Override
								protected int getConfiguredWidth() {
									return 75;
								}
							}

							@Order(2)
							public class CountryColumn extends AbstractStringColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return "Country";
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
								}
							}

							@Order(3)
							public class AgeColumn extends AbstractStringColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return "Age";
								}

								@Override
								protected int getConfiguredWidth() {
									return 50;
								}
							}

							@Order(4)
							public class SexColumn extends AbstractStringColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return "Sex";
								}

								@Override
								protected int getConfiguredWidth() {
									return 50;
								}
							}

							@Order(5)
							public class CountColumn extends AbstractIntegerColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return "Count";
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
								}
							}
						}
					}

				}

				@Order(1)
				public class DigitFilterBox extends AbstractGroupBox {

					@Override
					protected void execInitField() {
						setGridColumnCountHint(1);
					}

					@Order(1)
					public class DigitHeaderBox extends AbstractSequenceBox {

						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setStatusVisible(false);
						}

						@Order(1)
						public class DigitTitleField extends AbstractLabelField {
							@Override
							protected void execInitField() {
								setLabelVisible(false);
								setStatusVisible(false);
								setHtmlEnabled(true);
								setValue(HTML.bold("Filter by \"Digit\"").toHtml());
							}
						}

						@Order(2)
						public class DigitStatusField extends AbstractLabelField {
							@Override
							protected void execInitField() {
								setLabelVisible(false);
								setStatusVisible(false);
								setHtmlEnabled(true);
							}

							public void execUpdateValue() {
								DigitTable table = NumbersForm.this.getFieldByClass(DigitTableField.class).getTable();
								setValue(HTML.span(table.getCheckedRows().size() + " / " + table.getRowCount())
										.style("float: right").toHtml());
							}
						}
					}

					@Order(2)
					public class DigitTableField extends AbstractTableField<DigitTable> {

						@Override
						protected void execInitField() {
							setLabelVisible(false);
							setStatusVisible(false);
						}

						public boolean filter(ScannedNumber n) {
							return getTable().getRows().stream().filter(r -> r.isChecked())
									.filter(r -> r.getCellValue(0).equals((int) n.digit)).findAny().isPresent();
						}

						protected Stream<ITableRow> createNumbersTableRows() {
							return getAllScannedNumber().collect(groupingBy(n -> n.digit, counting())).entrySet()
									.stream().map(e -> {
										ITableRow row = getTable().createRow();
										row.setCellValue(0, e.getKey());
										row.setCellValue(1, e.getValue());
										row.setChecked(true);
										return row;
									});
						}

						public class DigitTable extends AbstractTable {

							@Override
							protected boolean getConfiguredCheckable() {
								return true;
							}

							@Override
							protected void execInitTable() {
								addRows(createNumbersTableRows().collect(Collectors.toList()));
								checkAllRows();
								addTableListener(new TableListener() {
									@Override
									public void tableChanged(TableEvent e) {
										switch (e.getType()) {
										case TableEvent.TYPE_ROWS_CHECKED:
											NumbersForm.this.getFieldByClass(NumbersTableField.class)
													.execReloadTableData();
											NumbersForm.this.getFieldByClass(DigitStatusField.class).execUpdateValue();
											break;
										}
									}

									@Override
									public void tableChangedBatch(List<? extends TableEvent> batch) {
									}
								});
								NumbersForm.this.getFieldByClass(DigitStatusField.class).execUpdateValue();
							}

							@Order(1)
							public class DigitColumn extends AbstractIntegerColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return "Digit";
								}

								@Override
								protected boolean getConfiguredSortAscending() {
									return true;
								}

								@Override
								protected int getConfiguredWidth() {
									return 75;
								}
							}

							@Order(2)
							public class CountColumn extends AbstractIntegerColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return "Count";
								}

								@Override
								protected int getConfiguredWidth() {
									return 300;
								}
							}
						}
					}
				}
			}

			@Order(2)
			public class NumbersBox extends AbstractGroupBox {

				@Override
				protected void execInitField() {
					setTitle("NumbersTableField");
				}

				public class NumbersTableField extends AbstractTableField<NumbersTable> {

					@Override
					protected void execInitField() {
						setLabelVisible(false);
						setStatusVisible(false);
					}

					@Override
					protected void execReloadTableData() {
						getTable().deleteAllRows();
						getTable().addRows(createNumbersTableRows().collect(Collectors.toList()));
					}

					protected Stream<ITableRow> createNumbersTableRows() {
						ContributorTableField contributorTableField = NumbersForm.this
								.getFieldByClass(ContributorTableField.class);
						DigitTableField digitTableField = NumbersForm.this.getFieldByClass(DigitTableField.class);
						return getAllScannedNumber().filter(n -> contributorTableField.filter(n))
								.filter(n -> digitTableField.filter(n)).map(n -> {
									ITableRow row = getTable().createRow();
									row.setCellValue(0, n);
									row.setCellValue(1, HTML.div(HTML.span(n.path), HTML.br(), HTML.span("")));
									row.setCellValue(2, HTML.img("/res/numbers" + n.path)
											.addAttribute("style", "width:25px;height:25px").toHtml());
									return row;
								});
					}

					public class NumbersTable extends AbstractTable {

						@Override
						protected void execInitTable() {
							setAutoResizeColumns(true);
							setTableStatusVisible(true);
							addRows(createNumbersTableRows().collect(Collectors.toList()));
						}

						@Order(0)
						public class KeyColumn extends AbstractObjectColumn {
							@Override
							protected boolean getConfiguredDisplayable() {
								return false;
							}
						}

						@Order(1)
						public class NameColumn extends AbstractStringColumn {
							@Override
							protected String getConfiguredHeaderText() {
								return "Name";
							}

							@Override
							protected boolean getConfiguredHtmlEnabled() {
								return true;
							}
						}

						@Order(2)
						public class ImgColumn extends AbstractStringColumn {
							@Override
							protected String getConfiguredHeaderText() {
								return "Scan";
							}

							@Override
							protected boolean getConfiguredHtmlEnabled() {
								return true;
							}

							@Override
							protected int getConfiguredWidth() {
								return 50;
							}
						}
					}
				}
			}
		}
	}
}