package com.graly.alm.history;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class AlarmHisEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.alm.history.AlarmHisEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new AlarmHisEntryPage(this, "", "");
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
