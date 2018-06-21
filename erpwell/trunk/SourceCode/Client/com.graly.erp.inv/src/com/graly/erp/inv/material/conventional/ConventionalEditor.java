package com.graly.erp.inv.material.conventional;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

/**
 * 责任人：小谢
 * 需求：常规商品需求警报
 * 只处理小谢提供的商品物料清单
 * */
public class ConventionalEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.material.conventional.ConventionalEditor";
	
	protected IFormPage page;
	

	@Override
	protected void addPages() {
		try {
			page = new ConventionalEntryPage(this, "", "");
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
