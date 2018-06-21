package com.graly.erp.inv.transfer;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class InvQueryForm extends QueryForm {

	public InvQueryForm(Composite parent, int style, ADTable table) {
		super(parent, style, table);
	}

	@Override
	// 查询时，重写入库时间，间隔
	public IField getField(ADField adField) {
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if (adField.getIsMandatory()) {
			displayLabel = displayLabel + "*";
		}
		IField field = null;
		if (FieldType.FROMTO_CALENDAR.equalsIgnoreCase(displayText)
				&& (name.equals("dateCreated"))) {
			try {
				Date sysDate = Env.getSysDate();
				field = this.createFromToCalendarField(name, displayLabel, sysDate,sysDate);
				addField(name, field);
			} catch (Exception e) {
				e.printStackTrace();
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} else {
			field = super.getField(adField);
		}
		return field;
	}
	
	
    public FromToCalendarField createFromToCalendarField(String id, String label, Date fromDate, Date toDate) {
    	FromToCalendarField fe = new FromToCalendarField(id, fromDate, toDate);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
}
