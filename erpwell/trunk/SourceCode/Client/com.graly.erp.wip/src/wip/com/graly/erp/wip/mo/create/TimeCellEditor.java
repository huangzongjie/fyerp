package com.graly.erp.wip.mo.create;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;

import com.graly.framework.base.ui.util.Env;

public class TimeCellEditor extends CellEditor {
	protected static final int style = SWT.BORDER | SWT.TIME | SWT.SHORT;
	protected TreeViewer treeViewer;
	protected DateTime time;
	protected Date dateValue;
	
	public TimeCellEditor() {
        setStyle(style);
    }

    public TimeCellEditor(Composite parent, int style) {
        super(parent, style);
    }
    
    public TimeCellEditor(TreeViewer treeViewer) {
    	super(treeViewer.getTree(), style);
    	this.treeViewer = treeViewer;
    }

	@Override
	protected Control createControl(Composite parent) {
		time = new DateTime(parent, getStyle());
		time.setFont(parent.getFont());

//		time.addFocusListener(getFocusListener());
		
		return time;
	}

	@Override
	protected Object doGetValue() {
		if (dateValue == null){
    		dateValue = Env.getSysDate();
    	}
    	dateValue.setHours(time.getHours());
    	dateValue.setMinutes(time.getMinutes());
		return dateValue;
	}

	@Override
	protected void doSetFocus() {
		time.setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
//		Assert.isTrue(time != null && (value instanceof Date));
		if (value != null) {
			this.dateValue = (Date)value;
			time.setHours(dateValue.getHours());
			time.setMinutes(dateValue.getMinutes());
			time.setSeconds(0);
		}
	}
	
	private FocusListener getFocusListener() {
		return new FocusListener() {
        	public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
            	DateTime t = (DateTime) e.widget;
            	
            }
        };
	}

}
