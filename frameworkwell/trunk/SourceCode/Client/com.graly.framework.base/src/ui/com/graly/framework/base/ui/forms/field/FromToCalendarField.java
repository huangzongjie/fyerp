package com.graly.framework.base.ui.forms.field;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.DefaultSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class FromToCalendarField extends AbstractField {
	protected int mStyle = SWT.BORDER | SWT.READ_ONLY;
	public static final String DATE_FROM = "from";
	public static final String DATE_TO = "to";
	CalendarCombo dateFrom;
	CalendarCombo dateTo;
	Date fromDate;
	Date toDate = Env.getSysDate();;
	{
		Calendar c = Calendar.getInstance();
	    c.setTime(toDate);   //设置当前日期
	    c.add(Calendar.YEAR, -1); //年份减1
	    fromDate = c.getTime(); //结果
	    
	}
	
	Map<String,Date> dateMap = new HashMap<String, Date>();

	protected void initDateMap() {
		dateMap.put(DATE_FROM, fromDate);
		dateMap.put(DATE_TO, toDate);
	}
    
    public FromToCalendarField(String id) {
        super(id);
        initDateMap();
    }
    
    public FromToCalendarField(String id, int style) {
        this(id);
        mStyle = style;
    }
    
    public FromToCalendarField(String id, Date fromDate, Date toDate) {
    	super(id);
    	this.fromDate = fromDate;
    	this.toDate = toDate;
    	initDateMap();
    }
    
	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
        setValue(dateMap);

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
        
        Composite control = toolkit.createComposite(composite);
        GridLayout glayout = new GridLayout();
        glayout.numColumns = 4;
        GridData gdata = new GridData();
        gdata.grabExcessHorizontalSpace = true;
        gdata.horizontalAlignment = SWT.FILL;
        control.setLayout(glayout);
        control.setLayoutData(gdata);
        
        Label fromLabel = toolkit.createLabel(control, Message.getString("common.from"));
        dateFrom = new CalendarCombo(control, mStyle, new Settings(), null);
        dateFrom.setDate(dateMap.get(DATE_FROM));
//        dateFrom.setToolTipText("选择开始日期,如果选择None则从最早的记录开始查");
        
        Label toLabel = toolkit.createLabel(control, Message.getString("common.to"));
        dateTo = new CalendarCombo(control, mStyle, new Settings(), null);
        dateTo.setDate(dateMap.get(DATE_TO));
//        dateTo.setToolTipText("选择结束日期,如果选择None,则一直查到最新的记录");
        
        toolkit.adapt(dateFrom);
        toolkit.paintBordersFor(dateFrom);
        toolkit.adapt(dateTo);
        toolkit.paintBordersFor(dateTo);
        
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        dateFrom.setLayoutData(gd);
        dateTo.setLayoutData(gd);
        if (getToolTipText() != null) {
        	dateFrom.setToolTipText(getToolTipText());
        }
        mControls[i] = control;
	}

	public CalendarCombo[] getDateControls() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
        	Composite control = (Composite) ctrl[1];
        	Control[] children = control.getChildren();
        	CalendarCombo[] ccs = new CalendarCombo[]{(CalendarCombo) children[1],(CalendarCombo) children[3]};
            return ccs;
        } else {
            return new CalendarCombo[]{(CalendarCombo)ctrl[0]};
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
			Map<String, Date> dateMap = (Map<String, Date>) getValue();
			getDateControls()[0].setDate(dateMap.get(DATE_FROM));
			getDateControls()[1].setDate(dateMap.get(DATE_TO));
		} else {
			getDateControls()[0].setText("");
			getDateControls()[1].setText("");
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
		return FieldType.FROMTO_CALENDAR;
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		dateFrom.setEnabled(enabled);
		dateTo.setEnabled(enabled);
		super.enableChanged(enabled);
    }

	@Override
	public Object getValue() {
		dateMap.put(DATE_FROM, dateFrom.getDate() == null ? null : dateFrom.getDate().getTime());
		dateMap.put(DATE_TO, dateTo.getDate() == null ? null : dateTo.getDate().getTime());
		return dateMap;
	}
	// Add by Bruce You 2012-03-07
	public void setDateTo(Date todate) {
		dateMap.put(DATE_TO, todate);
	}
	public void setDateFrom(Date fromdate) {
		dateMap.put(DATE_FROM, fromdate);
	}
	public void setDateBoth(Date fromdate , Date todate) {
		dateMap.put(DATE_FROM, fromdate);
		dateMap.put(DATE_TO, todate);
	}
}
