package com.graly.erp.inv.workshop.services.query;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class ServicesQueryEntryPage extends SectionEntryPage {

	public ServicesQueryEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name, table);
	}

	public ServicesQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new ServicesQuerySection(new EntityTableManager(adTable));
	}

}
