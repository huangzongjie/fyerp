package com.graly.erp.wip.workcenter.schedule.purchase;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * ÐÐÕþ²Ö¿â¿â´æ²éÑ¯
 * */
public class PurchaseMaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.schedule.purchase.PurchaseMaterialEditor";
	
	protected IFormPage page;
	

	@Override
	protected void addPages() {
		try {
			page = new PurchaseMaterialEntryPage(this, "", "");
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
