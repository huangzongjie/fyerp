package com.graly.framework.base.entitymanager.query;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

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
	// ��ѯʱ������вֿ���λ�������λ�Ĳο���ӦΪWarehouseIdList,������VWarehouseUserList
	// ����ĳ�ֿ��ѯʱ����Ҫ�û�ӵ�иòֿ��Ȩ��,������ο���ΪVWarehouseUserList,������ظ��Ĳֿ�
	public IField getField(ADField adField) {
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if (adField.getIsMandatory()) {
			displayLabel = displayLabel + "*";
		}
		IField field = null;
		if (FieldType.REFTABLE.equalsIgnoreCase(displayText)
				&& (name.equals("warehouseRrn") || name
						.equals("targetWarehouseRrn"))) {
			try {
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(adField.getReftableRrn());
				refTable = (ADRefTable) entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null) {
					return null;
				}
				ADTable adTable = entityManager.getADTable(refTable
						.getTableRrn());
				TableListManager tableManager = new TableListManager(adTable);
				TableViewer viewer = (TableViewer) tableManager.createViewer(
						getShell(), new FormToolkit(getShell().getDisplay()));
				String where = " userRrn = " + Env.getUserRrn()
						+ " AND (isVirtual = 'N' OR isVirtual is null)";
				List<ADBase> list = entityManager.getEntityList(
						Env.getOrgRrn(), adTable.getObjectRrn(), Env
								.getMaxResult(), where, refTable
								.getOrderByClause());
				if (!adField.getIsMandatory()) {
					String className = adTable.getModelClass();
					list.add((ADBase) Class.forName(className).newInstance());
				}
				viewer.setInput(list);
				field = createRefTableFieldList(name, displayLabel, viewer,
						refTable);
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
