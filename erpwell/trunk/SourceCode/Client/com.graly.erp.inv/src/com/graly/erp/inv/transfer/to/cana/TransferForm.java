package com.graly.erp.inv.transfer.to.cana;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;
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
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class TransferForm extends EntityForm {
	public TransferForm(Composite parent, int style, ADTab tab, IMessageManager mmng) {
		super(parent, style, tab, mmng);
	}

	public TransferForm(Composite parent, int style, Object object, ADTab tab, IMessageManager mmng) {
		super(parent, style, object, tab, mmng);
	}

	public IField getField(ADField adField) {
		String displayText = adField.getDisplayType();
		String name = adField.getName();
		String displayLabel = I18nUtil.getI18nMessage(adField, "label");
		if (adField.getIsMandatory()) {
			displayLabel = displayLabel + "*";
		}
		IField field = null;
		if (FieldType.REFTABLE.equalsIgnoreCase(displayText) && (name.equals("targetWarehouseRrn"))) {
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
				String where = " userRrn = " + Env.getUserRrn() + " AND (isVirtual = 'N' OR isVirtual is null) and objectRrn =42803113";
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
