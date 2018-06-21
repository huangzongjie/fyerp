package com.graly.erp.inv.out;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class OutEntryPage extends SectionEntryPage {
	
	public OutEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public OutEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new OutSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docStatus in ('APPROVED', 'DRAFTED') ");
	}
}
