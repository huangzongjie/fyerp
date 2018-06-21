package com.graly.erp.wip.workcenter;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WorkCenterEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter.WorkCenterEditor";
	
	protected IFormPage page;
	
	public WorkCenterEditor(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new WorkCenterEntryPage(this, "", "");
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
