package com.graly.erp.test.cus;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class UserAddForm extends EntityForm {
	private final String FIELD_ID = "prical";
	
	public UserAddForm(Composite parent, int style, ADTable table, IMessageManager mmng) {
    	this(parent, style, null, table, mmng);
    }
    
    public UserAddForm(Composite parent, int style, Object object, ADTable table, IMessageManager mmng) {
		super(parent, style, object, table, mmng);
    }
    
    @Override
    public void createForm(){
        try {
			allADfields = table.getFields();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
    }
    
    @Override
	public void addFields() {
		if (allADfields != null && allADfields.size() > 0){
			for (ADField adField : allADfields) {
	    		if (FIELD_ID.equals(adField.getName()) && adField.getIsDisplay()) {
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
		}
	}
}
