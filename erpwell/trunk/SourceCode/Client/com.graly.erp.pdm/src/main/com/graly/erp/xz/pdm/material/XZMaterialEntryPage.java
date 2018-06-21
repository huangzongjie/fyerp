package com.graly.erp.xz.pdm.material;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class XZMaterialEntryPage extends EntityEntryPage {

	public XZMaterialEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		block = new XZMaterialEntityBlock(new EntityTableManager(adTable));
	}
}