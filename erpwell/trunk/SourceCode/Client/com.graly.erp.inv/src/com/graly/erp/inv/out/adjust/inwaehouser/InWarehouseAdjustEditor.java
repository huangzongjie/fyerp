package com.graly.erp.inv.out.adjust.inwaehouser;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.inv.out.adjust.AdjustOutEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class InWarehouseAdjustEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.out.adjust.inwaehouser.InWarehouseAdjustEditor";
	
	protected IFormPage page;
	
	public InWarehouseAdjustEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new InWarehouseAdjustPage(this, "", "");
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
