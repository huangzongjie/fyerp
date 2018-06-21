package com.graly.erp.inv.transfer.to.cana;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class TransferToCanaEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.transfer.to.cana.TransferToCanaEditor";
	
	protected IFormPage page;
	
	public TransferToCanaEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new TransferToCanaEntryPage(this, "", "");
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
