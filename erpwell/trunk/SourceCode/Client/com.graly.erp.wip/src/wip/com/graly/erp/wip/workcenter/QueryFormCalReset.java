package com.graly.erp.wip.workcenter;

import org.eclipse.swt.widgets.Composite;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;

public class QueryFormCalReset extends QueryForm {
	public static String DATE_END_ACTUAL = "dateEndActual";
	public static String DATE_END = "dateEnd";

	public QueryFormCalReset(Composite parent, int style, ADTable table) {
		super(parent, style, table);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addFields() {
		if (allADfields != null && allADfields.size() > 0){
			for (ADField adField : allADfields) {
	    		if (adField.getIsQuery()) {
	    			adField.setIsReadonly(false);
	    			IField field = getField(adField);
	    			if(adField.getIsMandatory()){
	    				String displayLabel = I18nUtil.getI18nMessage(adField, "label");
	    				field.setLabel(displayLabel);
	    			}
	    			if (field == null) {
	    				continue;
	    			}
	    			//Add by Bruce You 2012-03-07
	    			//将FromToCalendar的部分初始值设为空
	    			if(field instanceof FromToCalendarField){
	    				if(field.getId().equals(DATE_END))
	    				{
	    					((FromToCalendarField)field).setDateTo(null);
	    				} else {
							if(field.getId().equals(DATE_END_ACTUAL))
							{
								((FromToCalendarField)field).setDateBoth(null,null);
							}
						}
	    			}
	    			adFields.put(adField.getName(), adField);
    				if (field != null) {
    					field.setADField(adField);
    				}
	    		}
	    	}
			registeValueChangeListener();
		}
	}
}
