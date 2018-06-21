package com.graly.erp.base;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialQueryForm extends QueryForm {

	public MaterialQueryForm(Composite parent, int style, ADTable table) {
    	super(parent, style, table);
    }
	
	// 查询时，如果有仓库栏位，则该栏位的参考表应为WarehouseIdList,而不是VWarehouseUserList
	// 即按某仓库查询时不需要用户拥有该仓库的权限,因此若参考表为VWarehouseUserList,会出现重复的仓库
	public IField getField(ADField adField) {
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if (adField.getIsMandatory()) {
			displayLabel = displayLabel + "*";
		}
		IField field = null;
		if (FieldType.REFTABLE.equalsIgnoreCase(displayText) && (name.equals("warehouseRrn")||name.equals("targetWarehouseRrn"))) {
			try {
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(adField.getReftableRrn());
				refTable = (ADRefTable) entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null) {
					return null;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				TableListManager tableManager = new TableListManager(adTable);
				TableViewer viewer = (TableViewer) tableManager.createViewer(getShell(), new FormToolkit(getShell().getDisplay()));
				String where = " userRrn = " + Env.getUserRrn() + " AND (isVirtual = 'N' OR isVirtual is null)";
				List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), where, refTable
						.getOrderByClause());
				if (!adField.getIsMandatory()) {
					String className = adTable.getModelClass();
					list.add((ADBase) Class.forName(className).newInstance());
				}
				viewer.setInput(list);
				field = createRefTableFieldList(name, displayLabel, viewer, refTable);
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
}
