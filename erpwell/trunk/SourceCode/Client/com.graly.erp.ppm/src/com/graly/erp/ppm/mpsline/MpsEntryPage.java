package com.graly.erp.ppm.mpsline;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class MpsEntryPage extends EntityEntryPage {
	protected IManagedForm form;
	protected MpsEntityBlock planBlock = null;

	public MpsEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name);
	}

	public MpsEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION);
		planBlock = new MpsEntityBlock(tableManager);
		block = planBlock;
	}

	public MpsEntityBlock getPlanBlock() {
		return planBlock;
	}
}
