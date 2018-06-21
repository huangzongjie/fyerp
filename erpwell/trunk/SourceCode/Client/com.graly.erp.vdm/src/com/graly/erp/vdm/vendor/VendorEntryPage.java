package com.graly.erp.vdm.vendor;

import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;

public class VendorEntryPage extends EntityEntryPage {

	public VendorEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		block = new VendorEntityBlock(new EntityTableManager(adTable));
	}
}