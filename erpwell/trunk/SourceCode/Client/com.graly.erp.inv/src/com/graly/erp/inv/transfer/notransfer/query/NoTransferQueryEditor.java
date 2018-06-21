package com.graly.erp.inv.transfer.notransfer.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class NoTransferQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.transfer.notransfer.query.NoTransferQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new NoTransferQueryEntryPage(this, "", "");
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
