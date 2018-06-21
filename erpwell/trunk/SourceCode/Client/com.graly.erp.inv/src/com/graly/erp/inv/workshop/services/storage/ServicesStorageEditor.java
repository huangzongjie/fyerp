package com.graly.erp.inv.workshop.services.storage;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class ServicesStorageEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.workshop.services.storage.ServicesStorageEditor";
	
	protected IFormPage page;
	
	public ServicesStorageEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new ServicesStorageEntryPage(this, "", "");
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
