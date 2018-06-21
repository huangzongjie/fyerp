package com.graly.erp.pur.vendorgoal;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.pur.po.query.PoQuerySection;
import com.graly.erp.pur.po.query.PoQueryTableManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.MasterSectionDialog;
import com.graly.framework.base.ui.util.UI;

public class PoQuerySectionDialog extends MasterSectionDialog {
	private String whereClause = " 1<> 1";

	public PoQuerySectionDialog(Shell parent, ADTable table, String whereClause) {
		super(parent, table);
		this.whereClause = whereClause;
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		section = new PoQuerySection(new PoQueryTableManager(adTable));
		section.setWhereClause(whereClause);
		
		VendorGoalQueryDialog qeuryDialog = new VendorGoalQueryDialog(UI.getActiveShell(),section.getTableManager(), section);
		section.setQueryDialog(qeuryDialog);
	}
}
