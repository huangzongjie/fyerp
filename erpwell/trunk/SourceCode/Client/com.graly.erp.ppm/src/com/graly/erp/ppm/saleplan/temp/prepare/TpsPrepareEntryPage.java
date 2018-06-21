package com.graly.erp.ppm.saleplan.temp.prepare;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.erp.ppm.saleplan.SalePlanEntryPage;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class TpsPrepareEntryPage extends SalePlanEntryPage {
	protected IManagedForm form;
	protected TpsPrepareEntityBlock tpsBlock;
	
	public TpsPrepareEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public TpsPrepareEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		tableManager.setStyle(SWT.CHECK | SWT.FULL_SELECTION);
		tpsBlock = new TpsPrepareEntityBlock(tableManager);
		block = tpsBlock;
		block.setWhereClause(" isGenerate = 'N' ");
	}
	
	public TpsPrepareEntityBlock getPlanBlock() {
		return tpsBlock;
	}
}
