package com.graly.erp.pdm.bomedit;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.validator.ValidatorFactory;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomEditDialog extends EntityDialog {
	private static final Logger logger = Logger.getLogger(BomEditDialog.class);
	private BomTreeDialog btd;
	private boolean isContainsOptional = false;
	
	public BomEditDialog(Shell parent, ADTable table, Bom editorBom, BomTreeDialog btd){
		super(parent, table, editorBom);
		this.btd = btd;
		isContainsOptional();
	}
	
	// 重载该方法实现验证错误时提示信息放在Dialog头部(放在放在各个控件前端会出现滚动条)
	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		final IMessageManager mmng = managedForm.getMessageManager();
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		EntityForm itemForm = new BomEditForm(body, SWT.NONE, adObject, table, mmng, this);
		itemForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		getDetailForms().add(itemForm);
	}
	
	protected void isContainsOptional() {
		//如果备注不为空并且备注的含有可选料信息，则表示已有可选料
		if(adObject instanceof Bom) {
			Bom bom = (Bom)adObject;
			if(bom.getDescription() != null
					&& bom.getDescription().indexOf(OptionalProperties.OPTIONAL_MATERIAL) != -1) {
				isContainsOptional = true;
			}
		}
	}
	
	@Override
    protected void okPressed() {
		if(dialogType.equals(DIALOGTYPE_NEW)) {
			try {
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				Bom newBom = pdmManager.newBom(btd.material);
				setAdObject(newBom);
				btd.setIsChanged(true);//设置操作标志
				refresh();
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}
			setReturnCode(OK);
		} else if(dialogType.equals(DIALOGTYPE_EDIT)) {
			btd.refreshEditorBom((Bom)getAdObject());
			Bom bom = (Bom)getAdObject();
			if(!(bom.getChildMaterial().getObjectRrn().compareTo(bom.getChildRrn()) == 0)){
				btd.setIsChanged(true);//设置操作标志
			}
			super.okPressed();
		}
    }
	
	@Override
	protected void cancelPressed() {
		btd.addNewBom((Bom)getAdObject());
		super.cancelPressed();
	}

	@Override
	protected boolean saveAdapter() {
		try {
			managedForm.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						setAdObject((Bom)detailForm.getObject());
					}
					
					if(dialogType.equals(DIALOGTYPE_EDIT) && isContainsOptional) {
						// 判断编辑后的BOM的描述是否含有：可选料，若没有则加上
						Bom bom = (Bom)getDetailForms().get(0).getObject();
						if(bom.getDescription() == null
								|| bom.getDescription().indexOf(OptionalProperties.OPTIONAL_MATERIAL) == -1) {
							String des = bom.getDescription() == null ?
									OptionalProperties.OPTIONAL_MATERIAL : OptionalProperties.OPTIONAL_MATERIAL + bom.getDescription();
							bom.setDescription(des);
						}
					}
					
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					setAdObject( pdmManager.saveBom((Bom)getAdObject(), Env.getUserRrn()) );					
					UI.showInfo(Message.getString("common.save_successed"));
					return true;
				} return false;
			}
		} catch (Exception e) {
			logger.error("Error at BomEditorDialog saveAdapter() : " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
		return false;
	}
	
	class BomEditForm extends EntityForm {
		protected BomEditDialog dialog;
		public BomEditForm(Composite parent, int style, Object obj, ADTable table,
				IMessageManager mmng, BomEditDialog dialog) {
	    	super(parent, style, obj, table, mmng);
	    	this.dialog = dialog;
	    }

		// 重载该方法, 使错误提示信息放Dialog的头部
		@Override
		public boolean validate() {
			this.setErrorMessage(null);
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
							this.setErrorMessage(String.format(Message.getString("common.ismandatory"), I18nUtil.getI18nMessage(adField, "label")));
							return validFlag;
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
								this.setErrorMessage(String.format(Message.getString("common.isvalid"), I18nUtil.getI18nMessage(adField, "label"), adField.getDataType()));
								return validFlag;
							} else if (!ValidatorFactory.isInRange(adField.getDataType(), value, adField.getMinValue(), adField.getMaxValue())){
								validFlag = false;
								if ((adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim()))
								 && (adField.getMaxValue() != null && !"".equalsIgnoreCase(adField.getMaxValue().trim()))){
									this.setErrorMessage(String.format(Message.getString("common.between"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue(), adField.getMaxValue()));
								} else if (adField.getMinValue() != null && !"".equalsIgnoreCase(adField.getMinValue().trim())){
									this.setErrorMessage(String.format(Message.getString("common.largerthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMinValue()));
								} else {
									this.setErrorMessage(String.format(Message.getString("common.lessthan"), I18nUtil.getI18nMessage(adField, "label"), adField.getMaxValue()));
								}
								return validFlag;
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
								this.setErrorMessage(String.format(Message.getString("common.namingrule_error"), I18nUtil.getI18nMessage(adField, "label")));
								return validFlag;
							}
						}
					}
				}
				
			}
			return validFlag;
		}
		
		protected void setErrorMessage(String errMeg) {
			if(dialog != null) {
				dialog.setErrorMessage(errMeg);
			}
		}
	}
	
}
