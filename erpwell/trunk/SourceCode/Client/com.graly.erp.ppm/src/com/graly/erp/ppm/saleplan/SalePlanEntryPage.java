package com.graly.erp.ppm.saleplan;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class SalePlanEntryPage extends EntityEntryPage {
	protected IManagedForm form;
	protected SalePlanEntityBlock planBlock=null;
	
	public SalePlanEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public SalePlanEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION);
		planBlock = new SalePlanEntityBlock(tableManager);
		block = planBlock;
	}
	
	public SalePlanEntityBlock getPlanBlock() {
		return planBlock;
	}
}
