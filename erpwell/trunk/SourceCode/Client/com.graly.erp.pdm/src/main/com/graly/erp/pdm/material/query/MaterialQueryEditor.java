package com.graly.erp.pdm.material.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class MaterialQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.pdm.material.query.MaterialQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MaterialQueryEntryPage(this, "", "");
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
