package com.graly.erp.vdm.vendormaterial;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class VendorMaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.vdm.vendormaterial.VendorMaterialEditor";
	protected IFormPage page;

	@Override
	protected void addPages() {
		try {
			page = new VendorMaterialEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		page.setFocus();
	}
}
