package com.graly.alm.alarm;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class AlarmEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.alm.alarm.AlarmEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new AlarmEntryPage(this, "", "");
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
