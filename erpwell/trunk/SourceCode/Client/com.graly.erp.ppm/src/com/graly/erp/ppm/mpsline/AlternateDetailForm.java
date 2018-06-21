package com.graly.erp.ppm.mpsline;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlternateDetailForm extends ChildEntityForm {
	private static final Logger logger = Logger.getLogger(AlternateDetailForm.class);
	private static final String FIELD_UnitQty = "unitQty";
	private static final String FIELD_Des = "description";
	private AlternateDialog parentDialog;

	public AlternateDetailForm(Composite parent, int style, Object object, ADTable table,
			IMessageManager mmng, Object parentObject) {
		super(parent, style, object, table, mmng, parentObject);
		createFormContent();
	}
	
	@Override
    public void createForm() {
		this.setGridY(2);
	}

    public void createFormContent() {
        try {
        	if (table != null) {
				allADfields = table.getFields();
				for(ADField adField : allADfields) {
					if(FIELD_UnitQty.equals(adField.getName())) {
						adField.setIsDisplay(true);
						adField.setIsMandatory(true);
						break;
					}
				}
			}
        	addFields();
        	createContent();
        	loadFromObject();
        } catch (Exception e) {
        	logger.error("MOAlternateDetailForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
    }

	public void setEnabled(){
		super.setEnabled();
		for (IField f : fields.values()){
    		if(!FIELD_UnitQty.equals(f.getId()) && !FIELD_Des.equals(f.getId())) {
    			f.setEnabled(false);
    		}
		}
	}

	@Override
    public void loadFromObject() {
		super.loadFromParent();
		super.loadFromObject();
    }

	public void setParentDialog(AlternateDialog parentDialog) {
		this.parentDialog = parentDialog;
	}
	
	@Override
    public boolean saveToObject() {
		this.setErrorMessage(null);
		return super.saveToObject();
    }
	
	@Override
	public boolean validate() {
		boolean validFlag = true;
		for (IField f : fields.values()){
			ADField adField = adFields.get(f.getId());
			if(adField != null){
				if (adField.getIsMandatory()){
					Object value = f.getValue();
					boolean isMandatory = false;
					if (value == null){
						isMandatory = true;
					} else {
						if (value instanceof String){
							if ("".equalsIgnoreCase(value.toString().trim())){
								isMandatory = true;
							}
						}
					}
					if (isMandatory){
						validFlag = false;
						setErrorMessage(String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")));
						break;
					}
				}
				if (adField.getDataType() != null && !"".equalsIgnoreCase(adField.getDataType().trim())){
					if (!(f.getValue() instanceof String)){
						continue;
					}
					String value = (String)f.getValue();
					if (value != null && !"".equalsIgnoreCase(value.trim())){
						if (!ValidatorFactory.isValid(adField.getDataType(), value)){
							validFlag = false;
							setErrorMessage(String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label"), adField.getDataType()));
							break;
						} else if (!ValidatorFactory.isInRange(adField.getDataType(), value, adField.getMinValue(), adField.getMaxValue())){
							validFlag = false;
							if ((adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim()))
							 && (adField.getMaxValue() != null && !"".equalsIgnoreCase(adField.getMaxValue().trim()))){
								setErrorMessage(String.format(Message.getString("common.between"), I18nUtil.getI18nMessage(adField, "label"),
										adField.getMinValue(), adField.getMaxValue()));
								break;
							} else if (adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim())){
								setErrorMessage(String.format(Message.getString("common.largerthan"),
										I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue()));
								break;
							} else {
								setErrorMessage(String.format(Message.getString("common.lessthan"),
										I18nUtil.getI18nMessage(adField, "label"), adField.getMaxValue()));
								break;
							}
						}
					}
				}
				if (adField.getNamingRule() != null && !"".equalsIgnoreCase(adField.getNamingRule().trim())){
					Object value = f.getValue();
					if (value == null){
						continue;
					}
					if (value instanceof String){
						if (!Pattern.matches(adField.getNamingRule(), value.toString())) {
							validFlag = false;
							setErrorMessage(String.format(Message.getString("common.namingrule_error"), I18nUtil.getI18nMessage(adField, "label")));
							break;
						}
					}
				}
			}
		}
		return validFlag;
	}
	
	protected void setErrorMessage(String message) {
		if(parentDialog != null) {
			parentDialog.setErrorMessage(message);
		}
	}

}
