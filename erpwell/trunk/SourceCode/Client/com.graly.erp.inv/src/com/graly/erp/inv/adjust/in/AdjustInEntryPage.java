package com.graly.erp.inv.adjust.in;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class AdjustInEntryPage extends SectionEntryPage {
	public AdjustInEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public AdjustInEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new AdjustInSection(new EntityTableManager(adTable));
		masterSection.setWhereClause(" docType in ('ADIN') ");
	}
}
