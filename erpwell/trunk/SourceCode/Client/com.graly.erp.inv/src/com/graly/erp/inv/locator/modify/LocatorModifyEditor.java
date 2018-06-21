package com.graly.erp.inv.locator.modify;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class LocatorModifyEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.locator.modify.LocatorModifyEditor";
	
	protected IFormPage page;
	
	public LocatorModifyEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new LocatorModifyEntryPage(this, "", "");
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
