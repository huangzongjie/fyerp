package com.graly.erp.inv.workshop.requisition;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WorkShopRequestionEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.workshop.requisition.WorkShopRequestionEditor";
	
	protected IFormPage page;
	
	public WorkShopRequestionEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new WorkShopRequestionEntryPage(this, "", "");
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
