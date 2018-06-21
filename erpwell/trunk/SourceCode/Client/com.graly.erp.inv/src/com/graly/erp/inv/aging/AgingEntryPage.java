package com.graly.erp.inv.aging;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class AgingEntryPage extends SectionEntryPage {

	public AgingEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public AgingEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
//		tableManager.addStyle(SWT.CHECK);
		masterSection = new AgingSection(tableManager);
		masterSection.setWhereClause(" 1 <> 1 ");
	}
}
