package com.graly.framework.base.ui.forms.field;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.ui.util.Env;

public class DateField extends AbstractField {
	protected int mStyle = SWT.BORDER;
    protected int mCols = 1;
    DateTime date;
    int dateType = SWT.DATE;
    
    public DateField(String id) {
        super(id);
    }

    public DateField(String id, int style) {
        this(id);
        mStyle = style;
    }
    
    public DateField(String id, int style, int dateType) {
    	this(id, style);
    	this.dateType = dateType;
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
        date = new DateTime (parent, dateType | SWT.BORDER);
        if((dateType & SWT.SHORT) != 0){
        	date.setDay(1);
    	}
        date.setLayoutData(gd1);
        if (this.getADField() != null && ((ADField)this.getADField()).getIsReadonly()){
        	date.setEnabled(false);
		}
        toolkit.adapt(date);
        toolkit.paintBordersFor(date);
        Date val = (Date)getValue();
        final Date now = Env.getSysDate();
		if (val != null) {
            date.setYear(val.getYear());
            date.setMonth(val.getMonth());
            date.setDay(val.getDate());
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
        } else {
        	val = now;
        	setValue(val);
        }
        
        
        date.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
        		DateTime t = (DateTime) e.widget;
            	Date val = (Date)getValue();
            	if (val == null){
            		val = now;
            	}
            	val.setYear(t.getYear() - 1900);
            	val.setMonth(t.getMonth());
            	val.setDate(t.getDay());
            	val.setHours(0);
            	val.setMinutes(0);
            	val.setSeconds(0);
                setValue(val);
        	}
        });

        date.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
        		DateTime t = (DateTime) e.widget;
            	Date val = (Date)getValue();
            	if (val == null){
            		val = now;
            	}
            	val.setYear(t.getYear() - 1900);
            	val.setMonth(t.getMonth());
            	val.setDate(t.getDay());
            	val.setHours(0);
            	val.setMinutes(0);
            	val.setSeconds(0);
                setValue(val);
        	}
        });
        
//        date.addMouseTrackListener(new MouseTrackListener(){//这个监听器不太灵活，换成 selectlistener 和 focuslistener
//        	public void mouseEnter(MouseEvent e){}
//        	public void mouseHover(MouseEvent e){}
//        	
//        	public void mouseExit(MouseEvent e) {
//        		DateTime t = (DateTime) e.widget;
//            	Date val = (Date)getValue();
//            	if (val == null){
//            		val = new Date();
//            	}
//            	val.setYear(t.getYear() - 1900);
//            	val.setMonth(t.getMonth());
////            	if((dateType & SWT.SHORT) != 0){
////            		val.setDate(0);
////            	}else{
//            		val.setDate(t.getDay());
////            	}
//            	val.setHours(0);
//            	val.setMinutes(0);
//            	val.setSeconds(0);
//                setValue(val);
//        	}
//        });
        mControls[i] = date;
	}

	@Override
	public void refresh() {
		if (getValue() != null){
			Date val = (Date)getValue();
            date.setYear(val.getYear() + 1900);
            date.setMonth(val.getMonth());
            date.setDay(val.getDate());
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
		} else {
			Date val = Env.getSysDate();
			date.setYear(val.getYear() + 1900);
            date.setMonth(val.getMonth());
            date.setDay(val.getDate());
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
		}
	}
	
	public DateTime getDateControl() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (DateTime)ctrl[1];
        } else {
            return (DateTime)ctrl[0];
        }
    }
	
	@Override
	public String getFieldType() {
		return (((dateType & SWT.DATE) != 0) ? FieldType.DATE : FieldType.SHORTDATE);
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		date.setEnabled(enabled);
		super.enableChanged(enabled);
    }

}
