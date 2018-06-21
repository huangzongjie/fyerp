package com.graly.erp.bj.pdm.material;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class BJMaterialEntryPage extends EntityEntryPage {

	public BJMaterialEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		block = new BJMaterialEntityBlock(new EntityTableManager(adTable));
	}
}