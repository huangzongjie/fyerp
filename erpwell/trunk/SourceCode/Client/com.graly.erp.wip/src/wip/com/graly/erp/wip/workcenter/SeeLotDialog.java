package com.graly.erp.wip.workcenter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class SeeLotDialog extends LotDialog {
	private static final String TABLE_NAME_INVLOT = "INVLot";
	private ManufactureOrderLine selectMoLine;
	
	public SeeLotDialog(Shell shell, ManufactureOrderLine selectMoLine) {
		super(shell);
		this.table = getAdTableOfMOLine(TABLE_NAME_INVLOT);
		this.selectMoLine = selectMoLine;
	}

	public SeeLotDialog(Shell shell, ADTable table, ManufactureOrderLine selectMoLine) {
		super(shell, table);
		this.selectMoLine = selectMoLine;
	}

	protected void createSection(Composite composite) {
		lotSection = new SeeLotSection(table, selectMoLine);
		lotSection.createContents(managedForm, composite);
	}
	
	private ADTable getAdTableOfMOLine(String tableName) {
		ADTable table = null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			table = adManager.getADTable(0L, tableName);
			table = adManager.getADTableDeep(table.getObjectRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return table;
	}
	
	protected boolean isSureExit() {
		return true;
	}
}
