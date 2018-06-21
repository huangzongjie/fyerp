package com.graly.erp.xz.pur.po;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.po.form.GroupField;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class XZPOForm extends EntityForm {
	private static final Logger logger = Logger.getLogger(XZPOForm.class);
	private static final String PreFix_PaymentRule = "paymentRule";
	private static final String PreFix_QualityRule = "qualityRule";
	private static final String InvoiceType = "invoiceType"; // 发票类型
	private static final String VatRate = "vatRate"; // 增值税率(默认0.17)
	private LinkedHashMap<String, GroupField> groupFields;
	
	private List<ADField> paymentRules;
	private List<ADField> qualityRules;
	
	public XZPOForm(Composite parent, int style, Object object, ADTable table, IMessageManager mmng) {
		super(parent, style, object, table, mmng);
	}
    
	public XZPOForm(Composite parent, int style, Object object, ADTab tab, IMessageManager mmng) {
		super(parent, style, object, tab, mmng);
    }
	
	@Override
    public void createForm(){
        try {
        	List<ADField> fields = null;
        	paymentRules = new ArrayList<ADField>();
        	qualityRules = new ArrayList<ADField>();
        	groupFields = new LinkedHashMap<String, GroupField>(4, (float)0.75, false);
        	if (table != null) {
        		fields = table.getFields();
        	} else if (tab != null){
				if (tab.getGridY() > 1){
					super.setGridY(tab.getGridY().intValue());
				}
				fields = tab.getFields();
			}
        	setFieldsToAllADfields(fields);
        } catch (Exception e) {
        	logger.error("EntityForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        
        addFields();
        createContent();
        if (object != null) {
            loadFromObject();
        }
    }
	
	protected void setFieldsToAllADfields(List<ADField> fields) {
		//将关于支付方式和质量标准的ADField分开，不赋给allADfields
		for(ADField adField : fields) {
			String id = adField.getName();
			if((id.startsWith(PreFix_PaymentRule) && !PreFix_PaymentRule.equals(id))
					|| id.equals(InvoiceType) || id.equals(VatRate)) {
				paymentRules.add(adField);
			} else if(id.startsWith(PreFix_QualityRule) && !PreFix_QualityRule.equals(id)) {
				qualityRules.add(adField);
			} else {
				allADfields.add(adField);
			}
		}
	}
	
	public IField getField(ADField adField){
		IField field = null;
		String name = adField.getName();
		// 如果为支付方式或质量标准则创建GroupField
		if(PreFix_PaymentRule.equals(name)) {
			String displayLabel = I18nUtil.getI18nMessage(adField, "label");
			// object为PurchaseOrder
			field = new GroupField(PreFix_PaymentRule, SWT.BORDER, object,
					paymentRules, mmng, new int[]{4, 3, 3, 3});
			field.setLabel(displayLabel);
			addField(name, field);
			groupFields.put(name, (GroupField)field);
		} else if(PreFix_QualityRule.equals(name)) {
			String displayLabel = I18nUtil.getI18nMessage(adField, "label");
			field = new GroupField(PreFix_QualityRule, SWT.BORDER, object,
					qualityRules, mmng, new int[]{4});
			field.setLabel(displayLabel);
			addField(name, field);
			groupFields.put(name, (GroupField)field);
		} else {
			field = super.getField(adField);
		}
		return field;
	}
	
	public void setEnabled(){
		super.setEnabled();
		for (IField f : groupFields.values()){
			ADField adField = (ADField)f.getADField();
			if (adField != null){
				f.setEnabled(false);
			}
		}
	}

	@Override
    public void loadFromObject() {
		if (object != null){
			for (IField f : fields.values()){
				if (!(f instanceof SeparatorField || f instanceof RefTextField || f instanceof GroupField
						|| ((ADField)f.getADField()).getIsParent())){
					Object o = PropertyUtil.getPropertyForIField(object, f.getId());
					f.setValue(o);
				}
			}
			//更新GroupField
			for (IField f : groupFields.values()){
				((GroupField)f).loadValue();
			}
			refresh();
			setEnabled();
		}
    }
	
	@Override
    public boolean saveToObject() {
		if(object != null) {
			if (!validate()){
				return false;
			}
			for (IField f : fields.values()){
//				if (!(f instanceof SeparatorField || f.isReadOnly() || f instanceof GroupField)) {
				if (!(f instanceof SeparatorField || f instanceof GroupField)) {
					PropertyUtil.setProperty(object, f.getId(), f.getValue());
				}
			}
			//保存GroupField
			for (IField f : groupFields.values()){
				if(!((GroupField)f).saveValue()) {
					return false;
				}
			}
			return true;
		}
		return false;
    }

	public void setObject(Object obj) {
		super.setObject(obj);
		for (GroupField gf : groupFields.values()){
			gf.setObject(obj);
		}
	}

    public LinkedHashMap<String, IField> getFields() {
    	if(groupFields == null || groupFields.size() == 0) {
    		return fields;
    	} else {
    		for (IField f : groupFields.values()){
    			return ((GroupField)f).getFields();
			}
    	}
    	return null;
    }

}
