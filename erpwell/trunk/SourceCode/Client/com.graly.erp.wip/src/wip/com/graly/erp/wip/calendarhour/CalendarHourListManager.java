package com.graly.erp.wip.calendarhour;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.calendar.model.CalendarHour;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;

public class CalendarHourListManager extends TableListManager {
	private String CALENDAR_DAY_HOURS = "part1";
	private CellEditor[] cellEditor;
	private int columnCout = 3;

	public CalendarHourListManager(ADTable adTable) {
		super(adTable);
	}

	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit, int heightHint) {
		TableViewer viewer = (TableViewer) super.newViewer(parent, toolkit, heightHint);
		viewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = event.gc.getFontMetrics().getHeight() * 3 / 2;
			}
		});
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		TableViewerEditor.create(viewer, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
		setCellEditor(viewer);
		return viewer;
	}

	private void setCellEditor(TableViewer tableViewer) {
		cellEditor = new CellEditor[columnCout];
		String[] properties = new String[columnCout];
		for (int i = 0; i < columnCout; i++) {
			String column = (String) tableViewer.getTable().getColumn(i).getData(TableListManager.COLUMN_ID);
			properties[i] = column;
			if (CALENDAR_DAY_HOURS.equals(column)) {
				cellEditor[i] = new MyTextCellEditor(tableViewer, column);
			} else {
				cellEditor[i] = null;
			}
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		TextCellModifier tcm = new TextCellModifier(tableViewer, "");
		tableViewer.setCellModifier(tcm);
	}

	class MyTextCellEditor extends TextCellEditor {
		private TableViewer tableViewer;
		private String cloumnName;

		public MyTextCellEditor(TableViewer tableViewer, String cloumnName) {
			super(tableViewer.getTable());
			this.tableViewer = tableViewer;
			this.cloumnName = cloumnName;
		}

		@Override
		protected Control createControl(Composite parent) {
			super.createControl(parent);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					TableItem[] items = tableViewer.getTable().getSelection();
					if (items != null && items.length > 0) {
						TableItem item = items[0];
						Object obj = item.getData();
						CalendarHour calendarHour = (CalendarHour) obj;
						String value = text.getText();
						if (value != null && value.equals("")) {
						} else {
							if (discernParameter(calendarHour, value)) {
								if (obj instanceof CalendarHour) {
									setValue(calendarHour, value);
								}
							} else {
								if (CALENDAR_DAY_HOURS.equals(cloumnName)) {
									tableViewer.editElement(item.getData(), 1);
								}
							}
						}
					}
				}
			});

			text.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if (CalendarHourSection.flag) {
						CalendarHourSection.setFlag(false);
						return;
					}
					String value = text.getText();
					TableItem[] items = tableViewer.getTable().getSelection();
					Boolean type = value.matches("\\d{1,2}+\\:\\d{2}+\\-\\d{1,2}+\\:\\d{2}");

					if (!type) {
						UI.showError(Message.getString("wip_hourinput_error"), Message.getString("common.inputerror_title"));

						if (items != null && items.length > 0) {
							TableItem item = items[0];
							tableViewer.editElement(item.getData(), 0);
							return;
						}
					} else {
						String[] hourSplit = value.split("-");
						int hh[] = new int[hourSplit.length + 1];
						int mm[] = new int[hourSplit.length + 1];
						int i = 1;
						for (String hours : hourSplit) {
							String[] times = hours.split(":");
							hh[i] = Integer.parseInt(times[0]);
							mm[i] = Integer.parseInt(times[1]);
							if (hh[i] > 24 || mm[i] > 59 || hh[i] < 0 || mm[i] < 0
									|| (hh[i] == 24 && mm[i] > 0)) {
								UI.showError(Message.getString("wip_hourinput_error"), Message.getString("common.inputerror_title"));
								if (items != null && items.length > 0) {
									TableItem item = items[0];
									tableViewer.editElement(item.getData(), 0);
									return;
								}
							}
							i++;
						}

						if (hh[1] > hh[2] || (hh[1] == hh[2] && mm[1] > mm[2])) {
							UI.showError(Message.getString("ppm.dateintervalused"), Message.getString("common.inputerror_title"));
							if (items != null && items.length > 0) {
								TableItem item = items[0];
								tableViewer.editElement(item.getData(), 0);
								return;
							}
						}
					}
				}
			});
			return text;
		}

		public boolean discernParameter(CalendarHour calendarHour, Object object) {
			if (CALENDAR_DAY_HOURS.equals(cloumnName)) {
				return true;
			}
			return false;
		}

		public void setValue(CalendarHour calendarHour, String value) {
			if (CALENDAR_DAY_HOURS.equals(cloumnName)) {
				String[] hours = value.split("-");
				if (hours != null && hours.length == 2) {
					String hour1 = hours[0];
					String hour2 = hours[1];
					String[] times1 = hour1.split(":");
					String[] times2 = hour2.split(":");

					if (hour2.trim().startsWith("0") && times1[0].length() > 1) {
						hour2 = hour2.trim().substring(1);
					}
					String hourValue = hour1 + "-" + hour2;
//					if (hourValue.trim().startsWith("0") && times2[0].length() > 1) {
//						hourValue = hourValue.trim().substring(1);
//					}
					calendarHour.setPart1(hourValue.trim());
				} else {
					calendarHour.setPart1(value.trim());
				}
			}
		}
	}

	class TextCellModifier implements ICellModifier {
		private TableViewer tableViewer;
		private CalendarHour calendarHour;

		public TextCellModifier(TableViewer tableViewer, String defValueInt) {
			this.tableViewer = tableViewer;
		}

		@Override
		public boolean canModify(Object element, String property) {
			if (CALENDAR_DAY_HOURS.equals(property)) {
				if (element instanceof CalendarHour) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			calendarHour = (CalendarHour) element;
			if (CALENDAR_DAY_HOURS.equals(property)) {
				return calendarHour.getPart1();
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			tableViewer.refresh();
		}
	}
}
