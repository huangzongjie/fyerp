package com.graly.erp.wip.workcenter.schedule.query;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;

public class ScheduleQueryForm extends QueryForm {
	private static final Logger logger = Logger.getLogger(ScheduleQueryForm.class);
	public ScheduleQueryForm(Composite parent, int style, ADTable table) {
		super(parent, style, table);
	}
	
    public FromToCalendarField createFromToCalendarField(String id, String label) {
    	Date fromDate= new Date();
    	Date toDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        cal.add(Calendar.DATE, 14);
        toDate = cal.getTime();
    	FromToCalendarField fe = new FromToCalendarField(id,fromDate,toDate);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
}
