package com.graly.erp.wip.calendarday;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.framework.base.entitymanager.editor.EntityEditor;

public class WipCalendarDayEditor extends EntityEditor{
	public static final String EDITOR_ID = "com.graly.erp.wip.calendarday.CalendarDayEditor";
	protected IFormPage page;
	
	@Override
	protected void addPages() {
		try {
			page = new WipCalendarDayEntryPage(this, "", "");
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
