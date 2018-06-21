package com.graly.erp.inv.transfer.hy.dpk;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class DpkTransferEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.transfer.hy.dpk.DpkTransferEditor";
	
	protected IFormPage page;
	
	public DpkTransferEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new DpkTransferEntryPage(this, "", "");
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
