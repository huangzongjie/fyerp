package com.graly.erp.inv.workshop.delivery;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WorkShopDeliveryEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.workshop.delivery.WorkShopDeliveryEditor";
	
	protected IFormPage page;
	
	public WorkShopDeliveryEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new WorkShopDeliveryEntryPage(this, "", "");
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
