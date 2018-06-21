package com.graly.erp.ppm.saleplan.temp;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.erp.ppm.saleplan.SalePlanEntryPage;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityBlock;

public class TpsEntryPage extends SalePlanEntryPage {
	protected IManagedForm form;
	protected TpsEntityBlock tpsBlock;
	
	public TpsEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public TpsEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION);
		tpsBlock = new TpsEntityBlock(tableManager);
		block = tpsBlock;
		block.setWhereClause(" isGenerate = 'N' ");
	}
	
	public TpsEntityBlock getPlanBlock() {
		return tpsBlock;
	}
}
