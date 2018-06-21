package com.graly.erp.inv.material;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.wip.model.VWorkShopStorage;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.runtime.Framework;

public class WorkShopStorageLotDialog extends LotDialog {
	private VWorkShopStorage selectedWorkShopStorage;
	private String tableName = "INVLot";
	public WorkShopStorageLotDialog(Shell shell) {
		super(shell);
	}

	public WorkShopStorageLotDialog(Shell shell, VWorkShopStorage selectedWorkShopStorage) {
		super(shell);
		this.selectedWorkShopStorage = selectedWorkShopStorage;
	}

	protected void createSection(Composite composite) {
		lotSection = new WorkShopStorageLotSection(table, this, selectedWorkShopStorage);
		lotSection.createContents(managedForm, composite);
	}
	
	protected boolean isSureExit() {
		return true;
	}
	
	protected ADTable getADTableOfInvLot() {
		try {
			if(table == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTable(0L, getADTableName());
			}
			return table;
		} catch(Exception e) {
		}
		return null;
	}
}
