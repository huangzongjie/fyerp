package com.graly.erp.inv.transfer.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class TransferQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.transfer.query.TransferQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new TransferQueryEntryPage(this, "", "");
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
