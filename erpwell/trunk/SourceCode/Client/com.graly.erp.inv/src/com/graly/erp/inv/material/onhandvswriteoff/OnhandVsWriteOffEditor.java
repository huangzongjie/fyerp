package com.graly.erp.inv.material.onhandvswriteoff;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OnhandVsWriteOffEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.material.onhandvswriteoff.OnhandVsWriteOffEditor";
	
	protected IFormPage page;
	

	@Override
	protected void addPages() {
		try {
			page = new OnhandVsWriteOffEntryPage(this, "", "");
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
