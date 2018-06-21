package com.graly.erp.inv.material.storage.list;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class MaterialStorageListEntryPage extends SectionEntryPage {


	public MaterialStorageListEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name, table);
	}

	public MaterialStorageListEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MaterialStorageListSection(new EntityTableManager(adTable));
	}

}
