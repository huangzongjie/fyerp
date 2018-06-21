package com.graly.promisone.base.entitymanager.forms;

import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.activeentity.model.ADRefTable;
import com.graly.promisone.activeentity.model.ADTab;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.views.TableListManager;
import com.graly.promisone.base.ui.forms.Form;
import com.graly.promisone.base.ui.forms.field.BooleanField;
import com.graly.promisone.base.ui.forms.field.ComboField;
import com.graly.promisone.base.ui.forms.field.DateField;
import com.graly.promisone.base.ui.forms.field.FieldType;
import com.graly.promisone.base.ui.forms.field.IField;
import com.graly.promisone.base.ui.forms.field.RadioField;
import com.graly.promisone.base.ui.forms.field.TableListField;
import com.graly.promisone.base.ui.forms.field.TextField;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.StringUtil;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;

public class QueryForm extends EntityForm {
	
	public QueryForm(Composite parent, int style, ADTable table) {
    	super(parent, style, null, table, null);
    }
	
	@Override
    public void createForm(){
        try {
        	if (table != null){
        		for(ADTab tab : table.getTabs()) {
        			if(tab != null) {
        				if(tab.getGridY() != null) {
        					super.setGridY(tab.getGridY().intValue());
        				} else super.setGridY(1);
        				break;
        			} else {
        				super.setGridY(1);
        			}
        		}
				allADfields = table.getFields();
			}
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        addFields();
        createContent();
    }

	@Override
	public void addFields() {
		if (allADfields != null && allADfields.size() > 0){
			for (ADField adField : allADfields) {
	    		if (adField.getIsQuery()) {
	    			adField.setIsReadonly(false);
	    			adField.setIsMandatry(false);
	    			IField field = getField(adField);
	    			if (field == null) {
	    				continue;
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

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}

}
