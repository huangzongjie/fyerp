package com.graly.erp.inv.material.locator;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class MaterialLocatorEntityEntryPage extends EntityEntryPage {

	public MaterialLocatorEntityEntryPage(FormEditor editor, String id,
			String name) {
		super(editor, id, name);
	}

	@Override
	protected void createBlock(ADTable adTable) {
		block = new MaterialLocatorEntityBlock(new EntityTableManager(adTable));
	}
}
