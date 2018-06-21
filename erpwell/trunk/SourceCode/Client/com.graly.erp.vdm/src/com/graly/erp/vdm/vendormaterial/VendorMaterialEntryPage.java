package com.graly.erp.vdm.vendormaterial;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEntryPage;

public class VendorMaterialEntryPage extends EntityEntryPage {
	protected IManagedForm form;
	protected VendorMaterialEntityBlock planBlock=null;
	
	public VendorMaterialEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public VendorMaterialEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	protected void createBlock(ADTable adTable) {
		block = new VendorMaterialEntityBlock(new VendorMaterialTableManager(adTable));
	}
}
