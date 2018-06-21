package com.graly.erp.inv.material.qtysquery.js;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;

public class JSMaterialQtyQueryEntryPage extends SectionEntryPage {


	public JSMaterialQtyQueryEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name, table);
	}

	public JSMaterialQtyQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createSection(ADTable adTable) {
		masterSection = new JSMaterialQtyQuerySection(new EntityTableManager(adTable));
	}

}
