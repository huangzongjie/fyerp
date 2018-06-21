package com.graly.erp.inv.material.locator;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialLocatorEntityEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.material.locator.MaterialLocatorEntityEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {try {
		page = new MaterialLocatorEntityEntryPage(this, "", "");
		addPage(page);
	} catch (PartInitException e) {
		e.printStackTrace();
	}}
}
