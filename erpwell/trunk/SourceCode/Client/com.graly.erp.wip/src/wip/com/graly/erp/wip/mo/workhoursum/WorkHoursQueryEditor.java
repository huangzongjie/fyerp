package com.graly.erp.wip.mo.workhoursum;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WorkHoursQueryEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.wip.mo.workhoursum.WorkHoursQueryEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new WorkHoursQueryEntryPage(this, "", "");
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
