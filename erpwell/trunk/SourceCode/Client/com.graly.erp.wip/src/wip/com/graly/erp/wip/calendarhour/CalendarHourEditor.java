package com.graly.erp.wip.calendarhour;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class CalendarHourEditor extends EntityEditor {
	public static final String EDITOR_ID = "com.graly.erp.wip.calendarhour.CalendarHourEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new CalendarHourEntryPage(this, "", "");
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
