package com.graly.erp.pur.request;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class RequisitionEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.pur.request.RequisitionEditor";
	
	protected IFormPage page;
	
	public RequisitionEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new RequisitionEntryPage(this, "", "");
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
