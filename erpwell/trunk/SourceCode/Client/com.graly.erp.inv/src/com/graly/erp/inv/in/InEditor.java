package com.graly.erp.inv.in;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class InEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.in.InEditor";
	
	protected IFormPage page;
	
	public InEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new InEntryPage(this, "", "");
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