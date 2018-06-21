package com.graly.erp.bj.inv.outother;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 备件出库功能
 * 该功能与开能的其他出库功能一致。但存在一些客制化的东西
 * */
public class BJOtherOutEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.bj.inv.outother.BJOtherOutEditor";
	
	protected IFormPage page;
	
	public BJOtherOutEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new BJOtherOutEntryPage(this, "", "");
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
