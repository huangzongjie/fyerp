package com.graly.erp.inv.workshop.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

//车间出入库统计报表
public class WorkShopQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.inv.workshop.query.WorkShopQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new WorkShopQueryEntryPage(this, "", "");
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
