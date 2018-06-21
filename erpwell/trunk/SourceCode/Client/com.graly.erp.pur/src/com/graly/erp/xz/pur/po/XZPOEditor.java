package com.graly.erp.xz.pur.po;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 行政采购订单(移植于开能环保采购订单功能)
 * 只有审核功能无需其他功能
 * 
 * */
public class XZPOEditor extends EntityEditor {
public static final String EDITOR_ID = "com.graly.erp.xz.pur.po.XZPOEditor";
	
	protected IFormPage page;
	
	public XZPOEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new XZPOEntryPage(this, "", "");
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
