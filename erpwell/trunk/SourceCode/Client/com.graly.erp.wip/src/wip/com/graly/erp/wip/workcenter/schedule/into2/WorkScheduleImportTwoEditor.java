package com.graly.erp.wip.workcenter.schedule.into2;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

//车间排程导入--包名为into因为import是关键字不能用
//主要功能：根据不同车间不同日期导入
//
public class WorkScheduleImportTwoEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.schedule.into2.WorkScheduleImportTwoEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new WorkScheduleImportTwoEntryPage(this, "", "");
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
