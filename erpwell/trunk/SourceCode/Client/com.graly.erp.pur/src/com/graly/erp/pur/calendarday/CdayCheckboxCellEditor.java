package com.graly.erp.pur.calendarday;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.base.calendar.model.CalendarDay;

public class CdayCheckboxCellEditor extends CheckboxCellEditor {
	protected static final int style = SWT.CHECK;
	boolean value = false;
	Button checkBox;
	private TableViewer tableViewer;
	
	public CdayCheckboxCellEditor(TableViewer tableViewer) {
        super(tableViewer.getTable());
        this.tableViewer = tableViewer;
    }
	
	protected Control createControl(Composite parent) {
		checkBox = new Button(parent, style);
		checkBox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
            	notifyCalendarDay();
            }
			
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
		return checkBox;
    }
	
	protected Object doGetValue() {
        value = checkBox.getSelection();
        return value;
    }

    protected void doSetValue(Object value) {
        Assert.isTrue(checkBox != null && value instanceof Boolean);
        boolean preValue = ((Boolean) value).booleanValue();
        if (preValue == true) {
        	this.value = false;
        } else {
        	this.value = true;
        }
        checkBox.setSelection(this.value);
        notifyCalendarDay();
    }
	
    public void activate() {
    }
    
    public void notifyCalendarDay() {
		TableItem[] items = tableViewer.getTable().getSelection();
    	if (items != null && items.length > 0){
    		TableItem item = items[0];
    		Object data = item.getData();
    		if(data != null) {
				CalendarDay calendarDay = (CalendarDay)data;
				Boolean isHoliday = (Boolean)checkBox.getSelection();
				calendarDay.setIsHoliday(isHoliday);
				tableViewer.refresh();
			}
    	}
	}
}
