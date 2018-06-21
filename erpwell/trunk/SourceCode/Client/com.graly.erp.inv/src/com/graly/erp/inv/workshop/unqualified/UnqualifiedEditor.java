package com.graly.erp.inv.workshop.unqualified;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

//不良品车间调拨
public class UnqualifiedEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.workshop.unqualified.UnqualifiedEditor";
	
	protected IFormPage page;
	
	public UnqualifiedEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new UnqualifiedEntryPage(this, "", "");
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
