package com.graly.erp.inv.material.kegongquery;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class MaterialQtyQueryEntryPage extends SectionEntryPage {


	public MaterialQtyQueryEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name, table);
	}

	public MaterialQtyQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new MaterialQtyQuerySection(new EntityTableManager(adTable));
	}

}
