package com.graly.framework.base.ui.forms.field;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.ui.util.Env;

public class DateTimeField extends AbstractField {
	
    protected int mStyle = SWT.BORDER;
    protected int mCols = 1;
    DateTime date;
    DateTime time;
    
    public DateTimeField(String id) {
        super(id);
    }

    public DateTimeField(String id, int style) {
        super(id);
        mStyle = style;
    }
    
	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		String labelStr = getLabel();
		int i = 0;
        if (labelStr != null) {
        	mControls = new Control[2];
        	Label label = toolkit.createLabel(composite, labelStr);
            mControls[0] = label;
            i = 1;
        } else {
        	mControls = new Control[1];
        }
		
		Composite parent = toolkit.createComposite(composite);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        parent.setLayoutData(gd);
        parent.setLayout(layout);
		toolkit.adapt(parent);
        toolkit.paintBordersFor(parent);
        
        GridData gd1;
		layout.numColumns = 2;
		gd1 = new GridData();
		gd1.horizontalAlignment = SWT.FILL;
		gd1.grabExcessHorizontalSpace = true;
        date = new DateTime (parent, SWT.DATE | SWT.BORDER);
        date.setLayoutData(gd1);
        time = new DateTime (parent, SWT.TIME | SWT.BORDER);
        time.setLayoutData(gd1);
        if (this.getADField() != null && ((ADField)this.getADField()).getIsReadonly()){
        	date.setEnabled(false);
        	time.setEnabled(false);
		}
        toolkit.adapt(date);
        toolkit.paintBordersFor(date);
        toolkit.adapt(time);
        toolkit.paintBordersFor(time);
        Date val = (Date)getValue();
        if (val != null) {
            date.setYear(val.getYear());
            date.setMonth(val.getMonth());
            date.setDay(val.getDate());
            time.setHours(val.getHours());
            time.setMinutes(val.getMinutes());
            time.setSeconds(val.getSeconds());
        } else {
        	//time.getHours(val.getHours());
        }
        final Date now = Env.getSysDate();
        date.addFocusListener(new FocusListener() {
        	public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
            	DateTime t = (DateTime) e.widget;
            	Date date = (Date)getValue();
            	if (date == null){
					date = now;
            	}
            	date.setYear(t.getYear() - 1900);
                date.setMonth(t.getMonth());
                date.setDate(t.getDay());
                setValue(date);
            }
        });
        time.addFocusListener(new FocusListener() {
        	public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
            	Date time = (Date)getValue();
            	if (time == null){
            		time = now;
            	}
            	time.setHours(time.getHours());
                time.setMinutes(time.getMinutes());
                time.setSeconds(time.getSeconds());
            }
        });
        mControls[i] = parent;

	}
	
	public Composite getGroupControl() {
		Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (Composite)ctrl[1];
        } else {
            return (Composite)ctrl[0];
        }
    }
	
	public int getStyle() {
        return mStyle;
    }
    
    public void setColumns(int cols) {
        mCols = cols;
    }
    
	@Override
	public void refresh() {
		Date now = Env.getSysDate();
		if (getValue() != null){
			Date val = (Date)getValue();
            date.setYear(val.getYear());
            date.setMonth(val.getMonth());
            date.setDay(val.getDate());
            time.setHours(val.getHours());
            time.setMinutes(val.getMinutes());
            time.setSeconds(val.getSeconds());
		} else {
			Date val = now;
			date.setYear(val.getYear() + 1900);
            date.setMonth(val.getMonth());
            date.setDay(val.getDate());
            time.setHours(val.getHours());
            time.setMinutes(val.getMinutes());
            time.setSeconds(val.getSeconds());
            setValue(val);
		}
	}
	
	public String getFieldType() {
		return "datetime";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		date.setEnabled(enabled);
		time.setEnabled(enabled);
		super.enableChanged(enabled);
    }
}
