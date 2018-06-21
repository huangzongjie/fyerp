package com.graly.erp.xz.inv.outother;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 行政领用管理
 * 该功能与开能的其他出库功能一致。但存在一些客制化的东西
 * */
public class XZOtherOutEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.xz.inv.outother.XZOtherOutEditor";
	
	protected IFormPage page;
	
	public XZOtherOutEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new XZOtherOutEntryPage(this, "", "");
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
