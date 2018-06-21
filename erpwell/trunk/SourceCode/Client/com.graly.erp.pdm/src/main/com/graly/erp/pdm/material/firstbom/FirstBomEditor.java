package com.graly.erp.pdm.material.firstbom;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class FirstBomEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.pdm.material.firstbom.FirstBomEditor";
	
	protected IFormPage page;
	

	@Override
	protected void addPages() {
		try {
			page = new FirstBomEntryPage(this, "", "");
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
