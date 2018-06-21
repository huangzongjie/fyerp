package com.graly.erp.inv.out.adjust.inwaehouser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.adjust.ByLotAdjustOutSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.activeentity.model.ADUserRefList;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.ADFieldUtil;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class InWarehouseByLotAdjustOutSection extends ByLotAdjustOutSection {
	protected MovementOut out;

	public InWarehouseByLotAdjustOutSection(ADTable adTable,
			LotDialog parentDialog) {
		super(adTable, parentDialog);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createOutTypeContent(Composite parent, FormToolkit toolkit)
			throws Exception {
	
		ADTable refAdTable = this.getADTableBy(TABLE_NAME_REFTABLE);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY_USER_REF);
		refTable.setValueField(VALUE_USER_ERF);
		refTable.setTableRrn(refAdTable.getObjectRrn());
		String whereClause =  " referenceName = '" + ADJUSTOUT_TYPE + "'";
		refTable.setWhereClause(whereClause);
		TableListManager tableManager = new TableListManager(refAdTable);
		TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0) {
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), refAdTable.getObjectRrn(),
					Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
			viewer.setInput(list);
		}
		outTypeFiled = ADFieldUtil.createRefTableFieldList("outType", Message.getString("inv.out_type"), viewer, refTable);
		outTypeFiled.createContent(parent, toolkit);
		outTypeFiled.setValue("Èë¿âµ÷Õû");
		outTypeFiled.refresh();
		outTypeFiled.setEnabled(false);
	}
	
}
