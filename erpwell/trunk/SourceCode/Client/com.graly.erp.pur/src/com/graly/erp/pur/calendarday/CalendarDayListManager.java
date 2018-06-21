package com.graly.erp.pur.calendarday;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.calendar.model.CalendarDay;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class CalendarDayListManager extends TableViewerManager {
	private static final Logger logger = Logger.getLogger(CalendarDayListManager.class);
	private CellEditor[] cellEditor;
	protected TableViewer tableViewer;

	public CalendarDayListManager(ADTable adTable) {
		super(adTable);
		super.setStyle(SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
	}

	public CalendarDayListManager(ADTable adTable, int style) {
		this(adTable);
		super.addStyle(style);
	}

	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		ItemAdapterFactory factory = new ItemAdapterFactory();
		try {
			factory.registerAdapter(Object.class, new CalendarDayItemAdapter<ADBase>());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return factory;
	}

	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit, int h) {
		final TableViewer viewer = (TableViewer) super.newViewer(parent, toolkit, 400);
		setCellEditor(viewer);
		return viewer;
	}

	private void setCellEditor(TableViewer tableViewer) {
		int size = this.getColumns().length;
		cellEditor = new CellEditor[size];
		String[] properties = new String[size];
		for (int i = 0; i < size; i++) {
			String column = (String) tableViewer.getTable().getColumn(i).getData(TableViewerManager.COLUMN_ID);
			if ("isHoliday".equals(column)) {
				cellEditor[i] = new CdayCheckboxCellEditor(tableViewer);
			} else {
				cellEditor[i] = null;
			}
			properties[i] = column;
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		CalendarDayCellModifier ccm = new CalendarDayCellModifier(tableViewer);
		tableViewer.setCellModifier(ccm);
	}

	private class CalendarDayCellModifier implements ICellModifier {
		TableViewer tableViewer;
		CalendarDay calendarDay;

		public CalendarDayCellModifier(TableViewer tableViewer) {
			this.tableViewer = tableViewer;
		}

		@Override
		public boolean canModify(Object element, String property) {
			if ("isHoliday".equals(property)) {
				TableItem it = tableViewer.getTable().getSelection()[0];
				it.setImage(1, null);
				return true;
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			if (element instanceof CalendarDay) {
				calendarDay = (CalendarDay) element;
				if ("isHoliday".equals(property)) {
					return calendarDay.getIsHoliday();
				}
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			//				try {
			//					TableItem it = ((TableItem)element);
			//					Boolean isHoliday = calendarDay.getIsHoliday();
			//					if(value != null) {
			//						CalendarDay calendarDay = (CalendarDay)it.getData();
			//						isHoliday = (Boolean)value;
			//						calendarDay.setIsHoliday(isHoliday);
			//					}
			//					
			//					/*if (Boolean.TRUE.equals(isHoliday)) {
			//						it.setImage(1, JFaceResources.getImageRegistry().getDescriptor("CHECKED").createImage());
			//					} else {
			//						it.setImage(1, JFaceResources.getImageRegistry().getDescriptor("UNCHECKED").createImage());;
			//					}*/
			//					tableViewer.refresh();
			//				} catch(Exception e) {
			//					logger.error("Error CalendarDayListManager : modify()" + e.getMessage());
			//				}
		}
	}

}
