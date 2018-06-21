package com.graly.erp.inv.out;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OutEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.out.OutEditor";
	
	protected IFormPage page;
	
	public OutEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new OutEntryPage(this, "", "");
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
