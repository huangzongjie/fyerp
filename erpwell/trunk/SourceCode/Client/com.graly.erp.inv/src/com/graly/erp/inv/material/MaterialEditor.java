package com.graly.erp.inv.material;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.material.MaterialEditor";
	
	protected IFormPage page;
	

	@Override
	protected void addPages() {
		try {
			page = new MaterialEntryPage(this, "", "");
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
