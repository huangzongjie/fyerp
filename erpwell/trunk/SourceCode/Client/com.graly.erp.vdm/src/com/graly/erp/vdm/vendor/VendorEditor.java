package com.graly.erp.vdm.vendor;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class VendorEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.vdm.vendor.VendorEditor";
	
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new VendorEntryPage(this, "", "");
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