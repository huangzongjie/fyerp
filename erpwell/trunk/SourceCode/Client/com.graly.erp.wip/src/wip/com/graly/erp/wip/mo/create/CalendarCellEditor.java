package com.graly.erp.wip.mo.create;

import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.graly.framework.base.ui.util.I18nUtil;

public class CalendarCellEditor extends CellEditor {
	protected static final int style = SWT.BORDER | SWT.READ_ONLY;
	protected TreeViewer treeViewer;
	protected CalendarCombo calendar;
	protected Date dateValue;
	
	public CalendarCellEditor() {
        setStyle(style);
    }

    public CalendarCellEditor(Composite parent, int style) {
        super(parent, style);
    }
    
    public CalendarCellEditor(TreeViewer treeViewer) {
    	super(treeViewer.getTree(), style);
    	this.treeViewer = treeViewer;
    }

	@Override
	protected Control createControl(Composite parent) {
		calendar = new CalendarCombo(parent, getStyle(), new Settings(), null);
		calendar.setFont(parent.getFont());

		return calendar;
	}

	@Override
	protected Object doGetValue() {
		if(this.calendar.getDate() != null) {
			this.dateValue = this.calendar.getDate().getTime();
		} else {
			this.dateValue = null;
		}
		return dateValue;
	}

	@Override
	protected void doSetFocus() {
		calendar.setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
//		Assert.isTrue(calendar != null && (value instanceof Date));
		if(value == null) {
			this.dateValue = null;
		} else if(value instanceof Date) {
			this.dateValue = (Date)value;			
		}
		calendar.setDate(dateValue);
	}
	
	class Settings extends  org.eclipse.nebula.widgets.calendarcombo.DefaultSettings {
		public String getDateFormat() {
			return I18nUtil.getDefaultDatePattern();
		}
		
		public Locale getLocale() {
			return Locale.getDefault();
		}
	}
}