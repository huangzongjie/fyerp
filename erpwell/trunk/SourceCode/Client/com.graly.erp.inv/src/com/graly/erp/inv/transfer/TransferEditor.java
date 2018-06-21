package com.graly.erp.inv.transfer;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class TransferEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.transfer.TransferEditor";
	
	protected IFormPage page;
	
	public TransferEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new TransferEntryPage(this, "", "");
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
