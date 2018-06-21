package com.graly.erp.bj.inv.in.pur;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 备件采购入库：
 * 顾洁要求跟开能一样，但是审核时的价格保存2位数字
 * com.graly.erp.bj.inv.in.BJInEditor该类可以废除
 * */
public class BJPurInEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.bj.inv.in.pur.BJPurInEditor";
	
	protected IFormPage page;
	
	public BJPurInEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new BJPurInEntryPage(this, "", "");
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