package com.graly.erp.wip.workcenter.schedule.query;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

//车间出入库统计报表
public class WorkShopScheduleQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.schedule.query.WorkShopScheduleQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new WorkShopScheduleQueryEntryPage(this, "", "");
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
