package com.graly.erp.pur.msm;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.views.TableListManager;

public class MinStorageMaterialPage extends SectionEntryPage {
	
	public MinStorageMaterialPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public MinStorageMaterialPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		TableListManager tableManager = new TableListManager(adTable);
		tableManager.addStyle(SWT.CHECK);
		masterSection = new MinStorageMaterialSection(tableManager);
		masterSection.setWhereClause(" 1 <> 1 ");
	}
}
