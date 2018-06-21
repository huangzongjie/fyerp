package com.graly.erp.wip.workcenter2;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WorkCenterEditor2 extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.workcenter2.WorkCenterEditor2";
	
	protected IFormPage page;
	
	public WorkCenterEditor2(){
	}
	
	@Override
	protected void addPages() {
		try {
			page = new WorkCenterEntryPage2(this, "", "");
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
