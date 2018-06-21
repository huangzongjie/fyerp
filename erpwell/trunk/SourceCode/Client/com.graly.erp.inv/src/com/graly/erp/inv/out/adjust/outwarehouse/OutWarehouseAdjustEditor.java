package com.graly.erp.inv.out.adjust.outwarehouse;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.inv.out.adjust.AdjustOutEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class OutWarehouseAdjustEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.out.adjust.outwarehouse.OutWarehouseAdjustEditor";
	
	protected IFormPage page;
	
	public OutWarehouseAdjustEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new OutWarehouseAdjustPage(this, "", "");
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
