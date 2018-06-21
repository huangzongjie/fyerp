package com.graly.erp.inv.in;

import org.eclipse.swt.widgets.Shell;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.MasterSectionDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class UnWriteOffMovemetDialog extends MasterSectionDialog {

	public UnWriteOffMovemetDialog(Shell parentShell, ADTable adTable) {
		super(parentShell, adTable);
	}

	@Override
	protected void createSection(ADTable adTable) {
		section = new UnWriteOffMovementSection(new EntityTableManager(adTable));
	}
}
