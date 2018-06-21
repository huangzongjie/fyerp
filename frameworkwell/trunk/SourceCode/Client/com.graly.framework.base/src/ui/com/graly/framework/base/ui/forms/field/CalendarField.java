package com.graly.framework.base.ui.forms.field;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.DefaultSettings;
import org.eclipse.nebula.widgets.calendarcombo.ICalendarListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.util.I18nUtil;

public class CalendarField extends AbstractField {
	protected int mStyle = SWT.BORDER | SWT.READ_ONLY;
	CalendarCombo date;
    
    public CalendarField(String id) {
        super(id);
    }
    
    public CalendarField(String id, int style) {
        super(id);
        mStyle = style;
    }
    
	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		int i = 0;
		String labelStr = getLabel();
        if (labelStr != null) {
        	mControls = new Control[2];
        	Label label = toolkit.createLabel(composite, labelStr);
            mControls[0] = label;
            i = 1;
        } else {
        	mControls = new Control[1];
        }
        date = new CalendarCombo(composite, mStyle, new Settings(), null);

        toolkit.adapt(date);
        toolkit.paintBordersFor(date);
        Date val = (Date)getValue();
        if (val != null) {
            date.setDate(val);
        } else {
        	date.setText("");
        }
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        date.setLayoutData(gd);
        if (getToolTipText() != null) {
        	date.setToolTipText(getToolTipText());
        }
        mControls[i] = date;        
        date.addCalendarListener(new ICalendarListener() {
            public void popupClosed() {
            }

            public void dateChanged(Calendar date) {
            	setValue(date.getTime());
            }

            public void dateRangeChanged(Calendar start, Calendar end) {
            }
        });
        
        // 添加FocusListener监听器目的是为了解决CalendarListener的不足(当date控件日期为空时,第一次确定
        // 选择日期时,无法触发dateChanged事件,因此在CanlendarField捕捉不到选择的日期),当焦点失去时,如果
        // getValue()值为空并且date控件中有值时才会将date控件的日期赋给CanlendarField
        date.addFocusListener(new FocusListener() {
        	public void focusGained(FocusEvent e) {}
        	
        	public void focusLost(FocusEvent e) {
        		if(getValue() == null && date.getDate() != null) {
        			setValue(date.getDate().getTime());
        		}
        	}
        });
	}

	public CalendarCombo getDateControl() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (CalendarCombo)ctrl[1];
        } else {
            return (CalendarCombo)ctrl[0];
        }
    }

    public Label getLabelControl() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (Label)ctrl[0];
        } else {
            return null;
        }
    }

	@Override
	public void refresh() {
		if (getValue() != null){
			getDateControl().setDate((Date)getValue());
		} else {
			getDateControl().setText("");
		}
	}
	
	class Settings extends DefaultSettings{
		public String getDateFormat() {
			return I18nUtil.getDefaultDatePattern();
		}
		
		public Locale getLocale() {
			return Locale.getDefault();
		}
	}
	
	public String getFieldType() {
		return "calendar";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		date.setEnabled(enabled);
		super.enableChanged(enabled);
    }
}
