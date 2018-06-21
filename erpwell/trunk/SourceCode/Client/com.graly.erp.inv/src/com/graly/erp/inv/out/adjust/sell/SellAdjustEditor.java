package com.graly.erp.inv.out.adjust.sell;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.inv.out.adjust.AdjustOutEntryPage;
import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class SellAdjustEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.out.adjust.sell.SellAdjustEditor";
	
	protected IFormPage page;
	
	public SellAdjustEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new SellAdjustPage(this, "", "");
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
