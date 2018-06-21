package com.graly.erp.wip.workcenter.schedule.purchase2;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 *祁 新版外购件库存控制
 *与刘总工作令排程有区别
 *只给物料计划清单直接计算数据
 * */
public class PurchaseMaterialEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.schedule.purchase2.PurchaseMaterialEditor";
	
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
