package com.graly.erp.inv.workshop.reclaim;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WorkShopReclaimEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.workshop.reclaim.WorkShopReclaimEditor";
	
	protected IFormPage page;
	
	public WorkShopReclaimEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new WorkShopReclaimEntryPage(this, "", "");
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
