package com.graly.mes.prd;

import java.util.LinkedHashMap;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.prd.model.Parameter;

public class ParameterProperties extends EntityProperties {
	
	public ParameterProperties() {
		super();
    }
	
	@Override
	protected void saveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()){
					if (!detailForm.saveToObject()){
						saveFlag = false;
					}
				}
				if (saveFlag){
					if(validator()) {
						for (Form detailForm : getDetailForms()){
							PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
						}
						ADManager entityManager = Framework.getService(ADManager.class);
						ADBase obj = entityManager.saveEntity(getTable().getObjectRrn(), getAdObject(), Env.getUserRrn());						
						setAdObject(entityManager.getEntity(obj));
						UI.showInfo(Message.getString("common.save_successed"));//µ¯³öÌáÊ¾¿ò
						masterParent.refresh();
						refresh();									
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public boolean validator() {
		IField typeField = null;
		IField defField = null;
		String type = "";
		String value = "";
		for (Form detailForm : getDetailForms()){
			LinkedHashMap<String, IField> fields = detailForm.getFields();
			typeField = fields.get(Parameter.FIELDNAME_DATETYPE);
			if(typeField instanceof ComboField) {
				ComboField cf = (ComboField)typeField;
				type = (String)cf.getValue();
				defField = fields.get(Parameter.FIELDNAME_DEFAULTVALUE);
				value = (String)defField.getValue();
				break;
			}
		}
		IMessageManager mmng = form.getMessageManager();
		if(!value.equals("")) {
			if (!ValidatorFactory.isValid(type, value)){
				mmng.addMessage(Parameter.FIELDNAME_DEFAULTVALUE + "common.inputerror_title", 
						String.format(Message.getString("common.input_error"), 
								I18nUtil.getI18nMessage(null, "label")), null,
								IMessageProvider.ERROR, defField.getControls()[defField.getControls().length - 1]);
				return false;
			}
		}
		return true;
	}
	
}