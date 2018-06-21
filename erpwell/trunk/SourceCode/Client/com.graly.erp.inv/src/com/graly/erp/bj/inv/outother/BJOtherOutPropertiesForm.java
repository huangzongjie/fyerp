package com.graly.erp.bj.inv.outother;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.runtime.Framework;


/**
 * 备件客制化Form：因为有一个其他类型的设备只允许某个特例用户操作,无权限的OP在查询的时候是看不见的
 * */
public class BJOtherOutPropertiesForm extends  ChildEntityForm{

	private static final Logger logger = Logger.getLogger(BJOtherOutPropertiesForm.class);
	
	public BJOtherOutPropertiesForm(Composite parent, int style, Object object,
			ADTab tab, IMessageManager mmng, Object parentObject) {
		super(parent, style, object, tab, mmng, parentObject);
	}

	public BJOtherOutPropertiesForm(Composite parent, int style, Object object,
			ADTable table, IMessageManager mmng, Object parentObject) {
		super(parent, style, object, table, mmng, parentObject);
	}
	
	
	public IField getField(ADField adField){
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if(adField.getIsMandatory()) {
			displayLabel = displayLabel + "*";
		}
		int displayLength = adField.getDisplayLength() != null ? adField.getDisplayLength().intValue() : 32;
		IField field = null;
		if (FieldType.SEPARATOR.equalsIgnoreCase(displayText)){
			field = createSeparatorField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.TEXT.equalsIgnoreCase(displayText)){
			if (adField.getIsReadonly()){
				field = createReadOnlyText(name, displayLabel, "");
			} else if (adField.getIsUpper()){
				field = createUpperText(name, displayLabel, displayLength);
			} else {
				field = createText(name, displayLabel, displayLength);
			}
			addField(name, field);
		} else if (FieldType.TEXTAREA.equalsIgnoreCase(displayText)){
			if (adField.getIsReadonly()){
				field = createReadOnlyTextArea(name, displayLabel, "");
			} else {
				field = createTextArea(name, displayLabel);
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
		} else if (FieldType.SHORTDATE.equalsIgnoreCase(displayText)){
			field = createShortDateField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.DATETIME.equalsIgnoreCase(displayText)){
			field = createDateTimeField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.CALENDAR.equalsIgnoreCase(displayText)) {
			field = createCalendarField(name, displayLabel);
			addField(name, field);
		}else if(FieldType.FROMTO_CALENDAR.equalsIgnoreCase(displayText)){
			field = createFromToCalendarField(name, displayLabel);
			addField(name, field);
		} else if (FieldType.COMBO.equalsIgnoreCase(displayText) || 
				FieldType.DROPDOWNLIST.equalsIgnoreCase(displayText) ||
				FieldType.LIST.equalsIgnoreCase(displayText) || 
				FieldType.RADIO.equalsIgnoreCase(displayText)){
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			try {
				if (adField.getReferenceName() != null && !"".equalsIgnoreCase(adField.getReferenceName().trim())){
					ADManager entityManager = Framework.getService(ADManager.class);
					List<ADRefList> refList = entityManager.getADRefList(Env.getOrgRrn(), adField.getReferenceName());
					for (ADRefList listItem : refList){
						map.put(listItem.getValue(), listItem.getKey());
					}
					if (!adField.getIsMandatory()) {
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
				map.remove("");
				field = createRadioGroup(name, displayLabel, map);
				addField(name, field);
			}
		} else if ((FieldType.REFTABLE.equalsIgnoreCase(displayText)) || 
			(FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)) || 
			(FieldType.TABLELIST.equalsIgnoreCase(displayText)) ||
			(FieldType.DUALLIST.equalsIgnoreCase(displayText)) ||
			(FieldType.TABLESELECT.equalsIgnoreCase(displayText)) || 
			(FieldType.USERREFLIST.equalsIgnoreCase(displayText)) || 
			FieldType.SEARCH.equalsIgnoreCase(displayText)){
			try{
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(adField.getReftableRrn());
				if(adField.getReftableRrn()==null){
					return null;
				}
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null){
					return null;
				}
				refTable.setWhereClause(StringUtil.pareseWhereClause(refTable.getWhereClause()));
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				if (FieldType.REFTABLE.equalsIgnoreCase(displayText) ||
						FieldType.REFTABLECOMBO.equalsIgnoreCase(displayText)){
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
							|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
						List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
    					if (!adField.getIsMandatory()) {
							String className = adTable.getModelClass();
							list.add((ADBase)Class.forName(className).newInstance());
						}
    					viewer.setInput(list);
					}
					if (adField.getIsReadonly()){
						field = createRefTableFieldComboReadOnly(name, displayLabel, viewer, refTable);
					} else if (FieldType.REFTABLE.equalsIgnoreCase(displayText)){
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
					List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
					field = createDualListField(name, displayLabel, adTable, list);
					addField(name, field);
				} else if(FieldType.TABLESELECT.equalsIgnoreCase(displayText)) {
					String whereClause = "";
					if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
						whereClause = refTable.getWhereClause();
					}
					TableListManager tableManager = new TableListManager(adTable);
					tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION |SWT.BORDER 
							| SWT.H_SCROLL | SWT.V_SCROLL | SWT.HIDE_SELECTION);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
					field = createTableItemFilterField(name, displayLabel, viewer, adTable, whereClause);
					addField(name, field);
				} else if(FieldType.SEARCH.equalsIgnoreCase(displayText)) {
					String whereClause = "";
					if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
						whereClause = refTable.getWhereClause();
					}
					if(name!=null && name.equals("equipmentRrn")){
						field = createSearchField(name, displayLabel, adTable, refTable, whereClause, SWT.BORDER);
					}else{
						field = super.createSearchField(name, displayLabel, adTable, refTable, whereClause, SWT.BORDER);
					}
					addField(name, field);
				} else if(FieldType.USERREFLIST.equalsIgnoreCase(displayText)) {
					TableListManager tableManager = new TableListManager(adTable);
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));

					String whereClause = " referenceName = '" + adField.getUserReflistName() + "'";
					List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
							Env.getMaxResult(), whereClause, refTable.getOrderByClause());
					if (!adField.getIsMandatory()) {
						String className = adTable.getModelClass();
						list.add((ADBase)Class.forName(className).newInstance());
					}
					viewer.setInput(list);
					if (adField.getIsReadonly()){
						field = createRefTableFieldComboReadOnly(name, displayLabel, viewer, refTable);
					} else {
						field = createRefTableFieldList(name, displayLabel, viewer, refTable);
					}
					
					addField(name, field);
				} 
			} catch (Exception e){
				logger.error("EntityForm : Init tablelist", e);
			}
		} else if(FieldType.IMAGE.equals(displayText)) {
			field = createImageField(name, displayLabel, null);
			addField(name, field);
		} else if (FieldType.URL.equalsIgnoreCase(displayText)) {
			field = createUrlField(name, displayLabel, SWT.BORDER);
			addField(name, field);
		} else if (FieldType.REFTEXT.equalsIgnoreCase(displayText)) {
			field = createRefTextField(name, displayLabel, displayLength);
			addField(name, field);
		} else if (FieldType.HIDDEN.equalsIgnoreCase(displayText)) {
			field = createHiddenField(name);
			addField(name, field);
		}
		return field;
	}
	
	
    public SearchField createSearchField(String id, String label, ADTable adTable,
    		ADRefTable refTable, String whereClause, int style) {
    	BJSearchField fe = new BJSearchField(id, adTable, refTable, whereClause, style);
    	fe.setLabel(label);
    	return fe;
    }
	
}
