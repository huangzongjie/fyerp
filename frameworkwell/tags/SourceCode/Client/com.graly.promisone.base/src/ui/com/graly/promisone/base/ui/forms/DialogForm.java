package com.graly.promisone.base.ui.forms;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.activeentity.model.ADRefTable;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.forms.EntityForm;
import com.graly.promisone.base.entitymanager.views.TableListManager;
import com.graly.promisone.base.ui.forms.field.FieldType;
import com.graly.promisone.base.ui.forms.field.IField;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.StringUtil;
import com.graly.promisone.runtime.Framework;

public class DialogForm extends Form {

	private static final Logger logger = Logger.getLogger(EntityForm.class);

	public DialogForm(Composite parent, int style, Object object) {
		super(parent, style, object);
    }
	
	@Override
	public void addFields() {
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public IField getField(ADField adField){
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if(adField.getIsMandatry()) {
			displayLabel = displayLabel + "*";
		}
		int displayLength = adField.getDisplayLength() != null ? adField.getDisplayLength().intValue() : 32;
		IField field = null;
		if (FieldType.TEXT.equalsIgnoreCase(displayText)){
			if (adField.getIsReadonly()){
				field = createReadOnlyText(name, displayLabel, "");
			} else {
				field = createText(name, displayLabel, displayLength);
			}
			addField(name, field);
		} else if (FieldType.PASSWORD.equalsIgnoreCase(displayText)){
			field = createPassword(name, displayLabel, displayLength);
			addField(name, field);
		} else if (FieldType.BOOLEAN.equalsIgnoreCase(displayText)){
			field = createBooleanField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.DATE.equalsIgnoreCase(displayText)){
			field = createDateField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.DATETIME.equalsIgnoreCase(displayText)){
			field = createDateTimeField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.COMBO.equalsIgnoreCase(displayText) || 
				FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText) ||
				FieldType.LIST.equalsIgnoreCase(displayText) || 
				FieldType.RADIO.equalsIgnoreCase(displayText)){
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			try {
				if (adField.getReferenceName() != null && !"".equalsIgnoreCase(adField.getReferenceName().trim())){
					ADManager entityManager = Framework.getService(ADManager.class);
					List<ADRefList> refList = entityManager.getADRefList(0, adField.getReferenceName());
					for (ADRefList listItem : refList){
						map.put(listItem.getName(), listItem.getValue());
					}
					if (!adField.getIsMandatry()) {
						map.put("", "");
					}
				}
	        } catch (Exception e) {
	        	logger.error("EntityForm : Init listItem", e);
	        }
	        if (FieldType.COMBO.equalsIgnoreCase(displayText)){
		        field = createComboField(name, displayLabel, map);
		        addField(name, field);
	        } else if (FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText)){
				field = createDropDownList(name, displayLabel, map);
				addField(name, field);
			} else if (FieldType.RADIO.equalsIgnoreCase(displayText)){
				field = createRadioGroup(name, displayLabel, map);
				addField(name, field);
			}
		} else if ((FieldType.REFTABLE.equalsIgnoreCase(displayText)) || 
			(FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)) || 
			(FieldType.TABLELIST.equalsIgnoreCase(displayText)) ||
			(FieldType.DUALLIST.equalsIgnoreCase(displayText)) ||
			(FieldType.TABLESELECT.equalsIgnoreCase(displayText))){
			try{
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectId(adField.getRefTableId());
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableId() == null){
					return null;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableId());
				if (FieldType.REFTABLE.equalsIgnoreCase(displayText) ||
						FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
							|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
						List<ADBase> list = entityManager.getEntityList(Env.getOrgId(), adTable.getObjectId(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
						if (!adField.getIsMandatry()) {
							String className = adTable.getModelClass();
							list.add((ADBase)Class.forName(className).newInstance());
						}
						viewer.setInput(list);
					}
					if (FieldType.REFTABLE.equalsIgnoreCase(displayText)){
						field = createRefTableFieldList(name, displayLabel, viewer, refTable);
					} else {
						field = createRefTableFieldCombo(name, displayLabel, viewer, refTable);
					}
					addField(name, field);
				} else if (FieldType.TABLELIST.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					field = createTableListField(name, displayLabel, viewer);
					addField(name, field);
				} else if (FieldType.DUALLIST.equalsIgnoreCase(displayText)){
					List<ADBase> list = entityManager.getEntityList(Env.getOrgId(), adTable.getObjectId(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
					field = createDualListField(name, displayLabel, adTable, list);
					addField(name, field);
				}  else if(FieldType.TABLESELECT.equalsIgnoreCase(displayText)) {
					TableListManager tableManager = new TableListManager(adTable);
					tableManager.setStyle(SWT.CHECK);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					field = createTableItemFilterField(name, displayLabel, viewer, adTable);
					addField(name, field);
				} 
			} catch (Exception e){
				logger.error("EntityForm : Init tablelist", e);
			}
		}
		return field;
	}
	
	@Override
    public boolean saveToObject() {
		return true;
    }

}
