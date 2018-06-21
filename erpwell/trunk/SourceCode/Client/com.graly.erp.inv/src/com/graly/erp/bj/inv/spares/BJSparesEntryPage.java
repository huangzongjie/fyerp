package com.graly.erp.bj.inv.spares;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class BJSparesEntryPage extends SectionEntryPage {


	public BJSparesEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name, table);
	}

	public BJSparesEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new BJSparesSection(new EntityTableManager(adTable));
	}

}
