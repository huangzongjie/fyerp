package com.graly.erp.wip.mo.wms.out;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;
//WMS生产入库详细界面
public class MoWmsOutEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.mo.wms.out.MoWmsOutEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new MoWmsOutPage(this, "", "");
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
