package com.graly.erp.wip.prepare.tpsline;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.views.TableListManager;

public class PrepareTpsLineEntryPage extends SectionEntryPage {

	public PrepareTpsLineEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public PrepareTpsLineEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		EntityTableManager tableManager = new EntityTableManager(adTable);
		tableManager.addStyle(SWT.CHECK);
		masterSection = new PrepareTpsLineSection(tableManager);
		masterSection.setWhereClause(" 1 <> 1 ");
	}
}
